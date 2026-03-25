package com.example.demo.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.OverDueDto;
import com.example.demo.entity.Fine;
import com.example.demo.exception.FineAlreadyExistedByLoanIdException;
import com.example.demo.exception.FineNotFoundException;
import com.example.demo.feignclient.LoanClient;
import com.example.demo.repository.FineRepository;
import com.example.demo.service.FineService;
import com.example.demo.utility.FineStatus;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FineServiceImpl implements FineService {
	@Autowired
	private FineRepository fineRepository;
	@Autowired
	private LoanClient loanClient;

	@Override
	public Fine createFineForLoan(Long loanId) {
		log.debug("Creating fine for loanId: {}", loanId);
		Optional<Fine> byLoanId = fineRepository.findByLoanId(loanId);
		if (byLoanId.isPresent()) {
			log.warn("Fine already exists for loanId: {}", loanId);
			throw new FineAlreadyExistedByLoanIdException("Loan Alredy Existed ..");
		}
		log.debug("Fetching overdue data for loanId: {}", loanId);
		OverDueDto overDue = loanClient.checkOverDue(loanId);
		log.info("Fine created successfully for loanId: {}", loanId);
		Fine fine = new Fine();
		fine.setAmount(overDue.getTotalAmount());
		fine.setOverDueSince(LocalDate.now().minusDays(overDue.getOverDueDays()));
		fine.setFineStatus(overDue.getTotalAmount()==0D?FineStatus.NO_FINE.name():FineStatus.PENDING.name());
		fine.setLoanId(loanId);
		Fine save = fineRepository.save(fine);
		return save;
	}

	@Override
	@Cacheable(value = "fines", key = "'all'")
	public List<Fine> getAllFines() {
		log.debug("Fetching all fines");
		List<Fine> all = fineRepository.findAll();
		if (all.isEmpty()) {
			log.warn("No fines found in database");
			throw new FineNotFoundException("No Fines ");
		}
		log.info("Retrieved {} fines total", all.size());
		return all;
	}

	@Override
	@Cacheable(value = "fines",key = "'allpendingloans'")
	public List<Fine> getAllPendingFine() {
		log.debug("Fetching all pending fines");
		List<Fine> byFineStatus = fineRepository.findByFineStatus(FineStatus.PENDING.name());
		if (byFineStatus.isEmpty()) {
			log.warn("No pending fines found");
			throw new FineNotFoundException("No Pending Fine are available");
		}
		log.info("Retrieved {} pending fines", byFineStatus.size());
		return byFineStatus;
	}

	@Override
	@CircuitBreaker(name = "dailyFines", fallbackMethod = "fallbackProcessDailyFines")
	@Scheduled(cron = "0  0 7 * * *")
	@CacheEvict(value = "fines",key = "'allfines'")
	public void processDailyFines() {
		log.info("Starting daily fines processing");
		List<Fine> allFines = fineRepository.findAll();
		for (Fine fine : allFines) {
			Long loanId = fine.getLoanId();
			if (loanId == null) {
				log.warn("Skipping fine {} with null loanId", fine.getFineId());
				continue;
			}
			log.debug("Processing fine for loanId: {}", loanId);
			OverDueDto checkOverDue = loanClient.checkOverDue(loanId);
			if (checkOverDue != null && checkOverDue.getTotalAmount() > 0) {
				log.info("OverDue fine Processed : LoanID : {}, Amount : {}", fine.getLoanId(),
						checkOverDue.getTotalAmount());
				if (fine.getAmount() < checkOverDue.getTotalAmount()) {
					log.info("Updated fine amount for loanId: {} to {}", loanId, checkOverDue.getTotalAmount());
					fine.setAmount(checkOverDue.getTotalAmount());
					fineRepository.save(fine);
				}
			}
		}
		log.info("Daily Fine Processing completed.");
	}

	public void fallbackProcessDailyFines(Throwable t) {
		log.warn("Daily fines processing fallback: loanClient unavailable - skipping overdue checks. {}", t.getMessage());
		log.info("Daily Fine Processing completed with fallback (local fines only)");
	}
}

package com.example.demo.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.OverDueDto;
import com.example.demo.entity.Fine;
import com.example.demo.exception.FineNotFoundException;
import com.example.demo.feignclient.LoanClient;
import com.example.demo.repository.FineRepository;
import com.example.demo.service.FineService;
import com.example.demo.utility.FineStatus;

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
		OverDueDto overDue = loanClient.checkOverDue(loanId);
		Fine fine = new Fine();
		fine.setAmount(overDue.getTotalAmount());
		fine.setOverDueSince(LocalDate.now().minusDays(overDue.getOverDueDays()));
		fine.setFineStatus(FineStatus.PENDING.name());
		fine.setLoanId(loanId);
		Fine save = fineRepository.save(fine);
		return save;
	}

	@Override
	public List<Fine> getAllFines() {
		List<Fine> all = fineRepository.findAll();
		if (all.isEmpty()) {
			throw new FineNotFoundException("No Fines ");
		}
		return all;
	}

	@Override
	public List<Fine> getAllPendingFine() {
		List<Fine> byFineStatus = fineRepository.findByFineStatus(FineStatus.PENDING.name());
		if (byFineStatus.isEmpty()) {
			throw new FineNotFoundException("No Pending Fine are available");
		}
		return byFineStatus;
	}

	@Override
	@Scheduled(cron = "0  0 7 * * *")
	public void processDailyFines() {
		List<Fine> allFines = fineRepository.findAll();
		for (Fine fine : allFines) {
			Long loanId = fine.getLoanId();
			if (loanId == null) {
				log.warn("Skipping fine {} with null loanId", fine.getFineId());
				continue;
			}
			OverDueDto checkOverDue = loanClient.checkOverDue(loanId);
			if (checkOverDue != null && checkOverDue.getTotalAmount() > 0) {
				log.info("OverDue fine Processed : LoanID : {}, Amount : {}", fine.getLoanId(),
						checkOverDue.getTotalAmount());
				if (fine.getAmount() < checkOverDue.getTotalAmount()) {
					fine.setAmount(checkOverDue.getTotalAmount());
					fineRepository.save(fine);
				}
			}
		}
		log.info("Daily Fine Processing completed..");
	}
}

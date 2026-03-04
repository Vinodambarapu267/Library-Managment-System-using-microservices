package com.example.demo.serviceImpl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.dto.OverDueDto;
import com.example.demo.entity.Fine;
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
		if (overDue.getOverDueDays() > 0) {
			Fine fine = new Fine();
			fine.setAmount(overDue.getTotalAmount());
			fine.setOverDueSince(LocalDate.now().minusDays(overDue.getOverDueDays()));
			fine.setFineStatus(FineStatus.PENDING.name());
			Fine save = fineRepository.save(fine);
			return save;
		}
		throw new RuntimeException("No fine required - loan not overdue");
	}

	@Override
	public List<Fine> getAllFines() {
		List<Fine> all = fineRepository.findAll();
		if (all.isEmpty()) {
			throw new RuntimeException("No Fines ");
		}
		return all;
	}

	@Override
	public List<Fine> getAllPendingFine() {
		List<Fine> byFineStatus = fineRepository.findByFineStatus(FineStatus.PENDING.name());
		if (byFineStatus.isEmpty()) {
			throw new RuntimeException("No Pending Fine are available");
		}
		return byFineStatus;
	}
	@Value("${app.scheduled.loan-id:1}")  // From application.yml
	private Long scheduledLoanId;
	@Override
	@Scheduled(cron = "0 0 10 * * *")
	public void processDailyFines() {
		OverDueDto checkOverDue = loanClient.checkOverDue(scheduledLoanId);
		Double totalAmount = checkOverDue.getTotalAmount();
		log.error("You Need to Pay amount for the OverDue for taking book : "+totalAmount);
	}
}

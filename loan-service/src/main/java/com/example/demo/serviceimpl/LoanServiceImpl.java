package com.example.demo.serviceimpl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BorrowRequest;
import com.example.demo.dto.OverDueDto;
import com.example.demo.dto.UserRoleStatus;
import com.example.demo.entity.Loan;
import com.example.demo.exceptions.BookAlreadyReturnedException;
import com.example.demo.exceptions.BookCopiesNotAvailableException;
import com.example.demo.exceptions.BookNotReturnedException;
import com.example.demo.exceptions.BorrowBooksNotFoundException;
import com.example.demo.exceptions.LoanNotFoundException;
import com.example.demo.exceptions.UserNotAuthorizedException;
import com.example.demo.feignClient.BookClient;
import com.example.demo.feignClient.UserClient;
import com.example.demo.repository.LoanRepository;
import com.example.demo.service.LoanService;
import com.example.demo.utility.LoanStatus;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Service
@Transactional
@Slf4j
public class LoanServiceImpl implements LoanService {

	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private BookClient bookClient;
	@Autowired
	private UserClient userClient;

	@Override
	public Loan borrowBook(BorrowRequest borrowRequest) {
		log.debug("Borrow book request: userId={}, title='{}'", borrowRequest.getUserId(), borrowRequest.getTitle());
		
		List<Loan> availableLoans = loanRepository.findAllByUserIdAndStatus(borrowRequest.getUserId(),
				LoanStatus.BORROWED.name());
		for (Loan existedloan : availableLoans) {
			if (existedloan.getTitle().equals(existedloan.getTitle())) {
				log.warn("User has pending book: {}", existedloan.getTitle());
				throw new BookNotReturnedException("First return borrowed Book : " + existedloan.getTitle());
			}
		}
		log.debug("Checking user borrow eligibility: userId={}", borrowRequest.getUserId());
		UserRoleStatus checkStatus = userClient.checkStatus(borrowRequest.getUserId());

		if (!checkStatus.isAllowToBorrow()) {
			log.warn("User not authorized to borrow: userId={}", borrowRequest.getUserId());
			throw new UserNotAuthorizedException("User not Authorized to borrowing");
		}
		log.debug("Checking book availability: title='{}'", borrowRequest.getTitle());
		Integer copiesAvailable = bookClient.checkAvailability(borrowRequest.getTitle());

		if (copiesAvailable <= 1) {
			log.warn("Book not available: title='{}', copies={}", borrowRequest.getTitle(), copiesAvailable);
			throw new BookCopiesNotAvailableException("Book Not Available: Only " + copiesAvailable + " copy left");
		}
		log.info("Creating new loan: userId={}, title='{}'", borrowRequest.getUserId(), borrowRequest.getTitle());
		Loan loan = new Loan();
		loan.setTitle(borrowRequest.getTitle());
		loan.setUserId(borrowRequest.getUserId());
		loan.setBorrowAt(LocalDate.now());
		loan.setDueDate(LocalDate.now().plusDays(5));
		loan.setStatus(LoanStatus.BORROWED.name());
		Loan save = loanRepository.save(loan);
		Integer newCopies = copiesAvailable - 1;
		bookClient.updateBookCopies(borrowRequest.getTitle(), newCopies);
		log.info("Book borrowed successfully: loanId={}", save.getLoanId());
		return save;
	}

	public Loan returnBook(Long loanId) {
		log.debug("Return book request: loanId={}", loanId);
		Loan existedLoan = loanRepository.findById(loanId)
				.orElseThrow(() -> {
					log.warn("Loan not found: loanId={}", loanId);
					return new LoanNotFoundException("Loan not found");
				});
		if (existedLoan.getStatus().equals(LoanStatus.RETURNED.name())) {
			log.warn("Book already returned: loanId={}, title='{}'", loanId, existedLoan.getTitle());
			throw new BookAlreadyReturnedException(" Already Returned the book : " + existedLoan.getTitle());
		}

		log.debug("Checking availability before return: title='{}'", existedLoan.getTitle());
		Integer copiesAvailable = bookClient.checkAvailability(existedLoan.getTitle());

		existedLoan.setReturnredAt(LocalDate.now());
		existedLoan.setStatus(LoanStatus.RETURNED.name());
		existedLoan.setTitle(existedLoan.getTitle());

		Loan returned = loanRepository.save(existedLoan);
		Integer newCopies = copiesAvailable + 1;
		bookClient.updateBookCopies(existedLoan.getTitle(), newCopies);
		log.info("Book returned successfully: loanId={}", loanId);
		return returned;
	}

	public List<Loan> findAll() {
		log.debug("Fetching all loans");
		List<Loan> all = loanRepository.findAll();
		if (all.isEmpty()) {
			log.warn("No loans found in database");
			throw new LoanNotFoundException("No Loan in Databae");
		}
		log.info("Retrieved {} loans", all.size());
		return all;
	}

	public List<Loan> findByUserId(Long userId) {
		log.debug("Fetching loans for userId: {}", userId);
		Loan user = loanRepository.findByUserId(userId)
				.orElseThrow(() -> {
					log.warn("User not found: userId={}", userId);
					return new UserNotAuthorizedException("User Not Found");
				});

		List<Loan> allByUserId = loanRepository.findAllByUserId(user.getUserId());
		if (allByUserId.isEmpty()) {
			log.warn("No borrowed books found for userId: {}", userId);
			throw new BorrowBooksNotFoundException("No Books borrowed by this User : " + userId);
		}
		log.info("Retrieved {} loans for userId: {}", allByUserId.size(), userId);
		return allByUserId;
	}

	@Override
	public OverDueDto calculateOverDue(Long loanId) {
		log.debug("Calculating overdue for loanId: {}", loanId);
		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> {
			log.warn("Loan not found for overdue calculation: loanId={}", loanId);
			return new LoanNotFoundException("Loan Not Found");
		});

		LocalDate dueDate = loan.getDueDate();
		long rawDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
		long daysOverDue = Math.max(0, rawDays);
		Double principal = loan.getTotalAmount() == null ? 0.0 : 0.0;
		Double fineAmount = principal + 2 * daysOverDue;

		loan.setTotalAmount(fineAmount);
		loanRepository.save(loan);
		log.info("Overdue calculated: loanId={}, daysOverdue={}, fine=${}", loanId, daysOverDue, fineAmount);
		return new OverDueDto(loanId, (int) daysOverDue, fineAmount);
	}
}

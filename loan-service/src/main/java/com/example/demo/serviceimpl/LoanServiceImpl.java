package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BorrowRequest;
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

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private BookClient bookClient;
	@Autowired
	private UserClient userClient;

	public Loan borrowBook(BorrowRequest borrowRequest) {

		List<Loan> availableLoans = loanRepository.findAllByUserIdAndStatus(borrowRequest.getUserId(),
				LoanStatus.BORROWED.name());
		for (Loan existedloan : availableLoans) {
			if (existedloan.getTitle().equals(existedloan.getTitle())) {
				throw new BookNotReturnedException("First return borrowed Book : " + existedloan.getTitle());
			}
		}
		UserRoleStatus checkStatus = userClient.checkStatus(borrowRequest.getUserId());

		if (!checkStatus.isAllowToBorrow()) {
			throw new UserNotAuthorizedException("User not Authorized to borrowing");
		}
		Integer copiesAvailable = bookClient.checkAvailability(borrowRequest.getTitle());

		if (copiesAvailable <= 1) {
			throw new BookCopiesNotAvailableException("Book Not Available: Only " + copiesAvailable + " copy left");
		}
		Loan loan = new Loan();
		loan.setTitle(borrowRequest.getTitle());
		loan.setUserId(borrowRequest.getUserId());
		loan.setBorrowAt(LocalDateTime.now());
		loan.setDueDate(LocalDateTime.now().plusDays(5));
		loan.setStatus(LoanStatus.BORROWED.name());
		Loan save = loanRepository.save(loan);
		Integer newCopies = copiesAvailable - 1;
		bookClient.updateBookCopies(borrowRequest.getTitle(), newCopies);
		return save;
	}

	public Loan returnBook(Long loanId) {
		Loan existedLoan = loanRepository.findById(loanId)
				.orElseThrow(() -> new LoanNotFoundException("Loan not found"));
		if (existedLoan.getStatus().equals(LoanStatus.RETURNED.name())) {
			throw new BookAlreadyReturnedException(" Already Returned the book : " + existedLoan.getTitle());
		}

		Integer copiesAvailable = bookClient.checkAvailability(existedLoan.getTitle());

		existedLoan.setReturnredAt(LocalDateTime.now());
		existedLoan.setStatus(LoanStatus.RETURNED.name());
		existedLoan.setTitle(existedLoan.getTitle());

		Loan returned = loanRepository.save(existedLoan);

		Integer newCopies = copiesAvailable + 1;
		bookClient.updateBookCopies(existedLoan.getTitle(), newCopies);
		return returned;
	}

	public List<Loan> findAll() {
		List<Loan> all = loanRepository.findAll();
		if (all.isEmpty()) {
			throw new LoanNotFoundException("No Loan in Databae");
		}
		return all;
	}

	public List<Loan> findByUserId(Long userId) {
		Loan user = loanRepository.findByUserId(userId)
				.orElseThrow(() -> new UserNotAuthorizedException("User Not Found"));

		List<Loan> allByUserId = loanRepository.findAllByUserId(user.getUserId());

		if (allByUserId.isEmpty()) {
			throw new BorrowBooksNotFoundException("No Books borrowed by this User : " + userId);
		}
		return allByUserId;
	}
}

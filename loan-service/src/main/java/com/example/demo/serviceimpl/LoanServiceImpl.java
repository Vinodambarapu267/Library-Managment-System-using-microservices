package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BorrowRequest;
import com.example.demo.dto.UserRoleStatus;
import com.example.demo.entity.Loan;
import com.example.demo.feignClient.BookClient;
import com.example.demo.feignClient.UserClient;
import com.example.demo.repository.LoanRepository;
import com.example.demo.utility.LoanStatus;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LoanServiceImpl {
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
				throw new RuntimeException("First Return borrowed Book");
			}
		}
		UserRoleStatus checkStatus = userClient.checkStatus(borrowRequest.getUserId());

		if (!checkStatus.isAllowToBorrow()) {
			throw new RuntimeException("User not Authorized to borrowing");
		}
		Integer copiesAvailable = bookClient.checkAvailability(borrowRequest.getTitle());

		if (copiesAvailable <= 1) {
			throw new RuntimeException("Book Not Available: Only " + copiesAvailable + " copy left");
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
		Loan existedLoan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
		if (existedLoan.getStatus().equals(LoanStatus.RETURNED.name())) {
			throw new RuntimeException(" Already Returned the book : " + existedLoan.getTitle());
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
			throw new RuntimeException("No Loan in Databae");
		}
		return all;
	}

	public List<Loan> findByUserId(Long userId) {
		List<Loan> allByUserId = loanRepository.findAllByUserId(userId);

		if (allByUserId.isEmpty()) {
			throw new RuntimeException("No Books borrowed by this User : " + userId);
		}
		return allByUserId;
	}
}

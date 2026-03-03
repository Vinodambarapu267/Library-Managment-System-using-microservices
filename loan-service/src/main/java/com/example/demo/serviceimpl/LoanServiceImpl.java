package com.example.demo.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BookAvailability;
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
		UserRoleStatus checkStatus = userClient.checkStatus(borrowRequest.getUserId());
		if (!checkStatus.isAllowToBorrow()) {
			throw new RuntimeException("User not Authorized to borrowing");
		}
		BookAvailability availability = bookClient.checkAvailability(borrowRequest.getTitle());
		System.out.println(availability);
		if (!availability.canBorrow()) {
			throw new RuntimeException("Book Not Available: " + availability.getCopiesAvailable() + "Copies Left");
		}
		Loan loan = new Loan();
		loan.setTitle(borrowRequest.getTitle());
		loan.setUserId(borrowRequest.getUserId());
		loan.setBorrowAt(LocalDateTime.now());
		loan.setDueDate(LocalDateTime.now().plusDays(5));
		loan.setStatus(LoanStatus.BORROWED.name());
		Loan save = loanRepository.save(loan);
		Integer newCopies = availability.getCopiesAvailable() - 1;
		bookClient.updateBookCopies(borrowRequest.getTitle(), newCopies);
		return save;
	}

	public Loan returnBook(Long loanId) {
	Loan existedLoan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));
	if (existedLoan.getStatus() == LoanStatus.RETURNED.name()) {
		throw new RuntimeException(" Already Returned the book : " + existedLoan.getTitle());
	}
	UserRoleStatus checkStatus = userClient.checkStatus(existedLoan.getUserId());
	if (!checkStatus.isAllowToBorrow()) {
		throw new RuntimeException("User not Authorized to borrowing");
	}
	BookAvailability availability = bookClient.checkAvailability(existedLoan.getTitle());
	System.out.println(availability);
	if (!availability.canBorrow()) {
		throw new RuntimeException("Book Not Available: " + availability.getCopiesAvailable() + "Copies Left");
	}
	existedLoan.setReturnredAt(LocalDateTime.now().plusDays(2));
	existedLoan.setStatus(LoanStatus.RETURNED.name());
	existedLoan.setTitle(existedLoan.getTitle());
	Loan returned = loanRepository.save(existedLoan);
	Integer copiesAvailable = availability.getCopiesAvailable() + 1;
	bookClient.updateBookCopies(existedLoan.getTitle(), copiesAvailable);
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

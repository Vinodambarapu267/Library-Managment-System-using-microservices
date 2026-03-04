package com.example.demo.controller;

import java.net.HttpURLConnection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BorrowRequest;
import com.example.demo.dto.OverDueDto;
import com.example.demo.entity.Loan;
import com.example.demo.service.LoanService;
import com.example.demo.utility.ResponseMessage;
import com.example.demo.utility.ResponseStatus;

@RestController
@RequestMapping("/api/loan")
public class LoanController {
	@Autowired
	private LoanService loanService;

	@PostMapping("/borrowbook")
	public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest borrowRequest) {
		Loan borrowBook = loanService.borrowBook(borrowRequest);
		if (borrowBook != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					" book Borrow Successfully", borrowBook));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "book Borrow is Failed"));
		}
	}

	@PutMapping("/returnbook/{loanId}")
	public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
		Loan returnBook = loanService.returnBook(loanId);
		if (returnBook != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"returned SuccessFully", returnBook));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Return Failed"));
		}
	}

	@GetMapping("/{userId}/Loans")
	public ResponseEntity<?> findAllById(@PathVariable Long userId) {
		List<Loan> byUserId = loanService.findByUserId(userId);

		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
				"Borrowed Books under this User ID : " + userId, byUserId));
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		List<Loan> all = loanService.findAll();

		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
				"Retrived All Loans ", all));
	}

	@PostMapping("/{loanId}/overdue")
	public OverDueDto checkOverDue(@PathVariable Long loanId) {
		OverDueDto calculateOverDue = loanService.calculateOverDue(loanId);
		return calculateOverDue;
	}
}

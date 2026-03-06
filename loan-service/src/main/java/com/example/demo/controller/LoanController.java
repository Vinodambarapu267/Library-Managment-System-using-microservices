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

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/loan")
@Slf4j
public class LoanController {
	@Autowired
	private LoanService loanService;

	@PostMapping("/borrowbook")
	public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest borrowRequest) {
		log.debug("Received borrow request: userId={}, title='{}'", 
		          borrowRequest.getUserId(), borrowRequest.getTitle());
		
		Loan borrowBook = loanService.borrowBook(borrowRequest);
		if (borrowBook != null) {
			log.info("Book borrowed successfully: loanId={}", borrowBook.getLoanId());
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book borrowed successfully", borrowBook));
		} else {
			log.warn("Book borrow failed: userId={}, title='{}'", 
			       borrowRequest.getUserId(), borrowRequest.getTitle());
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Book borrow failed"));
		}
	}

	@PutMapping("/returnbook/{loanId}")
	public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
		log.debug("Received return request: loanId={}", loanId);
		
		Loan returnBook = loanService.returnBook(loanId);
		if (returnBook != null) {
			log.info("Book returned successfully: loanId={}", loanId);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_OK, ResponseStatus.SUCCESS.name(),
					"Book returned successfully", returnBook));
		} else {
			log.warn("Book return failed: loanId={}", loanId);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Book return failed"));
		}
	}

	@GetMapping("/{userId}/Loans")
	public ResponseEntity<?> findAllById(@PathVariable Long userId) {
		log.debug("Fetching loans for userId: {}", userId);
		
		List<Loan> byUserId = loanService.findByUserId(userId);
		log.info("Retrieved {} loans for userId: {}", byUserId.size(), userId);

		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_OK, ResponseStatus.SUCCESS.name(),
				"Borrowed books for user ID: " + userId, byUserId));
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		log.debug("Fetching all loans");
		
		List<Loan> all = loanService.findAll();
		log.info("Retrieved {} loans total", all.size());

		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_OK, ResponseStatus.SUCCESS.name(),
				"All loans retrieved", all));
	}

	@PostMapping("/{loanId}/overdue")
	public ResponseEntity<?> checkOverDue(@PathVariable Long loanId) {
		log.debug("Checking overdue for loanId: {}", loanId);
		
		OverDueDto calculateOverDue = loanService.calculateOverDue(loanId);
		log.info("Overdue calculated for loanId: {}, days={}, fine=${}", 
		         loanId, calculateOverDue.getOverDueDays(), calculateOverDue.getTotalAmount());
		
		return ResponseEntity.ok(calculateOverDue);
	}
}

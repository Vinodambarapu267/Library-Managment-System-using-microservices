package com.example.demo.controller;

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
import com.example.demo.entity.Loan;
import com.example.demo.serviceimpl.LoanServiceImpl;

@RestController
@RequestMapping("/api/loan")
public class LoanController {
	@Autowired
	private LoanServiceImpl loanServiceImpl;

	@PostMapping("/borrowbook")
	public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest borrowRequest) {
		Loan borrowBook = loanServiceImpl.borrowBook(borrowRequest);
		return ResponseEntity.ok(borrowBook);
	}
	@PutMapping("/returnbook/{loanId}")
	public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
		Loan returnBook = loanServiceImpl.returnBook(loanId);
		return ResponseEntity.ok(returnBook);
	}

	@GetMapping("/{userId}/Loans")
	public ResponseEntity<?> findAllById(@PathVariable Long userId) {
		List<Loan> byUserId = loanServiceImpl.findByUserId(userId);
		return ResponseEntity.ok(byUserId);
	}
	@GetMapping
	public ResponseEntity<?> findAll() {
		List<Loan> all = loanServiceImpl.findAll();
		return ResponseEntity.ok(all);
	}

}

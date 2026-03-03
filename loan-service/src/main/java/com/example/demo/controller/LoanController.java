package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

}

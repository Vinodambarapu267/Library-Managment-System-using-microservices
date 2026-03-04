package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.BorrowRequest;
import com.example.demo.dto.OverDueDto;
import com.example.demo.entity.Loan;

@Service
public interface LoanService {
	public Loan borrowBook(BorrowRequest borrowRequest);

	public Loan returnBook(Long loanId);

	public List<Loan> findAll();

	public List<Loan> findByUserId(Long userId);

	public OverDueDto calculateOverDue(Long loanId);
}

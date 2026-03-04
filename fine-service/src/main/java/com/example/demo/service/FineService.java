package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Fine;

public interface FineService {
	public Fine createFineForLoan(Long loanId);

	public List<Fine> getAllFines();

	public List<Fine> getAllPendingFine();

	public void processDailyFines();

}

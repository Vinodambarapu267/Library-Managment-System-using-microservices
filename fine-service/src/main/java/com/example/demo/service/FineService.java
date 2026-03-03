package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Fine;

public interface FineService {
	public Fine createFineForLoan(Fine fine);

	public List<Fine> getAllFines();

	public List<Fine> getAllPendingFine(String pending);

	public Double calculateFineAmount(int daysOverDue, Double principal);
}

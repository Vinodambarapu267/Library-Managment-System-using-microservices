package com.example.demo.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Fine;
import com.example.demo.repository.FineRepository;
import com.example.demo.service.FineService;

@Service
public class FineServiceImpl implements FineService {
	@Autowired
	private FineRepository fineRepository;

	@Override
	public Fine createFineForLoan(Fine fine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Fine> getAllFines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Fine> getAllPendingFine(String pending) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double calculateFineAmount(int daysOverDue, Double principal) {
		// TODO Auto-generated method stub
		return null;
	}

}

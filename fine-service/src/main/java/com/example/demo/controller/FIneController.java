package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Fine;
import com.example.demo.service.FineService;

@RestController
@RequestMapping("/api/fines")
public class FIneController {
	@Autowired
	private FineService fineService;

	@PostMapping("/createfine")
	public ResponseEntity<?> createFine(@RequestParam Long loanId) {
		Fine fineForLoan = fineService.createFineForLoan(loanId);
		return ResponseEntity.ok(fineForLoan);

	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		List<Fine> allFines = fineService.getAllFines();
		return ResponseEntity.ok(allFines);
	}

	@GetMapping("/getallpendingfines")
	public ResponseEntity<?> getPendingFines() {
		List<Fine> allPendingFine = fineService.getAllPendingFine();

		return ResponseEntity.ok(allPendingFine);
	}
}

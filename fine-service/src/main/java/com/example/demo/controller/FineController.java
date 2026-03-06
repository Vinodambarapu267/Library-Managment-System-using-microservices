package com.example.demo.controller;

import java.net.HttpURLConnection;
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
import com.example.demo.utility.ResponseMessage;
import com.example.demo.utility.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/fines")
@Slf4j
public class FineController {
	@Autowired
	private FineService fineService;

	@PostMapping("/createfine")
	public ResponseEntity<?> createFine(@RequestParam Long loanId) {
		log.debug("Received create fine request: loanId={}", loanId);
		
		Fine fineForLoan = fineService.createFineForLoan(loanId);
		if (fineForLoan != null) {
			log.info("Fine created successfully: loanId={}", loanId);
			return ResponseEntity.ok(new ResponseMessage(fineForLoan, HttpURLConnection.HTTP_CREATED,
					ResponseStatus.SUCCESS.name(), "Fine details added successfully"));
		} else {
			log.warn("Fine creation failed: loanId={}", loanId);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Internal server error"));
		}
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		log.debug("Fetching all fines");
		
		List<Fine> allFines = fineService.getAllFines();
		if (allFines != null) {
			log.info("Retrieved {} fines total", allFines.size());
			return ResponseEntity.ok(new ResponseMessage(allFines, HttpURLConnection.HTTP_OK,
					ResponseStatus.SUCCESS.name(), "All fines retrieved successfully"));
		} else {
			log.warn("No fines found");
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Internal server error"));
		}
	}

	@GetMapping("/getallpendingfines")
	public ResponseEntity<?> getPendingFines() {
		log.debug("Fetching all pending fines");
		
		List<Fine> allPendingFine = fineService.getAllPendingFine();
		if (allPendingFine != null) {
			log.info("Retrieved {} pending fines", allPendingFine.size());
			return ResponseEntity.ok(new ResponseMessage(allPendingFine, HttpURLConnection.HTTP_OK,
					ResponseStatus.SUCCESS.name(), "Pending fines retrieved successfully"));
		} else {
			log.warn("No pending fines found");
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Internal server error"));
		}
	}
}

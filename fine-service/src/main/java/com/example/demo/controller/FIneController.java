

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

@RestController
@RequestMapping("/api/fines")
public class FIneController {
	@Autowired
	private FineService fineService;

	@PostMapping("/createfine")
	public ResponseEntity<?> createFine(@RequestParam Long loanId) {
		Fine fineForLoan = fineService.createFineForLoan(loanId);
		if (fineForLoan != null) {
			return ResponseEntity.ok(new ResponseMessage(fineForLoan, HttpURLConnection.HTTP_CREATED,
					ResponseStatus.SUCCESS.name(), "Fine details added Successfully"));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Internale Server Error"));
		}
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		List<Fine> allFines = fineService.getAllFines();
		if (allFines != null) {
			return ResponseEntity.ok(new ResponseMessage(allFines, HttpURLConnection.HTTP_CREATED,
					ResponseStatus.SUCCESS.name(), "Fine details added Successfully"));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Internale Server Error"));
		}
	}

	@GetMapping("/getallpendingfines")
	public ResponseEntity<?> getPendingFines() {
		List<Fine> allPendingFine = fineService.getAllPendingFine();
		if (allPendingFine != null) {
			return ResponseEntity.ok(new ResponseMessage(allPendingFine, HttpURLConnection.HTTP_CREATED,
					ResponseStatus.SUCCESS.name(), "Fine details added Successfully"));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Internale Server Error"));
		}
	}
}

package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.OverDueDto;

@FeignClient(name="loan-service",url="http://localhost:9092")
public interface LoanClient {
	@GetMapping("/api/loan/{loan-Id}/overdue")
	public OverDueDto checkOverDue(@PathVariable("loan-Id") Long loanId);
}

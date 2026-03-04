package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.dto.OverDueDto;

@FeignClient(name="loan-service",url="http://localhost:9092")
public interface LoanClient {
	@PostMapping("/api/loan/{loanId}/overdue")
	public OverDueDto checkOverDue(@PathVariable Long loanId);
}

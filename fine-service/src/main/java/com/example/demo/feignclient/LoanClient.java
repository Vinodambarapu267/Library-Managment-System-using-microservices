package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="loan-service",url="http://localhost:9092")
public interface LoanClient {
 
}

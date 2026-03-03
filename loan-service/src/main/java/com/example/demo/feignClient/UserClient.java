package com.example.demo.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.UserRoleStatus;

@FeignClient(name = "user-service",url="http://localhost:9091")
public interface UserClient {
	@GetMapping("/api/users/{id}/checkstatus")
	public UserRoleStatus checkStatus(@PathVariable Long id);
}

package com.example.demo.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.demo.dto.BookAvailability;

@FeignClient(name = "book-service",url = "http://localhost:9090")
public interface BookClient {
	@GetMapping("/api/books/{title}/availability")
	public Integer checkAvailability(@PathVariable("title") String title);

	@PutMapping("/api/books/{title}/copies/{newCopies}")
	public BookAvailability updateBookCopies(@PathVariable("title") String title, @PathVariable("newCopies") Integer newCopies);
}

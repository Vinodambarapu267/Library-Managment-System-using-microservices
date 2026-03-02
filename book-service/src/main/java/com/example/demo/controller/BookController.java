package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import com.example.demo.service.BookService;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping("/addbook")
	public ResponseEntity<?> createBook(@RequestBody Book book) {
		Book save = bookService.createBook(book);
		return ResponseEntity.ok(save);

	}

	@PutMapping("/updatebook/{id}")
	public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
		Book updateBook = bookService.updateBook(id, bookDto);
		return ResponseEntity.ok(updateBook);
	}

	@GetMapping("/getbyisbn/")
	public ResponseEntity<?> findByisbn(@RequestParam String isbn) {
		Book byIsbn = bookService.getByIsbn(isbn);
		return ResponseEntity.ok(byIsbn);
	}

	@DeleteMapping("/deletebook/{isbn}")
	public ResponseEntity<?> removeByisbn(@PathVariable String isbn) {
		bookService.deleteBook(isbn);
		return ResponseEntity.ok("deleted successfully");
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		return ResponseEntity.ok(bookService.findAll());
	}
}

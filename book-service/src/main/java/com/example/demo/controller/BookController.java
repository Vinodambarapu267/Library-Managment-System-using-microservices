package com.example.demo.controller;

import java.net.HttpURLConnection;
import java.util.List;

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
import com.example.demo.utility.ResponseMessage;
import com.example.demo.utility.ResponseStatus;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

	@Autowired
	private BookService bookService;

	@PostMapping("/addbook")
	public ResponseEntity<?> createBook(@RequestBody Book book) {
		Book save = bookService.createBook(book);
		if (save != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book Created Successfully", save));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Book Created failed.."));

	}

	@PutMapping("/updatebook/{id}")
	public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
		Book updateBook = bookService.updateBook(id, bookDto);
		if (updateBook != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book updated Successfully", updateBook));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Book updated failed.."));
	}

	@GetMapping("/getbyisbn/")
	public ResponseEntity<?> findByisbn(@RequestParam String isbn) {
		Book byIsbn = bookService.getByIsbn(isbn);
		if (byIsbn != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book retrived Successfully", byIsbn));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Book retrived failed.."));
	}

	@DeleteMapping("/deletebook/{isbn}")
	public ResponseEntity<?> removeByisbn(@PathVariable String isbn) {
		bookService.deleteBook(isbn);
		
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
				"Book deleted succesfully"));
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		List<Book> all = bookService.findAll();
		if (!all.isEmpty()) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"All Book retrived Successfully", all));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Books retrived failed.."));
	}

	@GetMapping("/findbytitle")
	public ResponseEntity<?> findByTitleContainingIgnoreCase(@RequestParam String title) {
		List<Book> byTitleContainingIgnoreCase = bookService.findByTitleContainingIgnoreCase(title);
		if (!byTitleContainingIgnoreCase.isEmpty()) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"All Book retrived Successfully", byTitleContainingIgnoreCase));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Books retrived failed.."));
	}

	@GetMapping("/findbyauthor/{author}")
	public ResponseEntity<?> findByAuthorContainingIgnoreCase(@PathVariable String author) {
		List<Book> byAuthorContainingIgnoreCase = bookService.findByAuthorContainingIgnoreCase(author);
		if (!byAuthorContainingIgnoreCase.isEmpty()) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"All Book retrived Successfully", byAuthorContainingIgnoreCase));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Books retrived failed.."));
	}

	@GetMapping("/totalbooks")
	public Long totalBooksCount() {
		Long totalBooksCount = bookService.totalBooksCount();
		return totalBooksCount;
	}

}

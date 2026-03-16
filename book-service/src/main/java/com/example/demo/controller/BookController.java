package com.example.demo.controller;

import java.net.HttpURLConnection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.example.demo.service.BookCopiesUpdateService;
import com.example.demo.service.BookService;
import com.example.demo.utility.ResponseMessage;
import com.example.demo.utility.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {

	@Autowired
	private BookService bookService;
	@Autowired
	private BookCopiesUpdateService bookCopiesUpdateService;

	@PostMapping("/addbook")
	public ResponseEntity<?> createBook(@RequestBody Book book) {
		log.debug("Received createBook request : titla='{}'", book.getTitle());
		Book save = bookService.createBook(book);
		if (save != null) {
			log.info("Book created successfully, delegating response");
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book Created successfully", save));
		}
		log.warn("Book creation returned null from service for title='{}'", book.getTitle());
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Book Creation failed.."));

	}

	@PutMapping("/updatebook/{id}")
	public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto) {
		log.debug("Received updateBook request: ID='{}'", id);
		Book updateBook = bookService.updateBook(id, bookDto);
		if (updateBook != null) {
			log.info("Book Updated successfully, delegating response");
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book updated Successfully", updateBook));
		}
		log.warn("Book updation return null from service for ID= '{}'", id);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Book updated failed.."));
	}

	@GetMapping("/getbyisbn/")
	public ResponseEntity<?> findByisbn(@RequestParam String isbn) {
		log.debug("Received fetching request : ISBN='{}'", isbn);
		Book byIsbn = bookService.getByIsbn(isbn);
		if (byIsbn != null) {
			log.info("Book found successfully, delegating response");
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Book retrived Successfully", byIsbn));
		}
		log.warn("Book Found return null from service for ISBN= '{}'", isbn);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Book retrived failed.."));
	}

	@DeleteMapping("/deletebook/{isbn}")
	public ResponseEntity<?> removeByisbn(@PathVariable String isbn) {
		log.debug("Received deleting request : ISBN= '{}'", isbn);
		bookService.deleteBook(isbn);
		log.info("Book deleted successfully, delegating response");
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
				"Book deleted succesfully"));
	}

	@GetMapping
	public ResponseEntity<?> findAll() {
		log.debug("Received fetching all books request");
		List<Book> all = bookService.findAll();
		if (!all.isEmpty()) {
			log.info("all books found successfully, delegating response");
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"All Books retrived successfully", all));
		}
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Books retrived failed.."));
	}

	@GetMapping("/findbytitle")
	public ResponseEntity<?> findByTitleContainingIgnoreCase(@RequestParam String title) {
		log.debug("Received search book containing the title request: title ='{}'", title);
		List<Book> byTitleContainingIgnoreCase = bookService.findByTitleContainingIgnoreCase(title);
		if (!byTitleContainingIgnoreCase.isEmpty()) {
			log.info("Found {} books matching title: '{}'", byTitleContainingIgnoreCase.size(), title);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"All Book retrived successfully", byTitleContainingIgnoreCase));
		}
		log.warn("no books found  matching title = '{}'", title);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Books retrived failed.."));
	}

	@GetMapping("/findbyauthor/{author}")
	public ResponseEntity<?> findByAuthorContainingIgnoreCase(@PathVariable String author) {
		log.debug("Received search book by author request: author='{}'", author);
		List<Book> byAuthorContainingIgnoreCase = bookService.findByAuthorContainingIgnoreCase(author);
		if (!byAuthorContainingIgnoreCase.isEmpty()) {
			log.info("Found {} books by author ='{}'", author);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"All Book retrived successfully", byAuthorContainingIgnoreCase));
		}
		log.warn("No books found by author = '{}'", author);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.FAILURE.name(),
				"Books retrived failed.."));
	}

	@GetMapping("/totalbooks")
	public ResponseEntity<?> totalBooksCount() {
		log.debug("Received request for total books count");
		Long totalBooksCount = bookService.totalBooksCount();
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_OK, ResponseStatus.SUCCESS.name(),
				"Total books count retrieved successfully", totalBooksCount));
	}

	// Copies Updating in library

	@PutMapping("/{title}/copies/{newCopies}")
	public ResponseEntity<?> updateCopies(@PathVariable("title") String title,
			@PathVariable("newCopies") int newCopies) {
		log.debug("Received the update copies request : title='{}', new Copies={}", title, newCopies);
		Book updatedBook = bookCopiesUpdateService.updateTotalBookCopies(title, newCopies);
		log.info("Succesfully updated copies for '{}': new copies={}", title, newCopies);
		return ResponseEntity.ok(updatedBook);
	}

	@PostMapping("/{title}/issue")
	public ResponseEntity<?> issueBook(@PathVariable String title) {
		log.debug("Received the issue book request : title='{}'", title);
		Book issuedBook = bookCopiesUpdateService.issueBook(title);
		if (issuedBook != null) {
			log.info("book issued successfully by title = '{}'", title);
			return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), // ✅ 200 OK
					ResponseStatus.SUCCESS.name(), "Book issued successfully", issuedBook));
		}
		log.warn("book issueing return null from service for title='{}'", title);
		return ResponseEntity.ok(new ResponseMessage(HttpStatus.NOT_FOUND.value(), // ✅ 200 OK
				ResponseStatus.FAILURE.name(), "Book issuing failed"));
	}

	@PostMapping("/{title}/return")
	public ResponseEntity<?> returnBook(@PathVariable String title) {
		log.debug("Received the return book request : title='{}'", title);
		Book returnedBook = bookCopiesUpdateService.returnBook(title);
		log.info("book returned successfully by title='{}'", title);
		return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), // ✅ 200 OK
				ResponseStatus.SUCCESS.name(), "Book returned successfully", returnedBook));
	}

	@GetMapping("/{title}/availability")
	public Integer checkAvailability(@PathVariable String title) {
	    log.debug("Received check availability request: title='{}'", title);
	    Integer availability = bookCopiesUpdateService.availabiltyBook(title);  
	    return availability;
	}
}



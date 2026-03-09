package com.example.demo.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Book;
import com.example.demo.exception.BookNotAvailableException;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookCopiesUpdateService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookCopiesUpdateServiceImpl implements BookCopiesUpdateService {
	@Autowired
	private BookRepository bookRepository;

	@Override
	@CachePut(value = "books",key = "#title")
	@CacheEvict(value = "booksList",allEntries = true)
	public Book updateTotalBookCopies(String title, Integer newCopies) {
	    Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found"));
	    
	 
	    if (newCopies < 0 || newCopies > book.getTotalCopies()) {
	        throw new BookNotAvailableException("Invalid copies: " + newCopies + 
	            " (0-" + book.getTotalCopies() + ")");
	    }
	    

	    book.setCopiesAvailable(newCopies);  
	    return bookRepository.save(book);
	}

	@Override
	@CachePut(value = "books",key = "#title")
	public Book issueBook(String title) {
		Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found"));
		if (book.getCopiesAvailable() > 0) {
			book.issueBook();
			return bookRepository.save(book);
		}
		throw new RuntimeException("This book available right now");
	}

	@Override
	@CachePut(value = "books",key = "#title")
	public Book returnBook(String title) {
		Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found"));
		book.returnBook();
		return bookRepository.save(book);
	}

	@Override
	@Cacheable(value = "books",key = "#title")
	public Integer availabiltyBook(String title) {
		Book book = bookRepository.findByTitle(title).orElseThrow(() -> new RuntimeException("Book Not found"));
		Integer copiesAvailable = book.getCopiesAvailable();
		if(copiesAvailable<=0) {
			throw new RuntimeException("Book Copies Not Available");
		}

		return copiesAvailable;
	}

}

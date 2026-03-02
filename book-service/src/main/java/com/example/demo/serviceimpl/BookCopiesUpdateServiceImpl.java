package com.example.demo.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
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
	public Book updateTotalBookCopies(Long id, Integer newCopies) {
		Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
		if (newCopies < book.getCopiesAvailable()) {
			throw new BookNotAvailableException("Cannot Reduce Below AvailableCopies: " + book.getCopiesAvailable());
		}
		book.setTotalCopies(newCopies);
		return bookRepository.save(book);
	}

	@Override
	public Book issueBook(String title) {
		Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found"));
		book.issueBook();
		return bookRepository.save(book);
	}

	@Override
	public Book returnBook(String title) {
		Book book = bookRepository.findByTitle(title).orElseThrow(() -> new BookNotFoundException("Book not found"));
		book.returnBook();
		return bookRepository.save(book);
	}

}

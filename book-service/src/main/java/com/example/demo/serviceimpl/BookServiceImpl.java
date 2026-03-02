package com.example.demo.serviceimpl;

import java.util.List;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;

public class BookServiceImpl implements BookService{

	@Override
	public Book createBook(Book book) {
		
		return null;
	}

	@Override
	public Book updateBook(Long id, Book book) {
	
		return null;
	}

	@Override
	public Book getByIsbn(String isbn) {
		
		return null;
	}

	@Override
	public void deleteBook(String isbn) {
	
		
	}

	@Override
	public List<Book> findAll() {
		
		return null;
	}

	@Override
	public List<Book> findByTitleContainingIgnoreCase(String title) {
		
		return null;
	}

	@Override
	public List<Book> findByAuthorContainingIgnoreCase(String author) {
	
		return null;
	}

	@Override
	public Long totalBooksCount() {
	
		return null;
	}

}

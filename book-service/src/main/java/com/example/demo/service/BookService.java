package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Book;

public interface BookService {
	public Book createBook(Book book);

	public Book updateBook(Long id,Book book);

	public Book getByIsbn(String isbn);

	public void deleteBook(String isbn);

	public List<Book> findAll();

	public List<Book> findByTitleContainingIgnoreCase(String title);

	public List<Book> findByAuthorContainingIgnoreCase(String author);

	public Long totalBooksCount();
}

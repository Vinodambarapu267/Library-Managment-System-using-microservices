package com.example.demo.service;

import com.example.demo.entity.Book;

public interface BookCopiesUpdateService {
	public Book updateTotalBookCopies(Long id, Integer newCopies);

	public Book issueBook(String title);

	public Book returnBook(String title);

}

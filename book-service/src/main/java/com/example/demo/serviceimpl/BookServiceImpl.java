package com.example.demo.serviceimpl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import com.example.demo.utility.BookStatus;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	private BookRepository bookRepository;

	@Override
	public Book createBook(Book book) {
		book.setIsbn(isbnGeneate(book.getIsbn()));
		return bookRepository.save(book);
	}

	@Override
	public Book updateBook(Long id, BookDto bookDto) {
		Book existedBook = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not Found"));
		existedBook.setAuthor(bookDto.getAuthor());
		existedBook.setTitle(bookDto.getTitle());
		existedBook.setCopiesAvailablr(bookDto.getCopiesAvailablr());
		existedBook.setTotalcopies(bookDto.getTotalcopies());
		int copies = Integer.parseInt(bookDto.getCopiesAvailablr() != null ? bookDto.getCopiesAvailablr() : "0");
		existedBook.setBookStatus(copies > 0 ? BookStatus.AVAILABLE.name() : BookStatus.OUT_OF_STOCK.name());
		existedBook.setCategory(bookDto.getCategory());
		Book update = bookRepository.save(existedBook);
		return update;
	}

	@Override
	public Book getByIsbn(String isbn) {
		Book byisbn = bookRepository.findByIsbn(isbn.trim())
				.orElseThrow(() -> new RuntimeException("Book is not found with this ISBN : " + isbn));

		return byisbn;
	}

	@Override
	public void deleteBook(String isbn) {
		Book delete = bookRepository.findByIsbn(isbn).orElseThrow(() -> new RuntimeException("Book is not Found "));
		bookRepository.delete(delete);
	}

	@Override
	public List<Book> findAll() {
		List<Book> all = bookRepository.findAll();
		if (all.isEmpty()) {
			throw new RuntimeException("Libary is empty");
		}
		return all;
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

	private static String isbnGeneate(String isbn) {
		UUID uuid = UUID.randomUUID();
		return uuid.randomUUID().toString().replace("-", "").substring(0, 13).toUpperCase();
	}
}

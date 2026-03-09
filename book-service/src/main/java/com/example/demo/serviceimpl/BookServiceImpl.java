package com.example.demo.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import com.example.demo.exception.BookNotFoundException;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import com.example.demo.utility.BookStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private CacheManager cacheManager;

	@Override
	public Book createBook(Book book) {
		log.debug("Creating book with title: {}", book.getTitle());
		Optional<Book> existedBookByTitle = bookRepository.findByTitle(book.getTitle());
		if (existedBookByTitle.isPresent()) {
			log.warn("Book Already Present With this title{}", book.getTitle());
			throw new RuntimeException("Book Already Present With this title: " + book.getTitle());
		}
		book.setIsbn(isbnGenerate(book.getIsbn()));
		Book saveBook = bookRepository.save(book);

		log.info("Book Created successfully - ID : {}, Title : {} ", saveBook.getId(), saveBook.getTitle());
		return saveBook;
	}

	@Override
	@CachePut(value = "books", key = "#id")
	public Book updateBook(Long id, BookDto bookDto) {
		log.debug("Updating book with ID: {}", id);
		Book existedBook = bookRepository.findById(id).orElseThrow(() -> {
			log.warn("Book not Found ID: {}}", id);
			return new BookNotFoundException("Book not Found ID : " + id);
		});
		existedBook.setAuthor(bookDto.getAuthor());
		existedBook.setTitle(bookDto.getTitle());
		existedBook.setCopiesAvailable(bookDto.getCopiesAvailable());
		existedBook.setTotalCopies(bookDto.getTotalCopies());
		int copies = bookDto.getCopiesAvailable() != null ? bookDto.getCopiesAvailable() : 0;
		existedBook.setBookStatus(copies > 0 ? BookStatus.AVAILABLE.name() : BookStatus.OUT_OF_STOCK.name());
		existedBook.setCategory(bookDto.getCategory());
		Book update = bookRepository.save(existedBook);
		log.info("book Updated Successfully - ID: {}" + update.getId());
		Cache booksCache = cacheManager.getCache("books");
		booksCache.put(id, update);
		return update;
	}

	@Override
	@Cacheable(value = "books", key = "#isbn")
	public Book getByIsbn(String isbn) {
		log.debug("Searching Book By ISBN:{}", isbn);
		Book book = bookRepository.findByIsbn(isbn.trim()).orElseThrow(() -> {
			log.warn("Book is not found with this ISBN : " + isbn);
			return new BookNotFoundException("Book is not found with this ISBN : " + isbn);
		});
		log.info("Book found by ISBN {}: ,ID = {},Title = {}", book.getIsbn(), book.getId(), book.getTitle());
		return book;
	}

	@Override
	@CacheEvict(value = "books", key = "#isbn")
	public void deleteBook(String isbn) {
		log.debug("Deleting book by ISBN : {}", isbn);
		Book delete = bookRepository.findByIsbn(isbn).orElseThrow(() -> {
			log.warn("Book is not found with this ISBN : " + isbn);
			return new BookNotFoundException("Book is not Found ");
		});
		log.info("Book Deleted successfully  ISBN: {}", isbn);
		bookRepository.delete(delete);

	}

	@Override
	@Cacheable(value = "books", key = "'all'")
	public List<Book> findAll() {
		log.debug("Fetching All Books from repository");
		List<Book> all = bookRepository.findAll();
		if (all.isEmpty()) {
			log.warn("Library empty - no books available");
			throw new BookNotFoundException("Library is empty");
		}
		log.info("Retrived {} book from library", all.size());
		return all;
	}

	@Cacheable(value = "books", key = "#title")
	@Override
	public List<Book> findByTitleContainingIgnoreCase(String title) {
		log.debug("Searching  books containing Title : {}", title);
		List<Book> byTitleContainingIgnoreCase = bookRepository.findByTitleContainingIgnoreCase(title);
		if (byTitleContainingIgnoreCase.isEmpty()) {
			log.warn("No books found matching title pattern: '{}'", title);
			throw new BookNotFoundException("No book found with title : " + title);
		}
		log.info("Found {} books matching title '{}'", byTitleContainingIgnoreCase.size(), title);
		return byTitleContainingIgnoreCase;
	}

	@Cacheable(value = "books", key = "#author")
	@Override
	public List<Book> findByAuthorContainingIgnoreCase(String author) {
		log.debug("Searching  books by author : {}", author);
		List<Book> byAuthorContainingIgnoreCase = bookRepository.findByAuthorContainingIgnoreCase(author);
		if (byAuthorContainingIgnoreCase.isEmpty()) {
			throw new BookNotFoundException("No books found by author : " + author);
		}
		log.info("Found {} books by author '{}'", byAuthorContainingIgnoreCase.size(), author);
		return byAuthorContainingIgnoreCase;

	}

	@Cacheable(value = "books", key = "'long'")
	@Override
	public Long totalBooksCount() {
		log.debug("Counting the books");
		long count = bookRepository.count();
		if (count == 0) {
			log.warn("Book Count : {}", count);
		}
		log.warn("Book Count : {}", count);
		return count;
	}

	private static String isbnGenerate(String isbn) {
		log.debug("Generating the ISBN :{}", isbn);
		UUID uuid = UUID.randomUUID();
		log.info("Generated the ISBN:{} ", isbn);
		return uuid.randomUUID().toString().replace("-", "").substring(0, 13).toUpperCase();
	}
}

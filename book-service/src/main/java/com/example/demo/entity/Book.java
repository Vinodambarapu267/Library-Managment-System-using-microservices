package com.example.demo.entity;

import com.example.demo.exception.BookNotAvailableException;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String isbn;
	private String title;
	private String author;
	private Integer copiesAvailable;
	private Integer totalCopies;
	private String category;
	private Integer publishedYear;
	private String bookStatus = "AVAILABLE";

	@PrePersist
	@PreUpdate
	public void validateCopies() {
		if (copiesAvailable > totalCopies || copiesAvailable < 0) {
			throw new IllegalStateException("Invalid copy counts");
		}
	}

	// Enhanced methods
	public void issueBook() {
		if (copiesAvailable <= 0) {
			throw new BookNotAvailableException("No copies available for: " + title);
		}
		copiesAvailable--;
	}

	public void returnBook() {
		if (copiesAvailable >= totalCopies) {
			throw new IllegalStateException("All copies already available");
		}
		copiesAvailable++;
	}

	
	public void addCopies(int additionalCopies) {
		totalCopies += additionalCopies;
		copiesAvailable += additionalCopies;
	}
}

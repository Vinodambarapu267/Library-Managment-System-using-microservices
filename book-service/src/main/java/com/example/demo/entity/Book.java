package com.example.demo.entity;

import jakarta.persistence.Entity;
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
	private Long id;
	private String isbn;
	private String title;
	private String author;
	private String copiesAvailablr;
	private String totalcopies;
	private String category;
	private Integer publishedYear;
	private String bookStatus = "AVAILABLE";
}

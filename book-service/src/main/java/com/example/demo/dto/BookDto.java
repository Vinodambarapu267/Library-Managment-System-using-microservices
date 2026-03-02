package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
	private String isbn;
	private String title;
	private String author;
	private Integer copiesAvailable;
	private Integer totalCopies;
	private String category;
	private Integer publishedYear;
	private String bookStatus = "AVAILABLE";
}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAvailability {
	private boolean isAvailable;
	private Integer copiesAvailable;
	private boolean canBorrow;

	public void setCopies(int newCopies) {
		this.copiesAvailable = newCopies;
		this.isAvailable = newCopies > 0; 
	}

	public boolean canBorrow() {
		return isAvailable && copiesAvailable > 0;
	}

	public BookAvailability(int copies) {
		this.copiesAvailable = copies;
		this.isAvailable = copies > 0;
	}
}

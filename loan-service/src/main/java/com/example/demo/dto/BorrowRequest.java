package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest {
	@NotBlank(message = "Title is Required")
	private String title;

	@NotBlank(message = "User ID is required")
	private Long userId;
}

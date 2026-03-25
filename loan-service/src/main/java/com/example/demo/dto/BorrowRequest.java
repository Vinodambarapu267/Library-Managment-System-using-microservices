package com.example.demo.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequest implements Serializable{
	@NotBlank(message = "Title is Required")
	private String title;

	@NotBlank(message = "User ID is required")
	private Long userId;
}

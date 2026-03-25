package com.example.demo.dto;

import java.io.Serializable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements Serializable{
	@Valid
	@NotBlank(message = "Enter the username")
	private String userName;
	@Valid
	@NotBlank(message = "Enter the email ID")
	private String email;
	@Valid
	@NotBlank(message = "Enter the password")
	private String password;
	private String role;
}

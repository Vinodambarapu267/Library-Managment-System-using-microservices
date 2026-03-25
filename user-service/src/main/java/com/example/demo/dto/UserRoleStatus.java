package com.example.demo.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleStatus implements Serializable{
	private String role;
	private boolean isActive;
	private boolean allowToBorrow;
}

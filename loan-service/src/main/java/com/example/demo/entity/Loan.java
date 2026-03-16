package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "loan")
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long loanId;
	private String title;
	private Long userId;
	private LocalDate borrowAt;
	private LocalDate dueDate;
	private LocalDate returnedAt;
	private Double totalAmount;
	private String status;
}

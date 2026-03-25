package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.example.demo.utility.FineStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fine  implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long fineId;
	private Double amount;
	private LocalDate overDueSince;
	private Long loanId;
	private String fineStatus = FineStatus.PENDING.name();
}

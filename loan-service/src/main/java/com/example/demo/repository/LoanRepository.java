package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
	List<Loan> findAllByUserId(Long userId);

	List<Loan> findAllByUserIdAndStatus(Long userId, String status);

}

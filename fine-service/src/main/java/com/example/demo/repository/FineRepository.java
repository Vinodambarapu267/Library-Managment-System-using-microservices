package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Fine;

public interface FineRepository extends JpaRepository<Fine, Long> {
List<Fine> findByFineStatus(String fineStatus);
}

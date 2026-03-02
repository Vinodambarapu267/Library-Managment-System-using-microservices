package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.LibraryUser;

public interface UserRepository extends JpaRepository<LibraryUser, Long> {

}

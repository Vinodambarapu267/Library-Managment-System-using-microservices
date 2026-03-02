package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.LibraryUser;

public interface LibraryUserServie {
	public LibraryUser register(LibraryUser libraryUser);

	public LibraryUser findById(Long id);

	public LibraryUser updateUser(Long id, UserDto userDto);

	public List<LibraryUser> findAll();

	public void deleteById(Long id);
}

package com.example.demo.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.LibraryUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LibraryUserServie;

@Service
public class LibraryUserServieImpl implements LibraryUserServie {
	@Autowired
	private UserRepository userRepository;

	@Override
	public LibraryUser register(LibraryUser libraryUser) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LibraryUser findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LibraryUser updateUser(Long id, UserDto userDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LibraryUser> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}

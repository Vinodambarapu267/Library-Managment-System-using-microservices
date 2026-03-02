package com.example.demo.serviceimpl;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.LibraryUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LibraryUserServie;
import com.example.demo.utility.MemeberType;

@Service
public class LibraryUserServieImpl implements LibraryUserServie {
	@Autowired
	private UserRepository userRepository;

	@Override
	public LibraryUser register(LibraryUser libraryUser) {
		Optional<LibraryUser> byEmail = userRepository.findByEmail(libraryUser.getEmail());
		if (byEmail.isEmpty()) {
			libraryUser.setPassword(Base64.getEncoder().encodeToString(libraryUser.getPassword().getBytes()));
			libraryUser.setRole(roleupdate(libraryUser.getRole()));
			return userRepository.save(libraryUser);
		} else {
			throw new RuntimeException("User Already existed");
		}
	}

	@Override
	@Cacheable(value = "libraryusers", key = "#id")
	public LibraryUser findById(Long id) {
		LibraryUser user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException(" User Not Found Exception"));

		return user;
	}

	@Override
	@CachePut(value = "libraryusers",key = "#id")
	public LibraryUser updateUser(Long id, UserDto userDto) {
		LibraryUser existedUser = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not Found"));
		existedUser.setEmail(userDto.getEmail());
		existedUser.setPassword(Base64.getEncoder().encodeToString(userDto.getPassword().getBytes()));
		existedUser.setRole(roleupdate(userDto.getRole()));
		existedUser.setUserName(userDto.getUserName());
		LibraryUser update = userRepository.save(existedUser);
		return update;
	}

	@Override
	@Cacheable(value = "libraryusers",key = "'all'")
	public List<LibraryUser> findAll() {
		List<LibraryUser> all = userRepository.findAll();
		if (all.isEmpty()) {
			throw new RuntimeException("No User");
		}
		return all;
	}

	@Override
	@CacheEvict(value = "libraryusers",key = "#id")
	public void deleteById(Long id) {
	userRepository.deleteById(id);
	}

	private static String roleupdate(String role) {
		switch (role.toLowerCase()) {
		case "student" -> {

			return MemeberType.STUDENT.name();
		}
		case "faculty" -> {
			return MemeberType.FACULTY.name();
		}
		case "staff" -> {

			return MemeberType.STAFF.name();
		}
		case "librarian" -> {
			return MemeberType.LIBRARIAN.name();
		}
		case "admin" -> {

			return MemeberType.ADMIN.name();
		}
		case "guest" -> {
			return MemeberType.GUEST.name();
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + role);
		}
	}

	
}

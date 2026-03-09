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
import com.example.demo.dto.UserRoleStatus;
import com.example.demo.entity.LibraryUser;
import com.example.demo.exception.UserAlreadyExistException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.LibraryUserServie;
import com.example.demo.utility.MemeberType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LibraryUserServieImpl implements LibraryUserServie {
	@Autowired
	private UserRepository userRepository;

	@Override
	public LibraryUser register(LibraryUser libraryUser) {
		log.debug("Registering a user email='{}'", libraryUser.getEmail());
		Optional<LibraryUser> byEmail = userRepository.findByEmail(libraryUser.getEmail());
		if (byEmail.isEmpty()) {
			libraryUser.setPassword(Base64.getEncoder().encodeToString(libraryUser.getPassword().getBytes()));
			libraryUser.setRole(roleupdate(libraryUser.getRole()));
			log.info("User registered successfully: email='{}'", libraryUser.getEmail());
			return userRepository.save(libraryUser);
		} else {
			log.warn("Registration failed: User already exist with enail-'{}'", libraryUser.getEmail());
			throw new UserAlreadyExistException("User Already exist with email: " + libraryUser.getEmail());
		}
	}

	@Override
	@Cacheable(value = "libraryusers", key = "#id")
	public LibraryUser findById(Long id) {
		log.debug("Searching a  user ID: {}", id);
		LibraryUser user = userRepository.findById(id).orElseThrow(() -> {
			log.warn("User not found ID={}", id);
			return new UserNotFoundException(" User Not Found Exception");
		});
		log.info("User found successfully ID: {}", id);
		return user;
	}

	@Override
	@CachePut(value = "libraryusers", key = "#id")
	public LibraryUser updateUser(Long id, UserDto userDto) {
		log.debug("Updating a user by ID:{}", id);
		LibraryUser existedUser = userRepository.findById(id).orElseThrow(() -> {
			log.warn("User update failed: No user found with ID: {}", id);
			return new UserNotFoundException("User not Found");
		});
		existedUser.setEmail(userDto.getEmail());
		existedUser.setPassword(Base64.getEncoder().encodeToString(userDto.getPassword().getBytes()));
		existedUser.setRole(roleupdate(userDto.getRole()));
		existedUser.setUserName(userDto.getUserName());
		LibraryUser update = userRepository.save(existedUser);
		log.info("User updated successfully: ID: {}", id);
		return update;
	}

	@Override
	@Cacheable(value = "libraryusers", key = "'all'")
	public List<LibraryUser> findAll() {
		log.debug("Fetching All users cache/repository");
		List<LibraryUser> all = userRepository.findAll();
		if (all.isEmpty()) {
			log.warn("No user found in database");
			throw new UserNotFoundException("No User found in System");
		}
		log.info(" Successfully recieved {} users from cache/repository", all.size());
		return all;
	}

	@Override
	@CacheEvict(value = "libraryusers", key = "#id")
	public void deleteById(Long id) {
		log.debug("Deleting user by ID: {}", id);
		LibraryUser remove = userRepository.findById(id).orElseThrow(() -> {
			log.warn("User not found by ID: {}", id);
			return new UserNotFoundException("user not Found");
		});
		log.info("User deleted successfully ID: {}", id);
		userRepository.deleteById(remove.getId());
	}

	@Override
	@Cacheable(value = "UserRoleStatus",key = "#id")
	public UserRoleStatus checkRoleStatus(Long id) {
		log.debug("Checking user role by ID:{}", id);
		LibraryUser user = userRepository.findById(id).orElseThrow(() -> {
			log.warn("User not found by ID: {}", id);
			return new UserNotFoundException("User not found ID:" + id);
		});
		boolean canBorrow = switch (user.getRole()) {
		case "STUDENT", "FACULTY", "STAFF" -> true;
		case "GUEST", "INACTIVE" -> false;
		default -> false;
		};
		UserRoleStatus roleStatus = new UserRoleStatus();
		roleStatus.setRole(user.getRole());
		roleStatus.setActive(user.isActive());
		roleStatus.setAllowToBorrow(canBorrow);
		log.info("successfully user role retrived ID: {} ", id);
		return roleStatus;
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

package com.example.demo.controller;

import java.net.HttpURLConnection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserRoleStatus;
import com.example.demo.entity.LibraryUser;
import com.example.demo.service.LibraryUserServie;
import com.example.demo.utility.ResponseMessage;
import com.example.demo.utility.ResponseStatus;

import jakarta.ws.rs.PathParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class LibraryUserController {

	@Autowired
	private LibraryUserServie libraryUserServie;

	@PostMapping("/register")
	public ResponseEntity<?> registerLibrary(@RequestBody LibraryUser libraryUser) {
		log.debug("Received  user registration request email='{}' ", libraryUser.getEmail());
		LibraryUser register = libraryUserServie.register(libraryUser);
		if (register != null) {
			log.info("User successfully registered email='{}' , delegating response", libraryUser.getEmail());
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"User registered Successfully", register));
		} else {
			log.warn("User registraion return null from service for email='{}'", libraryUser.getEmail());
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "User registration  failed.."));

		}
	}

	@PutMapping("/updateUser/id")
	public ResponseEntity<?> updateByuser(@RequestParam Long id, @RequestBody UserDto dto) {
		log.debug("Received  user updation reques ID: {}", id);
		LibraryUser updateUser = libraryUserServie.updateUser(id, dto);
		if (updateUser != null) {
			log.info("User updated successfully: ID={}", id);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"User updated Successfully", updateUser));
		} else {
			log.warn("user updation return null from service for ID={}", id);
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "User updation failed.."));

		}
	}

	@GetMapping("/getbyid/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		log.debug("Received  fetching user request : ID ={}", id);
		LibraryUser byId = libraryUserServie.findById(id);
		log.info("User found successfully: ID={}, delegating response", id);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
				"User retrived Successfully", byId));
	}

	@GetMapping()
	public ResponseEntity<?> findAll() {
		log.debug("Received  fetching all user request");
		List<LibraryUser> all = libraryUserServie.findAll();
		if (all != null) {
			log.info("All users found successfully: count={}", all.size());
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"all user retrieved Successfully", all));
		} else {
			log.warn("All user found return null from servicec");
			return ResponseEntity
					.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR, ResponseStatus.FAILURE.name(), ""));

		}
	}

	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<?> deleteById(@PathParam(value = "id") Long id) {
		log.debug("Received  user deletion request ID={}", id);
		libraryUserServie.deleteById(id);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_OK,
				ResponseStatus.FAILURE.name(), "User deleted successfully"));
	}

	@GetMapping("/{id}/checkstatus")
	public ResponseEntity<?> checkStatus(@PathVariable Long id) {
		log.debug("Received  Check role status request : ID={}", id);
		UserRoleStatus checkRoleStatus = libraryUserServie.checkRoleStatus(id);
		log.info("User role status retrieved successfully: ID={}", id);
		return ResponseEntity.ok(checkRoleStatus);

	}
}

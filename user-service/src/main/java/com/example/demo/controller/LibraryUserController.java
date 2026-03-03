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

import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.Path;

@RestController
@RequestMapping("/api/users")
public class LibraryUserController {

	@Autowired
	private LibraryUserServie libraryUserServie;

	@PostMapping("/register")
	public ResponseEntity<?> registerLibrary(@RequestBody LibraryUser libraryUser) {
		LibraryUser register = libraryUserServie.register(libraryUser);
		if (register != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Created Successfully", register));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Created failed.."));

		}
	}

	@GetMapping("/getbyid/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		LibraryUser byId = libraryUserServie.findById(id);

		return ResponseEntity.ok(byId);
	}

	@GetMapping()
	public ResponseEntity<?> findAll() {
		List<LibraryUser> all = libraryUserServie.findAll();
		if (all != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Created Successfully", all));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Created failed.."));

		}
	}

	@PutMapping("/updateUser/id")
	public ResponseEntity<?> updateByuser(@RequestParam Long id, @RequestBody UserDto dto) {
		LibraryUser updateUser = libraryUserServie.updateUser(id, dto);
		if (updateUser != null) {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_CREATED, ResponseStatus.SUCCESS.name(),
					"Created Successfully", updateUser));
		} else {
			return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
					ResponseStatus.FAILURE.name(), "Created failed.."));

		}
	}

	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<?> deleteById(@PathParam(value = "id") Long id) {
		libraryUserServie.deleteById(id);
		return ResponseEntity.ok(new ResponseMessage(HttpURLConnection.HTTP_INTERNAL_ERROR,
				ResponseStatus.FAILURE.name(), "Created failed.."));
	}

	@GetMapping("/{id}/checkstatus")
	public ResponseEntity<?> checkStatus(@PathVariable Long id) {
		UserRoleStatus checkRoleStatus = libraryUserServie.checkRoleStatus(id);
		return ResponseEntity.ok(checkRoleStatus);

	}
}

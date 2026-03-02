package com.example.demo.controller;

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
import com.example.demo.entity.LibraryUser;
import com.example.demo.service.LibraryUserServie;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/users")
public class LibraryUserController {


	@Autowired
	private LibraryUserServie libraryUserServie;

	@PostMapping("/register")
	public ResponseEntity<?> registerLibrary(@RequestBody LibraryUser libraryUser) {
		LibraryUser register = libraryUserServie.register(libraryUser);
		return ResponseEntity.ok(register);
	}

	@GetMapping("/getbyid/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id) {
		LibraryUser byId = libraryUserServie.findById(id);
		return ResponseEntity.ok(byId);
	}
	@GetMapping()
	public ResponseEntity<?> findAll(){
		List<LibraryUser> all = libraryUserServie.findAll();
		return ResponseEntity.ok(all);
	}
	@PutMapping("/updateUser/id")
	public ResponseEntity<?> updateByuser(@RequestParam Long id,@RequestBody UserDto dto){
		LibraryUser updateUser = libraryUserServie.updateUser(id, dto);
		return ResponseEntity.ok(updateUser);
	}
	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<?> deleteById(@PathParam(value = "id") Long id){
		libraryUserServie.deleteById(id);
		return ResponseEntity.ok("deleted successfully");
	}
}

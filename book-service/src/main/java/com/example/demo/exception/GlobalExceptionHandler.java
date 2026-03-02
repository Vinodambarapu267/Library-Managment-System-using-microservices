package com.example.demo.exception;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<?> bookNotFound(BookNotFoundException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_BAD_REQUEST,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(BookAlreadyExistExcpetion.class)
	public ResponseEntity<?> BookAlreadyExist(BookAlreadyExistExcpetion exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_BAD_REQUEST,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

}

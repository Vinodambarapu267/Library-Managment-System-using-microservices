package com.example.demo.exception;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<?> userNotFound(UserNotFoundException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), exception.getLocalizedMessage(),
				HttpURLConnection.HTTP_BAD_REQUEST, request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}
@ExceptionHandler(UserAlreadyExistException.class)
public ResponseEntity<?> userAlreadYExist(UserAlreadyExistException exception, WebRequest request) {
	ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), exception.getLocalizedMessage(),
			HttpURLConnection.HTTP_BAD_REQUEST, request.getDescription(false));
	return ResponseEntity.ok(errorMessage);
}
}

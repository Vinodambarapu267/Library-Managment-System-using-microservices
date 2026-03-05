package com.example.demo.exception;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(FineNotFoundException.class)
	public ResponseEntity<?> handleFineNotFound(FineNotFoundException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_NOT_FOUND,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(FineAlreadyExistedByLoanIdException.class)
	public ResponseEntity<?> handleFineAlreadyExistedByLoanId(FineAlreadyExistedByLoanIdException exception,
			WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_NOT_FOUND,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}
}

package com.example.demo.exceptions;

import java.net.HttpURLConnection;
import java.time.LocalDateTime;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice

public class GlobalExceptionHandler {
	@ExceptionHandler(BookNotReturnedException.class)
	public ResponseEntity<?> handlenotReturned(BookNotReturnedException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_BAD_REQUEST,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(BookAlreadyReturnedException.class)
	public ResponseEntity<?> handleAlreadyReturned(BookAlreadyReturnedException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_BAD_REQUEST,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(BorrowBooksNotFoundException.class)
	public ResponseEntity<?> handleNotFound(BorrowBooksNotFoundException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_BAD_REQUEST,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(BookCopiesNotAvailableException.class)
	public ResponseEntity<?> handleUnavailable(BookCopiesNotAvailableException exception,
			WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_NOT_FOUND,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(LoanNotFoundException.class)
	public ResponseEntity<?> handleLoanNotFound(LoanNotFoundException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_CONFLICT,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}

	@ExceptionHandler(UserNotAuthorizedException.class)
	public ResponseEntity<?> handleAuthorized(UserNotAuthorizedException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_FORBIDDEN,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}
	@ExceptionHandler(BookNotFoundException.class)
	public ResponseEntity<?> handleBookNotFound(BookNotFoundException exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_FORBIDDEN,
				exception.getLocalizedMessage(), request.getDescription(false));
		return ResponseEntity.ok(errorMessage);
	}
	 @ExceptionHandler(RuntimeException.class)
	    public ResponseEntity<ErrorMessage> handleGeneric(RuntimeException ex, WebRequest request) {
		 return ResponseEntity.ok(new ErrorMessage(LocalDateTime.now(), HttpURLConnection.HTTP_INTERNAL_ERROR, ex.getMessage(), request.getDescription(false)));
	    }
}

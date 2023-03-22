package com.snayder.dsclients.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.snayder.dsclients.services.exceptions.DatabaseViolationException;
import com.snayder.dsclients.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> resourceNotFound(HttpServletRequest req,
			ResourceNotFoundException ex) {
		StandardError error = new StandardError();
		
		error.setTimestamp(Instant.now());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setError(ex.getMessage());
		error.setPath(req.getRequestURI());
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(DatabaseViolationException.class)
	public ResponseEntity<StandardError> databaseViolation(HttpServletRequest req,
			DatabaseViolationException ex) {
		StandardError error = new StandardError();
		
		error.setTimestamp(Instant.now());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setError(ex.getMessage());
		error.setPath(req.getRequestURI());
		
		return ResponseEntity.badRequest().body(error);
	}
}

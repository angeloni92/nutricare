package com.angeloni.nutricare.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler {

	/**
	 * Handles {@link InvalidCredentialsException} and returns a standardized error
	 * response.
	 *
	 * @param ex      the {@link InvalidCredentialsException} thrown when invalid
	 *                credentials are provided
	 * @param request the {@link WebRequest} associated with the current request
	 * @return a {@link ResponseEntity} containing {@link ErrorDetails} and an HTTP
	 *         status of {@link HttpStatus#UNAUTHORIZED}
	 *
	 *         This method captures the exception, constructs an
	 *         {@link ErrorDetails} object with the current timestamp, exception
	 *         message, and request details, and returns it as the response body.
	 */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorDetails> handleInvalidCredentialsException(InvalidCredentialsException ex,
			WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(ZoneId.systemDefault()), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handles generic {@link Exception} instances and returns a standardized error
	 * response.
	 *
	 * @param ex      the {@link Exception} thrown during request processing
	 * @param request the {@link WebRequest} associated with the current request
	 * @return a {@link ResponseEntity} containing {@link ErrorDetails} and an HTTP
	 *         status of {@link HttpStatus#INTERNAL_SERVER_ERROR}
	 *
	 *         This method captures any unhandled exceptions, constructs an
	 *         {@link ErrorDetails} object with the current timestamp, exception
	 *         message, and request details, and returns it as the response body.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> handleInternalServerException(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(ZoneId.systemDefault()), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handles validation exceptions thrown during the processing of request bodies annotated with {@code @Valid}.
	 *
	 * <p>This method captures validation errors, typically thrown as {@link MethodArgumentNotValidException}, 
	 * and constructs a list of {@link ErrorDetails} objects that contain the details of each validation error.
	 * Each {@link ErrorDetails} includes a timestamp, a description of the error, and the request details.
	 *
	 * @param ex      the exception containing validation error details, including the invalid fields and messages
	 * @param request the current web request, used to extract contextual information such as the request description
	 * @return a {@link ResponseEntity} containing a list of {@link ErrorDetails} and an HTTP status of {@link HttpStatus#BadRequest}
	 */
	@SuppressWarnings("unused")
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<ErrorDetails>> handleValidationExceptions(MethodArgumentNotValidException ex,
			WebRequest request) {
		List<ErrorDetails> errorDetailsList = new ArrayList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(ZoneId.systemDefault()), error.getDefaultMessage(),
					request.getDescription(false));
			errorDetailsList.add(errorDetails);
		}
		return new ResponseEntity<>(errorDetailsList, HttpStatus.BAD_REQUEST);
	}

}

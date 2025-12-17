package com.voyzr.tripms.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.warn("Validation failed: {} violations", ex.getBindingResult().getErrorCount());
        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        ApiError body = baseError(status.value(), "Validation Failed", ex.getMessage(), request, details);
        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.warn("Malformed JSON or unreadable message: {}", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
        ApiError body = baseError(status.value(), "Malformed JSON request", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage(), request, null);
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        log.warn("Constraint violation at {}: {}", req.getRequestURI(), ex.getMessage());
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> details.put(v.getPropertyPath().toString(), v.getMessage()));
        ApiError body = baseError(HttpStatus.BAD_REQUEST.value(), "Constraint Violation", ex.getMessage(), req, details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        log.warn("ResponseStatusException at {}: status={} reason={}", req.getRequestURI(), ex.getStatusCode().value(), ex.getReason());
        HttpStatus status = ex.getStatusCode() instanceof HttpStatus ? (HttpStatus) ex.getStatusCode() : HttpStatus.valueOf(ex.getStatusCode().value());
        ApiError body = baseError(status.value(), status.getReasonPhrase(), ex.getReason(), req, null);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        log.error("RuntimeException at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        // Special-case common not found message used in service
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("trip not found")) {
            ApiError body = baseError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), req, null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        ApiError body = baseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage(), req, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        ApiError body = baseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage(), req, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ApiError baseError(int status, String error, String message, WebRequest request, Map<String, String> details) {
        String path = request.getDescription(false);
        if (path != null && path.startsWith("uri=")) {
            path = path.substring(4);
        }
        return ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .details(details)
                .build();
    }

    private ApiError baseError(int status, String error, String message, HttpServletRequest request, Map<String, String> details) {
        return ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .details(details)
                .build();
    }
}

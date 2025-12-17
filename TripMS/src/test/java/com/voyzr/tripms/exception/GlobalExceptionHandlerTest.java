package com.voyzr.tripms.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Test
    void handleRuntime_notFoundMessage_returns404() {
        GlobalExceptionHandler h = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/trips/99");

        ResponseEntity<ApiError> resp = h.handleRuntime(new RuntimeException("Trip not found"), req);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals(404, resp.getBody().getStatus());
        assertEquals("/api/v1/trips/99", resp.getBody().getPath());
    }

    @Test
    void handleRuntime_generic_returns500() {
        GlobalExceptionHandler h = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/any");

        ResponseEntity<ApiError> resp = h.handleRuntime(new RuntimeException("Oops"), req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertEquals(500, resp.getBody().getStatus());
    }

    @Test
    void handleResponseStatus_usesProvidedStatus() {
        GlobalExceptionHandler h = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/x");

        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad");
        ResponseEntity<ApiError> resp = h.handleResponseStatus(ex, req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("bad", resp.getBody().getMessage());
    }

    @Test
    void handleConstraintViolation_returns400() {
        GlobalExceptionHandler h = new GlobalExceptionHandler();
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/validate");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> cv = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("field");
        when(cv.getPropertyPath()).thenReturn(path);
        when(cv.getMessage()).thenReturn("must not be null");
        ConstraintViolationException ex = new ConstraintViolationException("invalid", Collections.singleton(cv));

        ResponseEntity<ApiError> resp = h.handleConstraintViolation(ex, req);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Constraint Violation", resp.getBody().getError());
        assertTrue(resp.getBody().getDetails().containsKey("field"));
    }
}

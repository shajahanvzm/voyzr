package com.voyzr.tripms.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Standard error response body for API errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    // Optional field-level errors for validation problems
    private Map<String, String> details;
}

package com.securetaskmanagerapi.api.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
    private String message;
    private int status;
    private List<String> errors;
    private Instant timestamp = Instant.now();
    
    // Constructor for single error case
    public ErrorResponseDTO(String message, int status, String error) {
        this(message, status, Collections.singletonList(error));
    }
    
    // Constructor for multiple errors
    public ErrorResponseDTO(String message, int status, List<String> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }
}

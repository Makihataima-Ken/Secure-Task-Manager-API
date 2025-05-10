package com.securetaskmanagerapi.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTaskDTO {
    @NotBlank
    private String title;

    private String description;
    private LocalDate dueDate;
}

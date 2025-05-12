package com.securetaskmanagerapi.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDTO {
    @NotBlank
    private String title;

    private String description;
    private LocalDate dueDate;
}

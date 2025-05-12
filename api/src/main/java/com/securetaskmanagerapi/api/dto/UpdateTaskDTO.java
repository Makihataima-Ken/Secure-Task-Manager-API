package com.securetaskmanagerapi.api.dto;

import java.time.LocalDate;

import com.securetaskmanagerapi.api.entity.Status;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskDTO {
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDate dueDate;

    private Status status;
}

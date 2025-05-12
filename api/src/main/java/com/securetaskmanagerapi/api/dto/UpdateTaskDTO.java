package com.securetaskmanagerapi.api.dto;

import java.time.LocalDate;

import com.securetaskmanagerapi.api.entity.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Status status;
}

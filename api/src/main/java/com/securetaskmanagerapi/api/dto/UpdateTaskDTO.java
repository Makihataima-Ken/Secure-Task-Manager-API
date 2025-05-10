package com.securetaskmanagerapi.api.dto;

import java.time.LocalDate;

import com.securetaskmanagerapi.api.entity.Status;

import lombok.Data;


@Data
public class UpdateTaskDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Status status;
}

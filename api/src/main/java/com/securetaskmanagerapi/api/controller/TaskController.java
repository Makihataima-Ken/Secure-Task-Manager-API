package com.securetaskmanagerapi.api.controller;

import com.securetaskmanagerapi.api.dto.CreateTaskDTO;
import com.securetaskmanagerapi.api.dto.TaskResponseDTO;
import com.securetaskmanagerapi.api.dto.UpdateTaskDTO;
import com.securetaskmanagerapi.api.entity.Task;
import com.securetaskmanagerapi.api.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestBody @Valid CreateTaskDTO taskDTO,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String ownerId = jwt.getSubject();
        TaskResponseDTO createdTask = taskService.createTask(taskDTO, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        TaskResponseDTO task = taskService.getTaskById(id, ownerId);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(@AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        List<Task> tasks = taskService.getAllTasks(ownerId);
        List<TaskResponseDTO> taskDTOs = tasks.stream()
                .map(TaskResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTaskDTO taskDTO,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String ownerId = jwt.getSubject();
        TaskResponseDTO updatedTask = taskService.updateTask(id, taskDTO, ownerId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        taskService.deleteTask(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}

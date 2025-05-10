package com.securetaskmanagerapi.api.controller;

import com.securetaskmanagerapi.api.entity.Task;
import com.securetaskmanagerapi.api.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        Task createdTask = taskService.createTask(task, ownerId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        Task task = taskService.getTaskById(id, ownerId);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        List<Task> tasks = taskService.getAllTasks(ownerId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id, 
            @RequestBody Task taskDetails, 
            @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        Task updatedTask = taskService.updateTask(id, taskDetails, ownerId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        taskService.deleteTask(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
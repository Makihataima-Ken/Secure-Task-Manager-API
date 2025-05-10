package com.securetaskmanagerapi.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.securetaskmanagerapi.api.entity.Task;
import com.securetaskmanagerapi.api.exception.ResourceNotFoundException;
import com.securetaskmanagerapi.api.exception.UnauthorizedAccessException;
import com.securetaskmanagerapi.api.repository.TaskRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    
    
    public Task createTask(Task task, String ownerId) {
        task.setOwnerId(ownerId);
        return taskRepository.save(task);
    }

    
    public Task getTaskById(Long id, String ownerId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        if (!task.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this task");
        }
        
        return task;
    }

    
    public List<Task> getAllTasks(String ownerId) {
        return taskRepository.findByOwnerId(ownerId);
    }

    
    public Task updateTask(Long id, Task taskDetails, String ownerId) {
        Task task = getTaskById(id, ownerId);
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }

    
    public void deleteTask(Long id, String ownerId) {
        Task task = getTaskById(id, ownerId);
        taskRepository.delete(task);
    }

}

package com.securetaskmanagerapi.api.service;

import com.securetaskmanagerapi.api.dto.CreateTaskDTO;
import com.securetaskmanagerapi.api.dto.TaskResponseDTO;
import com.securetaskmanagerapi.api.dto.UpdateTaskDTO;
import com.securetaskmanagerapi.api.entity.Task;
import com.securetaskmanagerapi.api.exception.ResourceNotFoundException;
import com.securetaskmanagerapi.api.exception.UnauthorizedAccessException;
import com.securetaskmanagerapi.api.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponseDTO createTask(CreateTaskDTO taskDTO, String ownerId) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setStatus(taskDTO.getStatus());
        task.setOwnerId(ownerId);
        taskRepository.save(task);
        return new TaskResponseDTO(task);
    }

    public TaskResponseDTO getTaskById(Long id, String ownerId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this task");
        }

        return new TaskResponseDTO(task);
    }

    public List<Task> getAllTasks(String ownerId) {
        return taskRepository.findByOwnerId(ownerId);
    }

    public TaskResponseDTO updateTask(Long id, UpdateTaskDTO taskDTO, String ownerId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this task");
        }

        if (taskDTO.getTitle() != null) task.setTitle(taskDTO.getTitle());
        if (taskDTO.getDescription() != null) task.setDescription(taskDTO.getDescription());
        if (taskDTO.getStatus() != null) task.setStatus(taskDTO.getStatus());
        if (taskDTO.getDueDate() != null) task.setDueDate(taskDTO.getDueDate());

        taskRepository.save(task);
        return new TaskResponseDTO(task);
    }

    public void deleteTask(Long id, String ownerId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not authorized to access this task");
        }
        taskRepository.delete(task);
    }
}

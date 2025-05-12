package com.securetaskmanagerapi.api;

import com.securetaskmanagerapi.api.dto.CreateTaskDTO;
import com.securetaskmanagerapi.api.dto.TaskResponseDTO;
import com.securetaskmanagerapi.api.dto.UpdateTaskDTO;
import com.securetaskmanagerapi.api.entity.Task;
import com.securetaskmanagerapi.api.entity.Status;
import com.securetaskmanagerapi.api.exception.ResourceNotFoundException;
import com.securetaskmanagerapi.api.exception.UnauthorizedAccessException;
import com.securetaskmanagerapi.api.repository.TaskRepository;
import com.securetaskmanagerapi.api.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private final String OWNER_ID = "user123";
    private final String OTHER_OWNER_ID = "user456";
    private Task task;
    private CreateTaskDTO createTaskDTO;
    private UpdateTaskDTO updateTaskDTO;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setStatus(Status.PENDING);
        task.setOwnerId(OWNER_ID);

        createTaskDTO = new CreateTaskDTO();
        createTaskDTO.setTitle("New Task");
        createTaskDTO.setDescription("New Description");
        createTaskDTO.setDueDate(LocalDate.now().plusDays(5));
        createTaskDTO.setStatus(Status.PENDING);

        updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setTitle("Updated Task");
        updateTaskDTO.setDescription("Updated Description");
        updateTaskDTO.setDueDate(LocalDate.now().plusDays(10));
        updateTaskDTO.setStatus(Status.IN_PROGRESS);
    }

    // create task test
    //-----------------------------------------
    @Test
    void createTask_ShouldReturnTaskResponseDTO() {
        // Create a task that matches what the service will create
        Task expectedTask = new Task();
        expectedTask.setTitle(createTaskDTO.getTitle());
        expectedTask.setDescription(createTaskDTO.getDescription());
        expectedTask.setDueDate(createTaskDTO.getDueDate());
        expectedTask.setStatus(createTaskDTO.getStatus()); // Make sure this is set
        expectedTask.setOwnerId(OWNER_ID);
        
        when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);
        
        TaskResponseDTO result = taskService.createTask(createTaskDTO, OWNER_ID);
        
        assertNotNull(result);
        assertEquals(createTaskDTO.getTitle(), result.getTitle()); // Compare with DTO
        assertEquals(createTaskDTO.getDescription(), result.getDescription());
        assertEquals(createTaskDTO.getDueDate(), result.getDueDate());
        // assertEquals(createTaskDTO.getStatus(), result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_ShouldSetOwnerId() {
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            assertEquals(OWNER_ID, savedTask.getOwnerId());
            return task;
        });
        
        taskService.createTask(createTaskDTO, OWNER_ID);
    }

    //---------------------------------------------------
}

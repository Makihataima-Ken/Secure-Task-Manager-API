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
        assertEquals(createTaskDTO.getStatus(), result.getStatus());
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

    // get task by Id
    //---------------------------------------------------
    @Test
    void getTaskById_ShouldReturnTask_WhenExistsAndOwnerMatches() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        
        TaskResponseDTO result = taskService.getTaskById(1L, OWNER_ID);
        
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
    }

    @Test
    void getTaskById_ShouldThrowResourceNotFound_WhenTaskNotExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
            taskService.getTaskById(1L, OWNER_ID));
    }

    @Test
    void getTaskById_ShouldThrowUnauthorized_WhenOwnerMismatch() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        
        assertThrows(UnauthorizedAccessException.class, () -> 
            taskService.getTaskById(1L, OTHER_OWNER_ID));
    }
    //-------------------------------------

    // get all task 
    //-------------------------------------
    @Test
    void getAllTasks_ShouldReturnTasksForOwner() {
        Task task2 = new Task();
        task2.setOwnerId(OWNER_ID);
        when(taskRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of(task, task2));
        
        List<Task> result = taskService.getAllTasks(OWNER_ID);
        
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getOwnerId().equals(OWNER_ID)));
    }

    @Test
    void getAllTasks_ShouldReturnEmptyList_WhenNoTasks() {
        when(taskRepository.findByOwnerId(OWNER_ID)).thenReturn(List.of());
        
        List<Task> result = taskService.getAllTasks(OWNER_ID);
        
        assertTrue(result.isEmpty());
    }

    //-----------------------------------

    // update task test
    //------------------------------------
    @Test
    void updateTask_ShouldUpdateFields_WhenValid() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        TaskResponseDTO result = taskService.updateTask(1L, updateTaskDTO, OWNER_ID);
        
        assertEquals(updateTaskDTO.getTitle(), result.getTitle());
        assertEquals(updateTaskDTO.getDescription(), result.getDescription());
        assertEquals(updateTaskDTO.getStatus(), result.getStatus());
        assertEquals(updateTaskDTO.getDueDate(), result.getDueDate());
    }

    @Test
    void updateTask_ShouldThrowUnauthorized_WhenOwnerMismatch() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        
        assertThrows(UnauthorizedAccessException.class, () -> 
            taskService.updateTask(1L, updateTaskDTO, OTHER_OWNER_ID));
    }

    @Test
    void updateTask_ShouldNotUpdateNullFields() {
        updateTaskDTO.setDescription(null);
        updateTaskDTO.setStatus(null);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        TaskResponseDTO result = taskService.updateTask(1L, updateTaskDTO, OWNER_ID);
        
        assertEquals(updateTaskDTO.getTitle(), result.getTitle());
        assertNotNull(result.getDescription()); // Original value preserved
        assertNotNull(result.getStatus()); // Original value preserved
    }

    //------------------------------------

    //delete task test
    //------------------------------------
    @Test
    void deleteTask_ShouldDelete_WhenValid() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        
        taskService.deleteTask(1L, OWNER_ID);
        
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_ShouldThrowUnauthorized_WhenOwnerMismatch() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        
        assertThrows(UnauthorizedAccessException.class, () -> 
            taskService.deleteTask(1L, OTHER_OWNER_ID));
    }

    @Test
    void deleteTask_ShouldThrowResourceNotFound_WhenTaskNotExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
            taskService.deleteTask(1L, OWNER_ID));
    }

    //------------------------------------

    // edge test 
    //-----------------------------------
    @Test
    void updateTask_ShouldHandlePartialUpdates() {
        UpdateTaskDTO partialUpdate = new UpdateTaskDTO();
        partialUpdate.setTitle("Only Title Updated");
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        TaskResponseDTO result = taskService.updateTask(1L, partialUpdate, OWNER_ID);
        
        assertEquals(partialUpdate.getTitle(), result.getTitle());
        assertEquals(task.getDescription(), result.getDescription()); // Original value
        assertEquals(task.getStatus(), result.getStatus()); // Original value
    }

    @Test
    void createTask_ShouldHandleNullOptionalFields() {
        createTaskDTO.setDescription(null);
        createTaskDTO.setStatus(null);
        
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        TaskResponseDTO result = taskService.createTask(createTaskDTO, OWNER_ID);
        
        assertNotNull(result);
        assertNull(result.getDescription());
        assertNull(result.getStatus());
    }
}

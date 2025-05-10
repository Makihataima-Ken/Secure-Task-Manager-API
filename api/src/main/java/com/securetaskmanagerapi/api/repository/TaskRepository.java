package com.securetaskmanagerapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securetaskmanagerapi.api.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerId(String ownerId);
}

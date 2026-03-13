package com.sabin.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sabin.entity.Task;
import com.sabin.service.TaskService;

@RestController // Marks this class as REST API controller
@RequestMapping("/api/tasks") // Base URL for all task APIs
public class TaskController {

    @Autowired
    private TaskService taskService; // Service layer handling business logic

    // Get all tasks of the logged-in user
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Only USER or ADMIN can access
    public List<Task> getMyTasks(Authentication authentication) {

        // Get currently logged-in username
        String username = authentication.getName();

        // Fetch tasks belonging to that user
        return taskService.getTasksByUser(username);
    }

    // Create a new task
    @PostMapping
    @PreAuthorize("hasRole('USER')") // Only USER can create tasks
    public Task createTask(@Valid @RequestBody Task task, Authentication authentication) {

        String username = authentication.getName();

        // Save task for this user
        return taskService.createTask(task, username);
    }

    // Update an existing task
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @Valid @RequestBody Task task,
                                           Authentication authentication) {

        String username = authentication.getName();

        // Update task if it belongs to the user
        Task updated = taskService.updateTask(id, task, username);

        return ResponseEntity.ok(updated);
    }

    // Delete a task
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {

        String username = authentication.getName();

        // Delete task if owned by this user
        taskService.deleteTask(id, username);

        return ResponseEntity.ok().build();
    }
}


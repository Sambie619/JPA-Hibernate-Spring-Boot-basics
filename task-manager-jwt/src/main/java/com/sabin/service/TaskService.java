package com.sabin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.sabin.entity.Task;
import com.sabin.entity.User;
import com.sabin.repository.TaskRepository;
import com.sabin.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Get tasks belonging to logged-in user
    public List<Task> getTasksByUser(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return taskRepository.findByUser(user);
    }

    // Create task
    public Task createTask(Task task, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // The authenticated user becomes the owner of the new task.
        task.setUser(user);

        return taskRepository.save(task);
    }

    // Update task
    public Task updateTask(Long id, Task updatedTask, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Only return the task if it belongs to the logged-in user.
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());

        return taskRepository.save(task);
    }

    // Delete task
    public void deleteTask(Long id, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // This prevents deleting someone else's task by guessing the id.
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        taskRepository.delete(task);
    }
}

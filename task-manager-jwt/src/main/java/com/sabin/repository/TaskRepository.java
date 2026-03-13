package com.sabin.repository;

import com.sabin.entity.Task;
import com.sabin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Return only the tasks that belong to one user.
    List<Task> findByUser(User user);

    // Useful for update/delete so one user cannot touch another user's task.
    Optional<Task> findByIdAndUser(Long id, User user);
}


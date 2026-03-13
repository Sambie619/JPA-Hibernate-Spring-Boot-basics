package com.sabin.repository;

import com.sabin.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {//dervied
    List<Student> findByCourseId(Long courseId);
}


package com.sabin.repository;

import com.sabin.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Derived query method - by property name
    List<Student> findByLastName(String lastName);

    // Derived query - contains (LIKE %keyword%)
    List<Student> findByFirstNameContainingIgnoreCase(String keyword);

    // Custom JPQL query
    @Query("SELECT s FROM Student s WHERE s.email LIKE %?1")
    List<Student> findByEmailDomain(String domain);
}


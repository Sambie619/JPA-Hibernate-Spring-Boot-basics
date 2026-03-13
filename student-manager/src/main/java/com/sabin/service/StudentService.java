package com.sabin.service;

import com.sabin.entity.Student;
import com.sabin.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    public Student createStudent(Student student) {
        // Example rule: email should be lower-case
        student.setEmail(student.getEmail().toLowerCase());
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail().toLowerCase());
        existing.setCourse(updated.getCourse());
        return studentRepository.save(existing);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> findByLastName(String lastName) {
        return studentRepository.findByLastName(lastName);
    }

    public List<Student> searchByFirstName(String keyword) {
        return studentRepository.findByFirstNameContainingIgnoreCase(keyword);
    }

    public List<Student> findByEmailDomain(String domain) {
        return studentRepository.findByEmailDomain(domain);
    }
}


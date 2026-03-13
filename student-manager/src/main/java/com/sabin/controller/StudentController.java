package com.sabin.controller;

import com.sabin.entity.Student;
import com.sabin.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // GET all
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // POST create
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student saved = studentService.createStudent(student);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
        //It explicitly sets the status code to 201.
        //201 Created is the correct REST response when a new record is successfully created.
    }

 // PUT update
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") Long id,
                                                 @RequestBody Student student) {
        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // GET by lastName
    @GetMapping("/search/by-lastname")
    public List<Student> searchByLastName(@RequestParam("lastName") String lastName) {
        return studentService.findByLastName(lastName);
    }

    // GET by firstName contains
    @GetMapping("/search/by-firstname")
    public List<Student> searchByFirstName(@RequestParam("keyword") String keyword) {
        return studentService.searchByFirstName(keyword);
    }

    // GET by email domain
    @GetMapping("/search/by-domain")
    public List<Student> searchByDomain(@RequestParam("domain") String domain) {
        return studentService.findByEmailDomain(domain);
    }
}
/*| Method | Status Code     | Reason               |
| ------ | --------------- | -------------------- |
| GET    | 200 OK          | Data retrieved       |
| PUT    | 200 OK          | Data updated         |
| DELETE | 204 No Content  | Deleted              |
| POST   | **201 Created** | New resource created |
*/

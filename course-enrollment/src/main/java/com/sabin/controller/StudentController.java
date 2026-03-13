package com.sabin.controller;

import com.sabin.entity.Student;
import com.sabin.service.StudentService;
import com.sabin.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.listAll());
        return "students"; // templates/students.html
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("courses", courseService.listAll());
        return "student-form"; // templates/student-form.html
    }

    @PostMapping
    public String saveStudent(@Valid @ModelAttribute("student") Student student,
                              BindingResult result,
                              @RequestParam("courseId") Long courseId,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseService.listAll());
            return "student-form";
        }
        studentService.save(student, courseId);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getById(id);
        model.addAttribute("student", student);
        model.addAttribute("courses", courseService.listAll());
        return "student-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.delete(id);
        return "redirect:/students";
    }
}


package com.re.studentmanage.controller;

import com.re.studentmanage.entity.Student;
import com.re.studentmanage.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        if (student.getId() != null || hasMissingRequiredField(student)) {
            return ResponseEntity.badRequest().build();
        }

        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        Optional<Student> existingStudent = studentRepository.findById(id);
        if (existingStudent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (hasMissingRequiredField(student)) {
            return ResponseEntity.badRequest().build();
        }

        Student updatedStudent = existingStudent.get();
        updatedStudent.setFullName(student.getFullName());
        updatedStudent.setEmail(student.getEmail());
        updatedStudent.setGpa(student.getGpa());

        return ResponseEntity.ok(studentRepository.save(updatedStudent));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Student> patchStudent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Student> existingStudent = studentRepository.findById(id);
        if (existingStudent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = existingStudent.get();

        if (updates.containsKey("fullName")) {
            student.setFullName((String) updates.get("fullName"));
        }

        if (updates.containsKey("email")) {
            student.setEmail((String) updates.get("email"));
        }

        if (updates.containsKey("gpa")) {
            student.setGpa(toDouble(updates.get("gpa")));
        }

        return ResponseEntity.ok(studentRepository.save(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean hasMissingRequiredField(Student student) {
        return student.getFullName() == null || student.getFullName().isBlank()
                || student.getEmail() == null || student.getEmail().isBlank()
                || student.getGpa() == null;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        return Double.valueOf(value.toString());
    }
}

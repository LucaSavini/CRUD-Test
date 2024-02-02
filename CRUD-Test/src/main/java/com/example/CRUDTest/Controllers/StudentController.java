package com.example.CRUDTest.Controllers;

import com.example.CRUDTest.Entities.Student;
import com.example.CRUDTest.Repository.StudentRepository;
import com.example.CRUDTest.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/create")
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }
    @GetMapping("/getList")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/getStudent/{id}")
    public Student getStudentById(@PathVariable Long id) {
        Optional<Student> optionalStudent= studentRepository.findById(id);
        if(optionalStudent.isPresent()) {
            return optionalStudent.get();
        }else {
            return null;
        }
    }

    @PutMapping("updateStudent/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        return studentRepository.save(student);
    }

    @PatchMapping("updateStatus/{id}")
    public Student updateIsWorking(@PathVariable Long id, @RequestParam boolean working) {
        return studentService.updateIsWorking(id, working);
    }

    @DeleteMapping("deleteStudent/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentRepository.deleteById(id);
    }
}

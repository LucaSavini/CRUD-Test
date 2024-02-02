package com.example.CRUDTest.Service;

import com.example.CRUDTest.Entities.Student;
import com.example.CRUDTest.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public Student updateIsWorking(Long id, boolean isWorking) {
        Optional<Student> studentOptional = studentRepository.findById(id);

        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            student.setWorking(isWorking);
            return studentRepository.save(student);
        } else {
            throw new RuntimeException("Student not found");
        }
    }

}
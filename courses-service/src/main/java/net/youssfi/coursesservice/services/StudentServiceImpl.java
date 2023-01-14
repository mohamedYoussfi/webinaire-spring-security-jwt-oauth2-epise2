package net.youssfi.coursesservice.services;

import lombok.extern.slf4j.Slf4j;
import net.youssfi.coursesservice.entities.Student;
import net.youssfi.coursesservice.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Service
@Transactional
@Slf4j
public class StudentServiceImpl implements StudentService {
    private StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public Student addNewStudent(String firstName, String lastName, String userId){
        Student student=Student.builder()
                .id(UUID.randomUUID().toString())
                .firstName(firstName)
                .lastName(lastName)
                .userID(userId)
                .build();
        return studentRepository.save(student);
    }
}

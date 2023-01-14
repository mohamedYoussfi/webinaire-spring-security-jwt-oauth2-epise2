package net.youssfi.coursesservice.handlers;

import lombok.extern.slf4j.Slf4j;
import net.youssfi.coursesservice.dtos.UserDetailsRequestDTO;
import net.youssfi.coursesservice.entities.Student;
import net.youssfi.coursesservice.entities.Teacher;
import net.youssfi.coursesservice.repository.StudentRepository;
import net.youssfi.coursesservice.repository.TeacherRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@Transactional
@Slf4j
public class ClassRoomEventHandler {
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;

    public ClassRoomEventHandler(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @Bean
    public Consumer<UserDetailsRequestDTO> userDetailsConsumer(){
        return (input)->{
            log.info(input.toString());
            String userId=input.userId();
            Teacher teacher=teacherRepository.findByUserId(userId);
            if(teacher!=null){
                if(input.firstName()!=null) teacher.setFirstName(input.firstName());
                if(input.lastName()!=null) teacher.setLastName(input.lastName());
                if(input.email()!=null) teacher.setEmail(input.email());
                teacherRepository.save(teacher);
            }
            Student student=studentRepository.findByUserID(userId);
            if(student!=null){
                if(input.firstName()!=null) student.setFirstName(input.firstName());
                if(input.lastName()!=null) student.setLastName(input.lastName());
                if(input.email()!=null) student.setEmail(input.email());
                studentRepository.save(student);
            }
        };
    }
}

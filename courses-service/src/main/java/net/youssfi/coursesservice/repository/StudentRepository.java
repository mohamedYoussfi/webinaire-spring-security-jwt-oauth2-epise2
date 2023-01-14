package net.youssfi.coursesservice.repository;

import net.youssfi.coursesservice.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,String> {
    Student findByUserID(String userId);
}

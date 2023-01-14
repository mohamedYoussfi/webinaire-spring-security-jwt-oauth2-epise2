package net.youssfi.coursesservice.repository;

import net.youssfi.coursesservice.entities.Course;
import net.youssfi.coursesservice.entities.Student;
import net.youssfi.coursesservice.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course,String > {
    List<Course> findByTeacher(Teacher teacher);
}

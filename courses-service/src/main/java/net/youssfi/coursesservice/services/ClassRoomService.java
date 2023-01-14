package net.youssfi.coursesservice.services;

import net.youssfi.coursesservice.dtos.CourseRequestDTO;
import net.youssfi.coursesservice.dtos.StudentRequestDTO;
import net.youssfi.coursesservice.dtos.TeacherRequestDTO;
import net.youssfi.coursesservice.entities.Course;
import net.youssfi.coursesservice.entities.Student;
import net.youssfi.coursesservice.entities.Subscription;
import net.youssfi.coursesservice.entities.Teacher;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ClassRoomService {
    Teacher saveNewTeacher(TeacherRequestDTO teacherRequestDTO);

    Course saveCourse(CourseRequestDTO courseRequestDTO, MultipartFile file, String picturesBaseURL, String userId, String jwt) throws IOException;

    List<Teacher> allTeachers();

    List<Course> myCourses(String userId);

    List<Course> allCourses();

    Student saveNewStudent(StudentRequestDTO studentRequestDTO);

    Course updateCoursePhoto(MultipartFile file, String courseId, String baseURL, String userId) throws IOException;
    Subscription newSubscription(String userId, String courseId, String token);

    List<Subscription> getStudentSubscriptions(String userId);

    List<Subscription> courseSubscriptions(String courseId);

    Course getCourseById(String id);
}

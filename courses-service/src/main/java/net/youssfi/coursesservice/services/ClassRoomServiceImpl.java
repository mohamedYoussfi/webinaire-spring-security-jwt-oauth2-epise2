package net.youssfi.coursesservice.services;

import lombok.extern.slf4j.Slf4j;
import net.youssfi.coursesservice.dtos.CourseRequestDTO;
import net.youssfi.coursesservice.dtos.StudentRequestDTO;
import net.youssfi.coursesservice.dtos.TeacherRequestDTO;
import net.youssfi.coursesservice.entities.Course;
import net.youssfi.coursesservice.entities.Student;
import net.youssfi.coursesservice.entities.Subscription;
import net.youssfi.coursesservice.entities.Teacher;
import net.youssfi.coursesservice.feign.AddRoleToUserDTO;
import net.youssfi.coursesservice.feign.AppRole;
import net.youssfi.coursesservice.feign.AuthServiceRestClient;
import net.youssfi.coursesservice.repository.CourseRepository;
import net.youssfi.coursesservice.repository.StudentRepository;
import net.youssfi.coursesservice.repository.SubscriptionRepository;
import net.youssfi.coursesservice.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ClassRoomServiceImpl implements ClassRoomService {
    private TeacherRepository teacherRepository;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private SubscriptionRepository subscriptionRepository;
    private JwtDecoder jwtDecoder;

    private AuthServiceRestClient authServiceRestClient;

    @Value("${course.pictures.path}")
    private String coursePicturesPath;

    public ClassRoomServiceImpl(TeacherRepository teacherRepository, StudentRepository studentRepository, CourseRepository courseRepository, SubscriptionRepository subscriptionRepository, JwtDecoder jwtDecoder, AuthServiceRestClient authServiceRestClient) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.jwtDecoder = jwtDecoder;
        this.authServiceRestClient = authServiceRestClient;
    }

    @Override
    public Teacher saveNewTeacher(TeacherRequestDTO teacherRequestDTO){
        Teacher teacher=Teacher.builder()
                .id(UUID.randomUUID().toString())
                .firstName(teacherRequestDTO.firstName())
                .lastName(teacherRequestDTO.latName())
                .email(teacherRequestDTO.email())
                .userId(teacherRequestDTO.userId())
                .build();
        teacherRepository.save(teacher);
        return teacher;
    }

    @Override
    public Course saveCourse(CourseRequestDTO courseRequestDTO, MultipartFile file, String picturesBaseURL, String userId, String jwt) throws IOException {
        Teacher teacher=teacherRepository.findByUserId(userId);
        if(teacher==null){
            Jwt decodedJWT = jwtDecoder.decode(jwt);
            String username=decodedJWT.getClaim("username");
            teacher=Teacher.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .firstName(decodedJWT.getClaim("firstName"))
                    .lastName(decodedJWT.getClaim("lastName"))
                    .email(decodedJWT.getClaim("email"))
                    .build();
             teacherRepository.save(teacher);
            log.info("********* Invoking Auth Service to Add Role TEACHER to user "+username+" ******");
            AppRole appRole = authServiceRestClient.addRoleToUser(new AddRoleToUserDTO(username, "TEACHER"));
            log.info(String.format("Role %s has been added to user %s",appRole.roleName(),username));
        }
        Course course=new Course();
        course.setId(UUID.randomUUID().toString());
        course.setTitle(courseRequestDTO.title());
        course.setGroupName(courseRequestDTO.groupName());
        course.setTeacher(teacher);
        if(file!=null){
            String coursePictureId=UUID.randomUUID().toString();
            String courseFileName=coursePictureId+"_"+file.getOriginalFilename();
            Path path= Paths.get(coursePicturesPath);
            if(!Files.exists(path)){
                Files.createDirectory(path);
            }
            Files.write(Path.of(coursePicturesPath,courseFileName),file.getBytes());
            course.setFileName(courseFileName);
            if(picturesBaseURL!=null) course.setPictureURL(picturesBaseURL+"photo/"+course.getId());
        }
        return courseRepository.save(course);
    }
    @Override
    public List<Teacher> allTeachers(){
        return teacherRepository.findAll();
    }
    @Override
    public List<Course> myCourses(String userId){
        Teacher teacher=teacherRepository.findByUserId(userId);
        List<Course> courseList=new ArrayList<>();
        if (teacher!=null) {
            courseList=courseRepository.findByTeacher(teacher);
        }
        List<Subscription> subscriptions=subscriptionRepository.findByStudentUserID(userId);
        List<Course> studentCourses = subscriptions.stream().map(sub -> sub.getCourse()).collect(Collectors.toList());
        courseList.addAll(studentCourses);
        return courseList;
    }

    @Override
    public Course getCourseById(String id) {
        Course course=courseRepository.findById(id).orElse(null);
        if(course==null) throw new RuntimeException("Course not found");
        return course;
    }

    @Override
    public List<Course> allCourses(){
        return courseRepository.findAll();
    }

    @Override
    public Student saveNewStudent(StudentRequestDTO studentRequestDTO){
        String username=studentRequestDTO.email();
        Student student=Student.builder()
                .id(UUID.randomUUID().toString())
                .firstName(studentRequestDTO.firstName())
                .lastName(studentRequestDTO.lastName())
                .userID(studentRequestDTO.userId())
                .build();

        return studentRepository.save(student);
    }

    @Override
    public Course updateCoursePhoto(MultipartFile file, String courseId, String baseURL, String userId) throws IOException {
        Course course=courseRepository.findById(courseId).get();
        // Cheek if the course is the owner of  the current user
        if(!course.getTeacher().getUserId().equals(userId)){
            throw new RuntimeException("You can not update resource, this course is not yours");
        }
        String photoId= UUID.randomUUID().toString();
        String photoName=courseId+"_"+photoId+"_"+file.getOriginalFilename();
        Path photosDirectory= Paths.get(coursePicturesPath);
        if(!Files.exists(photosDirectory)){
            Files.createDirectory(photosDirectory);
        }
        Files.write(Path.of(coursePicturesPath,photoName),file.getBytes());
        course.setFileName(photoName);
        course.setPictureURL(baseURL);
        return courseRepository.save(course);
    }

    @Override
    public Subscription newSubscription(String userId, String courseId, String token) {
        Student student = studentRepository.findByUserID(userId);
        if(student==null){
            Jwt decodedJWT = jwtDecoder.decode(token);
            String username=decodedJWT.getClaim("username");
            student=Student.builder()
                    .id(UUID.randomUUID().toString())
                    .userID(userId)
                    .firstName(decodedJWT.getClaim("firstName"))
                    .lastName(decodedJWT.getClaim("lastName"))
                    .email(decodedJWT.getClaim("email"))
                    .build();
            studentRepository.save(student);
            log.info("********* Invoking Auth Service to Add Role STUDENT to user "+username+" ******");
            AppRole appRole = authServiceRestClient.addRoleToUser(new AddRoleToUserDTO(username, "STUDENT"));
            log.info(String.format("Role %s has been added to user %s",appRole.roleName(),username));
        }
        Course course=courseRepository.findById(courseId).get();
        Subscription subscription=Subscription.builder()
                .course(course)
                .student(student)
                .date(new Date())
                .build();
        return subscriptionRepository.save(subscription);
    }

    @Override
    public List<Subscription> getStudentSubscriptions(String userId) {
        return subscriptionRepository.findByStudentUserID(userId);
    }

    @Override
    public List<Subscription> courseSubscriptions(String courseId) {
        return subscriptionRepository.findByCourseId(courseId);
    }
}

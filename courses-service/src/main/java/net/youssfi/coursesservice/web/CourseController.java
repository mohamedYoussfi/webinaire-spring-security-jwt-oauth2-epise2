package net.youssfi.coursesservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.youssfi.coursesservice.dtos.CourseRequestDTO;
import net.youssfi.coursesservice.dtos.SubscriptionRequestDTO;
import net.youssfi.coursesservice.entities.Course;
import net.youssfi.coursesservice.entities.Subscription;
import net.youssfi.coursesservice.repository.CourseRepository;
import net.youssfi.coursesservice.services.ClassRoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin("*")
public class CourseController {
    private ClassRoomService classRoomService;
    private CourseRepository courseRepository;
    @Value("${course.pictures.path}")
    private String coursePhotoPath;

    public CourseController(ClassRoomService classRoomService, CourseRepository courseRepository) {
        this.classRoomService = classRoomService;
        this.courseRepository = courseRepository;
    }
    @GetMapping("/courses")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<Course> allCourses() {
        return classRoomService.allCourses();
    }

    @GetMapping("/courses/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public Course courseDetails(@PathVariable String id) {
        return classRoomService.getCourseById(id);
    }

    @GetMapping("/myCourses")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<Course> myCourses(Principal principal) {
        return classRoomService.myCourses(principal.getName());
    }

    @PostMapping(value = "/courses", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @SecurityRequirement(name = "Bearer Authentication")
    public Course newCourse(
            MultipartFile file,
            @Parameter(name = "course", schema = @Schema(implementation = CourseRequestDTO.class))
            String courseData, Principal principal, HttpServletRequest request) throws IOException {
        String authHeader=request.getHeader("Authorization");
        String jwt=authHeader.substring(7,authHeader.length()).trim();
        CourseRequestDTO courseRequestDTO=new ObjectMapper().readValue(courseData,CourseRequestDTO.class);
        return classRoomService.saveCourse(courseRequestDTO,file,request.getRequestURL().toString(), principal.getName(),jwt);
    }
    @GetMapping(value = "/courses/photo/{courseId}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getPhotoProfile(@PathVariable String courseId) throws IOException {
        Course course=courseRepository.findById(courseId).get();
        Path path= Paths.get(coursePhotoPath,course.getFileName());
        return Files.readAllBytes(path);
    }
    @PutMapping(path = "/courses/photo/{courseId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "Bearer Authentication")
    public Course updateCoursePhoto(
            @RequestParam MultipartFile photoFile,
            @PathVariable String courseId,
            HttpServletRequest request, Principal principal) throws IOException {
        return this.classRoomService.updateCoursePhoto(photoFile,courseId,request.getRequestURL().toString(),principal.getName());
    }
    @PostMapping("/courses/subscription")
    public Subscription newSubscription(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO, HttpServletRequest request){
        String authHeader=request.getHeader("Authorization");
        String jwt=authHeader.substring(7,authHeader.length()).trim();
        return classRoomService.newSubscription(subscriptionRequestDTO.userId(), subscriptionRequestDTO.courseId(), jwt);
    }
    @GetMapping("/courses/studentSubscriptions")
    public List<Subscription> mySubscriptions(Principal principal){
        return classRoomService.getStudentSubscriptions(principal.getName());
    }
    @GetMapping("/courses/courseSubscriptions/{courseId}")
    public List<Subscription> courseSubscriptions(@PathVariable String courseId){
        return classRoomService.courseSubscriptions(courseId);
    }
}

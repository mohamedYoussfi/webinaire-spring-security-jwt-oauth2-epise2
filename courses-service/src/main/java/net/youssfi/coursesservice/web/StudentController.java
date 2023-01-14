package net.youssfi.coursesservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.youssfi.coursesservice.dtos.MaterialRequestDTO;
import net.youssfi.coursesservice.dtos.StudentRequestDTO;
import net.youssfi.coursesservice.entities.Material;
import net.youssfi.coursesservice.entities.Student;
import net.youssfi.coursesservice.repository.MaterialRepository;
import net.youssfi.coursesservice.repository.StudentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin()
public class StudentController {
    private StudentRepository studentRepository;
    private MaterialRepository materialRepository;
    @Value("${student.photos.path}")
    private String profilePath;
    @Value("${student.materials.path}")
    private String materialsPath;
    public StudentController(StudentRepository studentRepository, MaterialRepository materialRepository) {
        this.studentRepository = studentRepository;
        this.materialRepository = materialRepository;
    }
    @PostMapping(path = "/students2",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Student saveStudent(@RequestParam MultipartFile photoFile,
                               @RequestParam List<MultipartFile> files,
                               @Parameter(name = "student", schema = @Schema(implementation = StudentRequestDTO.class))
                               @RequestPart
                               String model ) throws IOException {
        String studentId=UUID.randomUUID().toString();
        String photoId=UUID.randomUUID().toString();
        String photoName=studentId+"_"+photoId+"_"+photoFile.getOriginalFilename();
        Path photosDirectory=Paths.get(profilePath);
        if(!Files.exists(photosDirectory)){
            Files.createDirectory(photosDirectory);
        }
        Files.write(Path.of(profilePath,photoName),photoFile.getBytes());
        StudentRequestDTO studentRequestDTO=new ObjectMapper().readValue(model,StudentRequestDTO.class);
        Student student=new Student();
        BeanUtils.copyProperties(studentRequestDTO,student);
        student.setId(studentId);
        student.setPhotoFileName(photoName);
        Student savedStudent = studentRepository.save(student);

        Path path2=Paths.get(materialsPath);
        if(!Files.exists(path2)){
            Files.createDirectory(path2);
        }
        for (MultipartFile multipartFile:files){
            String materialName=studentId+"_"+photoId+"_"+multipartFile.getOriginalFilename();
            Files.write(Path.of(materialsPath,materialName),multipartFile.getBytes());
            Material material=new Material();
            material.setId(UUID.randomUUID().toString());
            material.setMaterialFileName(materialName);
            material.setStudent(savedStudent);
            materialRepository.save(material);
        }
        return studentRepository.findById(studentId).get();
    }

    @PostMapping(path = "/students",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Student saveNewStudent(
            @RequestParam MultipartFile photoFile,
            @Parameter(name = "student", schema = @Schema(implementation = StudentRequestDTO.class))
                               @RequestPart String studentData, HttpServletRequest request) throws IOException {
        String studentId=UUID.randomUUID().toString();
        String photoId=UUID.randomUUID().toString();
        String photoName=studentId+"_"+photoId+"_"+photoFile.getOriginalFilename();
        Path photosDirectory=Paths.get(profilePath);
        if(!Files.exists(photosDirectory)){
            Files.createDirectory(photosDirectory);
        }
        Files.write(Path.of(profilePath,photoName),photoFile.getBytes());
        StudentRequestDTO studentRequestDTO=new ObjectMapper().readValue(studentData,StudentRequestDTO.class);
        Student student=new Student();
        BeanUtils.copyProperties(studentRequestDTO,student);
        student.setId(studentId);
        student.setPhotoFileName(photoName);
        student.setPhotoURL(request.getRequestURL()+"/"+studentId+"/profile");
        Student savedStudent = studentRepository.save(student);
        return savedStudent;
    }

    @PostMapping(path = "/materials",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Material saveNewMaterial(
            @RequestParam MultipartFile materialFile,
            @Parameter(name = "material", schema = @Schema(implementation = MaterialRequestDTO.class))
            @RequestPart String materialData, HttpServletRequest request) throws IOException {
        MaterialRequestDTO materialRequestDTO=new ObjectMapper().readValue(materialData,MaterialRequestDTO.class);
        Student student=studentRepository.findById(materialRequestDTO.studentId()).get();
        String materialId=UUID.randomUUID().toString();
        String fileId=UUID.randomUUID().toString();
        String materialFileName=materialId+"_"+fileId+"_"+materialFile.getOriginalFilename();
        Path materialDirectory=Paths.get(materialsPath);
        if(!Files.exists(materialDirectory)){
            Files.createDirectory(materialDirectory);
        }
        Files.write(Path.of(materialsPath,materialFileName),materialFile.getBytes());
        Material material= Material.builder()
                .id(materialId)
                .label(materialRequestDTO.label())
                .student(student)
                .materialFileName(materialFileName)
                .materialURL(request.getRequestURL()+"/"+materialId+"/file")
                .build();
        Material savedMaterial = materialRepository.save(material);
        return savedMaterial;
    }

    @PutMapping(path = "/students/{id}/profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Student updateStudentPhotoProfile(
            @PathVariable String id,
            @RequestParam MultipartFile photoFile,
            HttpServletRequest request) throws IOException {
        Student student=studentRepository.findById(id).get();
        String photoId=UUID.randomUUID().toString();
        String photoName=id+"_"+photoId+"_"+photoFile.getOriginalFilename();
        Path photosDirectory=Paths.get(profilePath);
        if(!Files.exists(photosDirectory)){
            Files.createDirectory(photosDirectory);
        }
        Files.write(Path.of(profilePath,photoName),photoFile.getBytes());
        student.setPhotoFileName(photoName);
        student.setPhotoURL(request.getRequestURL().toString());
        Student savedStudent = studentRepository.save(student);
        return savedStudent;
    }

    @GetMapping("/students")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<Student> studentList(){
        return studentRepository.findAll();
    }
    @GetMapping("/students/{id}")
    public Student studentDetails(@PathVariable String id){
        return studentRepository.findById(id).get();
    }


    @GetMapping(value = "/students/{studentId}/profile", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getPhotoProfile(@PathVariable String studentId) throws IOException {
        Student student=studentRepository.findById(studentId).get();
        Path path=Paths.get(profilePath,student.getPhotoFileName());
        return Files.readAllBytes(path);
    }
    @GetMapping(value = "/materials/{materialId}/file", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getPhotoMaterial(@PathVariable String materialId) throws IOException {
        Material material=materialRepository.findById(materialId).get();
        Path path=Paths.get(materialsPath,material.getMaterialFileName());
        return Files.readAllBytes(path);
    }
}

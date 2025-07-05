package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping ("/student")

public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public Student getStudentInfo(@PathVariable Long id) {

       return studentService.findStudent(id);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudent() {
        return ResponseEntity.ok(studentService.getAllStudent());
    }
    @GetMapping("/age-between")
    public ResponseEntity<Collection<Student>> getStudentsByAgeRange(
            @RequestParam int min,
            @RequestParam int max) {
        Collection<Student> students = studentService.getStudentsByAgeBetween(min, max);
        return ResponseEntity.ok(students);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student updateStudent(@RequestBody Student student) {
        return studentService.updateStudent(student);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
    @GetMapping("/{studentId}/faculty")
    public Faculty getStudentFaculty(@PathVariable Long studentId) {
        return studentService.findStudent(studentId).getFaculty();
    }
    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("File is too big");
        }

        studentService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        Avatar avatar = studentService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }
    @GetMapping("/count")
    public Integer getStudentsCount() {
        return studentService.getTotalCountOfStudents();
    }

    @GetMapping("/avg-age")
    public Double getAverageAge() {
        return studentService.getAverageAgeOfStudents();
    }

    @GetMapping("/last-five")
    public List<Student> getLastFiveStudents() {
        return studentService.findLastFiveStudents();
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = studentService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream((java.nio.file.Path) path);
             OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }
}




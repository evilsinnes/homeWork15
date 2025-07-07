package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class StudentService {
    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);
    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
                this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }
    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student: {}", student);
        return studentRepository.save(student);
    }
    public Student updateStudent(Student student) {
        logger.info("Was invoked method for update student");
        logger.debug("Updating student with id: {}", student.getId());


        if (!studentRepository.existsById(student.getId())) {
            logger.warn("Student with id {} not found for update", student.getId());
            throw new StudentNotFoundException(student.getId());
        }
        return studentRepository.save(student);
    }
    public Student addStudent(Student student) {

        return studentRepository.save(student);
    }
    public Student findStudent (long id){
        return studentRepository.findById(id).get();
    }

    public Student editStudent (Student student) {

        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student with id: {}", id);

        if (!studentRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent student with id: {}", id);
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);
    }



    public Collection<Student> getAllStudent() {
        return studentRepository.findAll();
    }
    public Collection<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        logger.info("Was invoked method for get students by age between {} and {}", minAge, maxAge);
        logger.debug("Searching students with age range: {} - {}",minAge, maxAge);
                return studentRepository.findByAgeBetween(minAge, maxAge);
    }
    public Avatar findAvatar (long studetId) {
        return  avatarRepository.findByStudentId(studetId).orElseThrow();

    }



    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student id: {}", studentId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with id: {}", studentId);
                    return new StudentNotFoundException(studentId);
                });

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
        logger.debug("Avatar saved for student id: {}", studentId);
    }
    public Integer getTotalCountOfStudents() {
        return studentRepository.getTotalCountOfStudents();
    }

    public Double getAverageAgeOfStudents() {
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<Student> findLastFiveStudents() {
        return studentRepository.findLastFiveStudents();
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

  }




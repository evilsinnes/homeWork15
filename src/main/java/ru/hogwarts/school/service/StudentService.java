package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll();
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
    public List<String> getStudentNamesStartingWithA() {
        logger.info("Getting student names starting with A");
        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name.toUpperCase().startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
    }
    public Double getAverageAge() {
        logger.info("Calculating average age of students");
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    public String getLongestFacultyName() {
        logger.info("Finding longest faculty name");
        return studentRepository.findAll().stream()
                .map(student -> student.getFaculty().getName())
                .max(Comparator.comparingInt(String::length))
                .orElse("No faculties found");
    }

    public Integer calculateSum() {
        logger.info("Calculating sum with parallel streams");
        return Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .reduce(0, Integer::sum);
    }
    public void printStudentsParallel() {
        List<Student> students = studentRepository.findAll();

        if (students.size() >= 6) {
            System.out.println("Основной поток:");
            System.out.println(students.get(0).getName());
            System.out.println(students.get(1).getName());

            new Thread(() -> {
                System.out.println("Поток 1:");
                System.out.println(students.get(2).getName());
                System.out.println(students.get(3).getName());
            }).start();

            new Thread(() -> {
                System.out.println("Поток 2:");
                System.out.println(students.get(4).getName());
                System.out.println(students.get(5).getName());
            }).start();
        } else {
            System.out.println("Недостаточно студентов для вывода (требуется минимум 6)");
        }
    }

    public synchronized void printStudentsSynchronized() {
        List<Student> students = studentRepository.findAll();

        if (students.size() >= 6) {
            System.out.println("Основной поток (синхронизированный):");
            printName(students.get(0).getName());
            printName(students.get(1).getName());

            new Thread(() -> {
                System.out.println("Поток 1 (синхронизированный):");
                printName(students.get(2).getName());
                printName(students.get(3).getName());
            }).start();

            new Thread(() -> {
                System.out.println("Поток 2 (синхронизированный):");
                printName(students.get(4).getName());
                printName(students.get(5).getName());
            }).start();
        } else {
            System.out.println("Недостаточно студентов для вывода (требуется минимум 6)");
        }
    }

    private synchronized void printName(String name) {
        System.out.println(name);
    }
    public long optimizedСalculateSum(){
        logger.info("Was invoked method for calculate optimized sum");
        long n = 1_000_000;
        return n * (n + 1) / 2;
    }
  }




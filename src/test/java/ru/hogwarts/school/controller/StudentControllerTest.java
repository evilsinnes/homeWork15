package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/student";
    }

    @Test
    public void testCreateStudent() {
        Student student = new Student();
        student.setName("Гарри Поттер");
        student.setAge(15);

        ResponseEntity<Student> response = restTemplate.postForEntity(
                getBaseUrl(),
                student,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Гарри Поттер", response.getBody().getName());
    }

    @Test
    public void testGetStudentById() {
        Student student = new Student();
        student.setName("Гермиона Грейнджер");
        student.setAge(15);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                student,
                Student.class
        );

        Long studentId = createResponse.getBody().getId();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Гермиона Грейнджер", response.getBody().getName());
    }

    @Test
    public void testUpdateStudent() {
        Student student = new Student();
        student.setName("Рон Уизли");
        student.setAge(15);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                student,
                Student.class
        );

        Student createdStudent = createResponse.getBody();
        createdStudent.setAge(16);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Student> request = new HttpEntity<>(createdStudent, headers);

        ResponseEntity<Student> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.PUT,
                request,
                Student.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(16, response.getBody().getAge());
    }

    @Test
    public void testDeleteStudent() {
        Student student = new Student();
        student.setName("Невилл Лонгботтом");
        student.setAge(15);

        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                student,
                Student.class
        );

        Long studentId = createResponse.getBody().getId();

        restTemplate.delete(getBaseUrl() + "/" + studentId);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + studentId,
                Student.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetStudentsByAgeBetween() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/age-between?min=14&max=16",
                Student[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 0);
    }
}
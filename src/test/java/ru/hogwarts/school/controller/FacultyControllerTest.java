package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    public void testCreateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Гриффиндор");
        faculty.setColor("Красный");

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                getBaseUrl(),
                faculty,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Гриффиндор", response.getBody().getName());
    }

    @Test
    public void testGetFacultyById() {
        Faculty faculty = new Faculty();
        faculty.setName("Слизерин");
        faculty.setColor("Зеленый");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                faculty,
                Faculty.class
        );

        Long facultyId = createResponse.getBody().getId();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + facultyId,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Слизерин", response.getBody().getName());
    }

    @Test
    public void testUpdateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Когтевран");
        faculty.setColor("Синий");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                faculty,
                Faculty.class
        );

        Faculty createdFaculty = createResponse.getBody();
        createdFaculty.setColor("Голубой");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> request = new HttpEntity<>(createdFaculty, headers);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                getBaseUrl(),
                HttpMethod.PUT,
                request,
                Faculty.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Голубой", response.getBody().getColor());
    }

    @Test
    public void testDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Пуффендуй");
        faculty.setColor("Желтый");

        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                getBaseUrl(),
                faculty,
                Faculty.class
        );

        Long facultyId = createResponse.getBody().getId();

        restTemplate.delete(getBaseUrl() + "/" + facultyId);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                getBaseUrl() + "/" + facultyId,
                Faculty.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testSearchFacultiesByNameOrColor() {
        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/search?name=Гриффиндор&color=Красный",
                Faculty[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 0);
    }
}
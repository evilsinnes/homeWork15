package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    public void testGetStudentById() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Гарри Поттер");
        student.setAge(15);

        when(studentService.findStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гарри Поттер"))
                .andExpect(jsonPath("$.age").value(15));
    }

    @Test
    public void testCreateStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Гермиона Грейнджер");
        student.setAge(15);

        when(studentService.addStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Гермиона Грейнджер\",\"age\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Гермиона Грейнджер"))
                .andExpect(jsonPath("$.age").value(15));
    }

    @Test
    public void testUpdateStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Рон Уизли");
        student.setAge(16);

        when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Рон Уизли\",\"age\":16}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Рон Уизли"))
                .andExpect(jsonPath("$.age").value(16));
    }

    @Test
    public void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetStudentsByAgeBetween() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Невилл Лонгботтом");
        student.setAge(15);

        when(studentService.getStudentsByAgeBetween(14, 16))
                .thenReturn(Collections.singletonList(student));

        mockMvc.perform(get("/student/age-between?min=14&max=16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Невилл Лонгботтом"))
                .andExpect(jsonPath("$[0].age").value(15));
    }
}

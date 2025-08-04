package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

@RestController
@RequestMapping ("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public Faculty getFacultyInfo(@PathVariable Long id) {
       // Faculty faculty = facultyService.findFaculty(id);
                return facultyService.findFaculty(id);
    }
    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }
    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        if (foundFaculty ==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
       return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
       return ResponseEntity.ok().build();
    }
//    public ResponseEntity<Collection<Faculty>> findFaculties(@RequestParam(required = false) String color) {
//        if (color != null && !color.isBlank()) {
//            return ResponseEntity.ok(facultyService.findByColor(color));
//        }
//        return ResponseEntity.ok(Collections.emptyList());
//    }
@GetMapping
public ResponseEntity<Collection<Faculty>> getAllFaculty() {
    return ResponseEntity.ok(facultyService.getAllFaculty());
}
    @GetMapping("/search")
    public ResponseEntity<Collection<Faculty>> searchFaculties(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color) {
        Collection<Faculty> faculties = facultyService.findFacultiesByNameOrColor(name,color);
        return ResponseEntity.ok(faculties);
    }
    @GetMapping("/{facultyId}/students")
    public Collection<Student> getFacultyStudents(@PathVariable Long facultyId) {
        return facultyService.findFaculty(facultyId).getStudents();
    }
    @GetMapping("/faculties/longest-name")
    public String getLongestFacultyName() {
        return facultyService.getAllFaculty().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse("");
    }

}




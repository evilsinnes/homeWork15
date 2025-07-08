package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@Service
public class FacultyService {

    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty: {}", faculty);
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("Was invoked method for get faculty by id: {}", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Faculty not found with id: {}", id);
                    return new FacultyNotFoundException(id);
                });
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for update faculty");
        logger.debug("Updating faculty with id: {}", faculty.getId());

        if (!facultyRepository.existsById(faculty.getId())) {
            logger.warn("Faculty with id {} not found for update", faculty.getId());
            throw new FacultyNotFoundException(faculty.getId());
        }
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);

        if (!facultyRepository.existsById(id)) {
            logger.warn("Attempt to delete non-existent faculty with id: {}", id);
            throw new FacultyNotFoundException(id);
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculty() {

        return facultyRepository.findAll();
    }
    public Collection<Faculty> findFacultiesByNameOrColor(String name, String color) {
        logger.info("Was invoked method for find faculties by name or color");
        logger.debug("Searching faculties with name: {} or color: {}", name, color);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }
}


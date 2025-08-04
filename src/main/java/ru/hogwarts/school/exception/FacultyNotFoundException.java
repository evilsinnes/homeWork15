package ru.hogwarts.school.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FacultyNotFoundException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(FacultyNotFoundException.class);

    public FacultyNotFoundException(Long id) {
        super("Faculty with id " + id + " not found");
        logger.error("Faculty not found exception thrown for id: {}", id);
    }
}
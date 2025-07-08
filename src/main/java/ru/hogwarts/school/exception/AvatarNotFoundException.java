package ru.hogwarts.school.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvatarNotFoundException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(AvatarNotFoundException.class);

    public AvatarNotFoundException(Long studentId) {
        super("Avatar not found for student id: " + studentId);
        logger.error("Avatar not found for student id: {}", studentId);
    }

    public AvatarNotFoundException(String message) {
        super(message);
        logger.error("Avatar not found: {}", message);
    }
}
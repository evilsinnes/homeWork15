package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Value("${server.port}")
    private int serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/port")
    public String getPort() {
        return String.format(
                "Application is running on port: %d (Active profile: %s)",
                serverPort,
                activeProfile
        );
    }

    @GetMapping("/profile")
    public String getActiveProfile() {
        return "Current active profile: " + activeProfile;
    }
}
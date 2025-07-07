package ru.hogwarts.school.controller;

import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final ServletWebServerApplicationContext webServerAppCtxt;

    public InfoController(ServletWebServerApplicationContext webServerAppCtxt) {
        this.webServerAppCtxt = webServerAppCtxt;
    }

    @GetMapping("/port")
    public int getPort() {
        return webServerAppCtxt.getWebServer().getPort();
    }
}
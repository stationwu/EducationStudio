package com.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentController {
    public final static String STUDENT_NEW_PATH = "/user/student/new";

    @GetMapping(STUDENT_NEW_PATH)
    public String createStudent() {
        return "student_register"; // serve the page
    }
}

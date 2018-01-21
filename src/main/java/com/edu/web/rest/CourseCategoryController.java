package com.edu.web.rest;

import com.edu.dao.CourseCategoryRepository;
import com.edu.domain.CourseCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


import java.util.List;

@RestController
public class CourseCategoryController {
    @Autowired
    private CourseCategoryRepository repository;

    public static final String PATH = "/api/v1/CourseCategory";

    @GetMapping(path = PATH)
    public ResponseEntity<List<CourseCategory>> show(HttpSession session) {
    	List<CourseCategory> courseCategories = repository.findAllByOrderByPriority();
        return new ResponseEntity<>(courseCategories, HttpStatus.OK);
    }

}

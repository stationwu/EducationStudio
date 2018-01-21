package com.edu.web.rest;

import com.edu.dao.CourseCategoryRepository;
import com.edu.domain.CourseCategory;
import com.edu.domain.dto.CourseCategoryContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseCategoryController {
	@Autowired
	private CourseCategoryRepository repository;

	public static final String PATH = "/api/v1/DemoCourseCategory";

	@GetMapping(path = PATH)
	public ResponseEntity<List<CourseCategoryContainer>> getCourseCategories(HttpSession session) {
		List<CourseCategory> courseCategories = repository.getDemoCourseCategoryList();
		List<CourseCategoryContainer> courseCategoryContainers = courseCategories.stream()
				.map(x -> new CourseCategoryContainer(x, 0)).collect(Collectors.toCollection(ArrayList::new));
		return new ResponseEntity<>(courseCategoryContainers, HttpStatus.OK);
	}

}

package com.edu.web.rest;

import com.edu.dao.CourseCategoryRepository;
import com.edu.dao.StudentRepository;
import com.edu.domain.CourseCategory;
import com.edu.domain.Student;
import com.edu.domain.dto.BookedCourseCategoryContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CourseCategoryController {
	@Autowired
	private CourseCategoryRepository courseCategoryRepository;

	@Autowired
	private StudentRepository studentRepository;

	public static final String PATH = "/api/v1/CourseCategory";

	@GetMapping(path = PATH)
	public ResponseEntity<List<BookedCourseCategoryContainer>> getCourseCategories(
			@RequestParam(value = "studentId") String studentId, HttpSession session) {
		List<CourseCategory> courseCategories = courseCategoryRepository.getAllCourseCategoryList();
		Student student = studentRepository.findOne(studentId);
		Map<CourseCategory, Integer> courseMap = student.getCourseCount();
		List<BookedCourseCategoryContainer> bookedCourseCategoryContainers = courseCategories.stream()
				.map(x -> courseMap.get(x) != null ? new BookedCourseCategoryContainer(x, courseMap.get(x))
						: new BookedCourseCategoryContainer(x, 0))
				.collect(Collectors.toCollection(ArrayList::new));

		return new ResponseEntity<>(bookedCourseCategoryContainers, HttpStatus.OK);
	}

}

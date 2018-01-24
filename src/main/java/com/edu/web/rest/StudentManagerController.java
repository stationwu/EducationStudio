package com.edu.web.rest;

import com.edu.dao.CourseRepository;
import com.edu.dao.StudentRepository;
import com.edu.domain.Course;
import com.edu.domain.CourseCategory;
import com.edu.domain.Image;
import com.edu.domain.Student;
import com.edu.domain.dto.ChildContainer;
import com.edu.utils.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

@RestController
public class StudentManagerController {
	public static final String PATH = "/api/v1/StudentManager";
	public static final String STUDENT_PATH = PATH + "/{id}";
	public final static String SEARCH_STUDENT_PATH = PATH + "/search";
	private final StudentRepository studentRepository;
	private final ImageService imageService;
	private final CourseRepository courseRepository;

	@Autowired
	public StudentManagerController(StudentRepository studentRepository, ImageService imageService,
			CourseRepository courseRepository) {
		this.studentRepository = studentRepository;
		this.imageService = imageService;
		this.courseRepository = courseRepository;
	}

	@PostMapping(SEARCH_STUDENT_PATH)
	public List<ChildContainer> searchStudent(@RequestParam(value = "keyword") String keyword, HttpSession session) {
		List<Student> students = studentRepository.search(keyword);
		List<ChildContainer> childContainers = students.stream().map(x -> new ChildContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		return childContainers;
	}

	@RequestMapping(path = STUDENT_PATH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ChildContainer> show(@PathVariable("studentId") String studentId) {
		Student entity = studentRepository.findOne(studentId);
		return new ResponseEntity<>(new ChildContainer(entity), HttpStatus.OK);
	}

	@PostMapping(path = STUDENT_PATH)
	public HttpEntity<ChildContainer> signAndUploadImage(@PathVariable("id") String studentId,
			@RequestParam(value = "imageName") String imageName, @RequestParam(value = "date") String date,
			@RequestParam(value = "hour") String hour, @RequestParam(value = "material") String material,
			@RequestParam(value = "teacher") String teacher, @RequestParam("file") MultipartFile files[])
			throws Exception {
		Student student = studentRepository.findOne(studentId);
		@SuppressWarnings("unchecked")
		List<Course> courses = (List<Course>) courseRepository.search(date, hour, new PageRequest(0, 1));
		Course course = courses.get(0);
		for (Course cou : student.getCoursesSet()) {
			if (course.getId() == cou.getId()) {
				throw new Exception("已签到过此次课程！");
			}
		}
		student.addCourse(course);
		student.removeReservedCourse(course);
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				Image img = imageService.saveIn3Size(imageName, material, student, course, file);
				student.addImage(img);
			}
		}

		for (Entry<CourseCategory, Integer> entry : student.getCourseCount().entrySet()) {
			if (entry.getKey().getId() == course.getCourseCategory().getId()) {
				entry.setValue(entry.getValue() - 1);
			}
		}

		Student entity = studentRepository.save(student);
		return new ResponseEntity<>(new ChildContainer(entity), HttpStatus.OK);
	}

}

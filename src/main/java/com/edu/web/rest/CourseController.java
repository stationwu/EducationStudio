package com.edu.web.rest;

import com.edu.dao.CourseCategoryRepository;
import com.edu.dao.CourseRepository;
import com.edu.dao.StudentRepository;
import com.edu.domain.Course;
import com.edu.domain.CourseCategory;
import com.edu.domain.Student;
import com.edu.domain.dto.ChildContainer;
import com.edu.domain.dto.CourseContainer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CourseController {
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public static final String AVAILABLE_PATH = "/api/v1/AvailableCourse";
    
    public static final String ALL_BEFORE_PATH = "/api/v1/AllBeforeCourse";
    
    public static final String ALL_PATH = "/api/v1/AllCourse";
    
    public static final String BOOK_PATH = AVAILABLE_PATH + "/book";

    @PostMapping(path = AVAILABLE_PATH)
    public ResponseEntity<List<CourseContainer>> getCoursesByCourseCategory(
            @RequestParam(value = "courseCategoryId") Long courseCategoryId, HttpSession session) {
        CourseCategory courseCategory = courseCategoryRepository.findOne(courseCategoryId);
        String localDate = LocalDate.now().toString();
        List<CourseContainer> list = courseCategory.getCourses().stream()
                .filter(x -> localDate.compareTo(x.getDate()) >= 0)
                .sorted(Comparator.comparing(Course::getDate).thenComparing(Course::getTimeFrom))
                .map(x -> new CourseContainer(x)).collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @PostMapping(path = ALL_PATH)
    public ResponseEntity<List<CourseContainer>> getAllCoursesByCourseCategory(
            @RequestParam(value = "courseCategoryId") Long courseCategoryId, HttpSession session) {
        CourseCategory courseCategory = courseCategoryRepository.findOne(courseCategoryId);
        List<CourseContainer> list = courseCategory.getCourses().stream()
                .sorted(Comparator.comparing(Course::getDate).thenComparing(Course::getTimeFrom))
                .map(x -> new CourseContainer(x)).collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    @GetMapping(path = ALL_BEFORE_PATH)
    public ResponseEntity<List<CourseContainer>> getAllBeforeCoursesByCourseCategory(
            @RequestParam(value = "courseCategoryId") Long courseCategoryId, HttpSession session) {
    	String localDate = LocalDate.now().toString();
        CourseCategory courseCategory = courseCategoryRepository.findOne(courseCategoryId);
        List<CourseContainer> list = courseCategory.getCourses().stream().filter(x -> localDate.compareTo(x.getDate()) <= 0)
                .sorted(Comparator.comparing(Course::getDate).thenComparing(Course::getTimeFrom).reversed())
                .map(x -> new CourseContainer(x)).collect(Collectors.toCollection(ArrayList::new));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(path = BOOK_PATH)
    public ResponseEntity<ChildContainer> bookCourse(@RequestParam(value = "studentId") String studentId,
                                                     @RequestParam(value = "courseId") Long courseId, HttpSession session) {
        Course course = courseRepository.findOne(courseId);
        Student student = studentRepository.findOne(studentId);
        student.addReservedCourse(course);
        ChildContainer childContainer = new ChildContainer(studentRepository.save(student));
        return new ResponseEntity<>(childContainer, HttpStatus.OK);
    }

}

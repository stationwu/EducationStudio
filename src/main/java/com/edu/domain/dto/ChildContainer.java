package com.edu.domain.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.edu.domain.Course;
import com.edu.domain.Image;
import com.edu.domain.Student;

import lombok.Data;

@Data
public class ChildContainer {
	private String id;
	private String childName;
	private String birthday;
	private Student.Gender gender;
	private List<CourseCategoryContainer> courseCategoryContainers;
	private List<CourseContainer> reversedCourses;
	private List<CourseContainer> usedCourses;
	private List<ImageContainer> imageContainers;

	public ChildContainer(Student student) {
		this.childName = student.getStudentName();
		this.birthday = student.getBirthday();
		this.gender = student.getGender();
		this.id = student.getId();
		this.courseCategoryContainers = student.getCourseCount().entrySet().stream()
				.map(x -> new CourseCategoryContainer(x.getKey(), x.getValue()))
				.collect(Collectors.toCollection(ArrayList::new));
		this.reversedCourses = student.getReservedCoursesSet().stream()
				.sorted((x, y) -> Long.compare(y.getId(), x.getId())).map(x -> new CourseContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		this.usedCourses = student.getCoursesSet().stream().sorted((x, y) -> Long.compare(y.getId(), x.getId()))
				.map(x -> new CourseContainer(x)).collect(Collectors.toCollection(ArrayList::new));
		this.imageContainers = student.getImagesSet().stream().sorted(Comparator.comparing(Image::getId).reversed())
				.map(x -> new ImageContainer(x)).collect(Collectors.toCollection(ArrayList::new));
	}
}

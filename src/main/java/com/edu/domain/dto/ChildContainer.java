package com.edu.domain.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.edu.domain.CourseProduct;
import com.edu.domain.Image;
import com.edu.domain.Student;

import lombok.Data;

@Data
public class ChildContainer {
	private String id;
	private String childName;
	private String birthday;
	private Student.Gender gender;
	private int age;
	private List<CourseCategoryContainer> courseCategories;
	private List<CourseContainer> reversedCourses;
	private List<CourseContainer> usedCourses;
	private List<PaintContainer> paints;

	public ChildContainer() {

	}

	public ChildContainer(Student student) {
		this.childName = student.getStudentName();
		this.birthday = student.getBirthday();
		this.gender = student.getGender();
		this.id = student.getId();
		LocalDate localDate = LocalDate.now();
		this.age = Integer.parseInt(localDate.toString().substring(0, 4))
				- Integer.parseInt(this.birthday.substring(0, 4));
		Set<CourseProduct> courseProducts = student.getCourseProducts();
		Map<Long, Long> totalCourseCount = courseProducts.stream()
				.collect(Collectors.groupingBy(x -> x.getId(), Collectors.counting()));
		this.courseCategories = student.getCourseCount().entrySet().stream()
				.map(x -> new CourseCategoryContainer(x.getKey(), x.getValue(),
						totalCourseCount.get(x.getKey().getId()) == null ? x.getValue()
								: totalCourseCount.get(x.getKey().getId()).intValue()))
				.collect(Collectors.toCollection(ArrayList::new));
		this.reversedCourses = student.getReservedCoursesSet().stream()
				.sorted((x, y) -> Long.compare(y.getId(), x.getId())).map(x -> new CourseContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		this.usedCourses = student.getCoursesSet().stream().sorted((x, y) -> Long.compare(y.getId(), x.getId()))
				.map(x -> new CourseContainer(x)).collect(Collectors.toCollection(ArrayList::new));
		this.paints = student.getImagesSet().stream().sorted(Comparator.comparing(Image::getId).reversed())
				.map(x -> new PaintContainer(x, student)).collect(Collectors.toCollection(ArrayList::new));
	}
}

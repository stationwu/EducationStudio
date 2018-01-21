package com.edu.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.edu.domain.CourseCategory;

import lombok.Data;

@Data
public class CourseCategoryContainer {
	private long id;

	private String courseName;

	private int period;

	private List<ImageContainer> images;

	private int priority;

	CourseCategoryContainer(CourseCategory courseCategory, Integer count) {
		this.id = courseCategory.getId();
		this.courseName = courseCategory.getCourseName();
		this.period = count;
		this.images = courseCategory.getImages().stream().map(x -> new ImageContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		this.priority = courseCategory.getPriority();
	}

}

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

	private int leftPeriod;
	
	private int totalPeriod;

	private List<ImageContainer> images;

	private int priority;
	
	public CourseCategoryContainer(){
		
	}

	public CourseCategoryContainer(CourseCategory courseCategory, Integer leftPeriod, Integer totalPeriod) {
		this.id = courseCategory.getId();
		this.courseName = courseCategory.getCourseName();
		this.leftPeriod = leftPeriod;
		this.totalPeriod = totalPeriod;
		this.images = courseCategory.getImages().stream().map(x -> new ImageContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		this.priority = courseCategory.getPriority();
	}

}

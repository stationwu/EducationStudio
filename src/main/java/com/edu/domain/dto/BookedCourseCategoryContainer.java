package com.edu.domain.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.edu.domain.CourseCategory;

import lombok.Data;

@Data
public class BookedCourseCategoryContainer {
	private long id;

	private String courseName;

	private int leftPeriod;

	private List<ImageContainer> images;

	private int priority;
	
	private boolean isDemoCourse;
	
	private boolean isValid;

	public BookedCourseCategoryContainer(CourseCategory courseCategory, Integer count) {
		this.id = courseCategory.getId();
		this.courseName = courseCategory.getCourseName();
		this.leftPeriod = count;
		this.isDemoCourse = courseCategory.isDemoCourse();
		this.images = courseCategory.getImages().stream().map(x -> new ImageContainer(x))
				.collect(Collectors.toCollection(ArrayList::new));
		this.priority = courseCategory.getPriority();
		if(this.isDemoCourse == true && count >0){
			this.isValid = false;
		}else{
			this.isValid = true;
		}
	}

}

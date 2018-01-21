package com.edu.domain.dto;

import com.edu.domain.Course;

import lombok.Data;

@Data
public class CourseContainer {
	private long id;
	
	private CourseCategoryContainer courseCategoryContainer;

	private String date;

	private String timeFrom;

	private String timeTo;
	
	private int bookedSeat;
	
	private int maxSeat;

	public CourseContainer(Course course) {
		this.id= course.getId();
		this.courseCategoryContainer = new CourseCategoryContainer(course.getCourseCategory(), 0);
		this.timeFrom = course.getTimeFrom();
		this.timeTo = course.getTimeTo();
		this.date = course.getDate();
		this.bookedSeat = course.getReservedStudentsSet().size();
		this.maxSeat = course.getMaxSeat();
	}

}

package com.edu.domain.dto;

import com.edu.domain.Course;

import lombok.Data;

@Data
public class CourseContainer {
	private long id;
	
	private long courseCategoryId;
	
	private String courseName;

	private String date;

	private String timeFrom;

	private String timeTo;
	
	private int bookedSeat;
	
	private int maxSeat;
	
	public CourseContainer(){
		
	}

	public CourseContainer(Course course) {
		this.id= course.getId();
		this.timeFrom = course.getTimeFrom();
		this.timeTo = course.getTimeTo();
		this.date = course.getDate();
		this.bookedSeat = course.getReservedStudentsSet().size();
		this.maxSeat = course.getMaxSeat();
		this.courseCategoryId = course.getCourseCategory().getId();
		this.courseName = course.getCourseCategory().getCourseName();
	}

}

package com.edu.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courseprod")
public class CourseProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
	@ManyToOne
	private Student student;
    
    @ManyToOne
	private CourseCategory courseCategory;

    private LocalDateTime startFrom;

    private LocalDateTime endAt;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CourseCategory getCourseCategory() {
		return courseCategory;
	}

	public void setCourseCategory(CourseCategory courseCategory) {
		this.courseCategory = courseCategory;
	}

	public LocalDateTime getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(LocalDateTime startFrom) {
		this.startFrom = startFrom;
	}

	public LocalDateTime getEndAt() {
		return endAt;
	}

	public void setEndAt(LocalDateTime endAt) {
		this.endAt = endAt;
	}
}

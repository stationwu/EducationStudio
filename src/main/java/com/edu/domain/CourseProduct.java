package com.edu.domain;

import javax.persistence.*;

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

}

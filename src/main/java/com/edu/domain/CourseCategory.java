package com.edu.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "coursecategory")
public class CourseCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String courseName;

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "courseCategory")
	@JsonIgnore
	private Set<CourseProduct> courseProducts = new HashSet<>();

	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "courseCategory")
	@JsonIgnore
	private Set<Course> courses = new HashSet<>();

	private int period;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@OneToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Image> images;

	private int priority;

	private boolean isDemoCourse;

	CourseCategory() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Set<CourseProduct> getCourseProducts() {
		return courseProducts;
	}

	public void setCourseProducts(Set<CourseProduct> courseProducts) {
		this.courseProducts = courseProducts;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public boolean isDemoCourse() {
		return isDemoCourse;
	}

	public void setDemoCourse(boolean isDemoCourse) {
		this.isDemoCourse = isDemoCourse;
	}

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}

	public CourseCategory(String courseName, int period, BigDecimal price, Set<Image> productImages, int priority,
			boolean isDemoCourse) {
		super();
		this.courseName = courseName;
		this.period = period;
		this.price = price;
		this.images = productImages;
		this.priority = priority;
		this.isDemoCourse = isDemoCourse;
	}
}

package com.edu.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order line item
 */
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

    @Min(1)
    private int quantity;

    private BigDecimal subTotalAmount;

    private LocalDateTime startFrom;

    private LocalDateTime endAt;

    @ManyToOne
    private Address address;

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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getSubTotalAmount() {
		return subTotalAmount;
	}

	public void setSubTotalAmount(BigDecimal subTotalAmount) {
		this.subTotalAmount = subTotalAmount;
	}

	public LocalDateTime getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(LocalDateTime startFrom) {
		this.startFrom = startFrom;
	}

	public String getAddressText() {
		return address.getAddressText();
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public LocalDateTime getEndAt() {
		return endAt;
	}

	public void setEndAt(LocalDateTime endAt) {
		this.endAt = endAt;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CourseProduct that = (CourseProduct) o;

		return id == that.id;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}
}

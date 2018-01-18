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

    private int period;
    
    @Column(nullable= false, precision=10, scale=2)
    private BigDecimal price;
    
    @OneToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Image> productImages;

    CourseCategory(){
    	
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

	public Set<Image> getProductImages() {
		return productImages;
	}

	public void setProductImages(Set<Image> productImages) {
		this.productImages = productImages;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public CourseCategory(String courseName, int period, BigDecimal price,
			Set<Image> productImages) {
		super();
		this.courseName = courseName;
		this.period = period;
		this.price = price;
		this.productImages = productImages;
	}
}

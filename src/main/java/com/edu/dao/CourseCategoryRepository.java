package com.edu.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.edu.domain.CourseCategory;

public interface CourseCategoryRepository extends CrudRepository<CourseCategory, Long> {
	List<CourseCategory> findAllByOrderByPriority();
}

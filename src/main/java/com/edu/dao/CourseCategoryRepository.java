package com.edu.dao;

import org.springframework.data.repository.CrudRepository;

import com.edu.domain.CourseCategory;

public interface CourseCategoryRepository extends CrudRepository<CourseCategory, Long> {

}

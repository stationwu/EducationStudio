package com.edu.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.edu.domain.CourseCategory;

public interface CourseCategoryRepository extends CrudRepository<CourseCategory, Long> {
	@Query("select s from CourseCategory s order by s.priority desc")
	List<CourseCategory> getAllCourseCategoryList();
	
	@Query("select s from CourseCategory s where s.isDemoCourse = TRUE order by s.priority desc")
	List<CourseCategory> getDemoCourseCategoryList();
}

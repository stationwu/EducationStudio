package com.edu.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.edu.domain.Student;

import lombok.Data;

@Data
public class ChildContainer {
    private String childName;
    private String birthday;
    private Student.Gender gender;
    private List<CourseCategoryContainer> courseCategoryContainers = new ArrayList<>();
    public ChildContainer(Student student){
    	this.childName = student.getStudentName();
    	this.birthday = student.getBirthday();
    	this.gender = student.getGender();
    	student.getCourseCount().forEach((x,y) -> courseCategoryContainers.add(new CourseCategoryContainer(x,y)));
    }
}

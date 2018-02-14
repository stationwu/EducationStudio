package com.edu.domain.dto;

import com.edu.domain.Course;
import com.edu.domain.Image;
import com.edu.domain.Student;

import lombok.Data;

@Data
public class PaintContainer {
	private long id;

    private String imageName;
    
    private long courseCategoryId;

    private String date;

    private String imageUrl;

    private String thumbnailUrl;

    private String createdBy;

    private String material;
    
    private String teacher;
    
    public PaintContainer(){
    	
    }
    
    public PaintContainer(long id, String imageName, String date, Course course,
            String imageUrl, String thumbnailUrl) {
        super();
        this.id = id;
        this.imageName = imageName;
        this.date = date;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public PaintContainer(Image image) {
        super();
        this.id = image.getId();
        this.imageName = image.getImageName();
        this.date = image.getDate();
        this.material = image.getMaterial();
        this.imageUrl = "/Images/" + image.getId();
        this.thumbnailUrl = "/Images/" + image.getId() + "/thumbnail";
        this.teacher = image.getTeacher();
        this.courseCategoryId = image.getCourse().getCourseCategory().getId();
    }

    public PaintContainer(Image x, Student student) {
        this(x);
        this.createdBy = student.getStudentName();
    }
}

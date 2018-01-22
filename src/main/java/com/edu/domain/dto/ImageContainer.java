package com.edu.domain.dto;

import com.edu.domain.Course;
import com.edu.domain.Image;
import com.edu.domain.Student;

import lombok.Data;

@Data
public class ImageContainer {
	private long id;

    private String imageName;

    private String date;

    private String imageUrl;

    private String thumbnailUrl;

    private String createdBy;

    private String material;
    
    private String teacher;
    
    private int priority;

    public ImageContainer(long id, String imageName, String date, Course course,
            String imageUrl, String thumbnailUrl) {
        super();
        this.id = id;
        this.imageName = imageName;
        this.date = date;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public ImageContainer(Image image) {
        super();
        this.id = image.getId();
        this.imageName = image.getImageName();
        this.date = image.getDate();
        this.material = image.getMaterial();
        this.imageUrl = "/Images/" + image.getId();
        this.thumbnailUrl = "/Images/" + image.getId() + "/thumbnail";
        this.priority = image.getPriority();
        this.teacher = image.getTeacher();
    }

    public ImageContainer(Image x, Student student) {
        this(x);
        this.createdBy = student.getStudentName();
    }
}

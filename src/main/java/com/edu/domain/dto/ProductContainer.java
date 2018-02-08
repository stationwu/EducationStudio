package com.edu.domain.dto;

import com.edu.domain.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProductContainer {

    private long id;

    private String productName;

    private String category;

    private BigDecimal productPrice;

    private String productDescription;

    private String imageUrl;

    private int quantity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime startFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime endAt;

    private String longProductDescription;

    public static enum ProductType {
        PRODUCT, DERIVED, IMAGE_COLLECTION, COURSE
    }

    private ProductType type;

    private int priority;

    private int classPeriod;

    private final boolean checked = false;

    public ProductContainer() {

    }

    public ProductContainer(CourseProduct product, int quantity) {
        this.productName = product.getCourseCategory().getCourseName();

        Set<Image> images = product.getCourseCategory().getImages();
        if (!images.isEmpty()) {
            this.imageUrl = "/Images/" + images.stream().findFirst().get().getId() + "/thumbnail";
        }

        this.productPrice = product.getCourseCategory().getPrice();
        this.type = ProductType.COURSE;
        this.quantity = quantity;
        this.startFrom = product.getStartFrom();
        this.endAt = product.getEndAt();
        this.id = product.getId();
    }
}

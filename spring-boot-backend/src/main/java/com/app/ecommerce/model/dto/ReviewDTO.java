package com.app.ecommerce.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private String username;
    private List<String> imageUrls;
}
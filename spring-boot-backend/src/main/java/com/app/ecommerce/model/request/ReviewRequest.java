package com.app.ecommerce.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
public class ReviewRequest {
    private Long productId;
    private String heading;
    private String content;
    private int rating;
    private List<MultipartFile> images;
}

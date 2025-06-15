package com.app.ecommerce.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponse {
    private String userName;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
}

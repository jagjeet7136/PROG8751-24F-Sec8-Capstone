package com.app.ecommerce.modules.product.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ProductUpdateRequest {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Size(max = 5000, message = "Long description cannot exceed 5000 characters")
    private String longDescription;

    @PositiveOrZero(message = "Discounted price cannot be negative.")
    private BigDecimal discountedPrice;

    @Positive(message = "Price must be greater than zero.")
    private BigDecimal price;

    @Pattern(regexp = "^(http|https)://.*", message = "Image URL must be a valid URL")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    @Positive(message = "Category Id should be a positive number")
    private Long categoryId;
}

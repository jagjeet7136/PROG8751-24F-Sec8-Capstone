package com.app.ecommerce.model.request;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required.")
    private String name;

    @NotBlank(message = "Product description is required.")
    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    @NotBlank(message = "Product long description is required.")
    private String longDescription;

    @NotNull(message = "Discounted price is required.")
    @PositiveOrZero(message = "Discounted price cannot be negative.")
    private Double discountedPrice;

    @NotNull(message = "Original price is required.")
    @Positive(message = "Price must be greater than zero.")
    private Double price;

    @NotBlank(message = "Image URL is required.")
    @Pattern(regexp = "^(http|https)://.*", message = "Image URL must be a valid URL.")
    private String imageUrl;

    @Min(value = 0, message = "Stock cannot be negative.")
    private int stock;

    @NotBlank(message = "Category name is required.")
    private String categoryName;
}

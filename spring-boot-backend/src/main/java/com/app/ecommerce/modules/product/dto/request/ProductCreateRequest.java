package com.app.ecommerce.modules.product.dto.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreateRequest {

    @NotBlank(message = "Product name is required.")
    @Size(max = 255, message = "Name cannot exceed 255 characters.")
    private String name;

    @NotBlank(message = "Product description is required.")
    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    @NotBlank(message = "Product long description is required.")
    private String longDescription;

    @PositiveOrZero(message = "Discounted price cannot be negative.")
    private BigDecimal discountedPrice;

    @NotNull(message = "Original price is required.")
    @Positive(message = "Price must be greater than zero.")
    private BigDecimal price;

    @NotBlank(message = "Image URL is required.")
    @Pattern(regexp = "^(https?://).+$", message = "Image URL must be a valid URL.")
    private String imageUrl;

    @NotNull(message = "Stock is required.")
    @Min(value = 0, message = "Stock cannot be negative.")
    private Integer stock;

    @NotNull(message = "Category ID is required.")
    private Long categoryId;
}

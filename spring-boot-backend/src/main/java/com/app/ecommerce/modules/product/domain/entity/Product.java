package com.app.ecommerce.modules.product.domain.entity;

import com.app.ecommerce.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required.")
    @Size(max = 255, message = "Name cannot exceed 255 characters.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Product description is required.")
    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    @Size(max = 500, message = "Long description cannot exceed 255 characters.")
    @Column(columnDefinition = "LONGTEXT")
    private String longDescription;

    @Column(precision = 10, scale = 2)
    @PositiveOrZero(message = "Discounted price cannot be negative.")
    private BigDecimal discountedPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Original price is required.")
    @Positive(message = "Price must be greater than zero.")
    private BigDecimal price;

    @Column(nullable = false)
    @NotBlank(message = "Image URL is required.")
    @Pattern(regexp = "^(https?://).+$", message = "Image URL must be a valid URL.")
    private String imageUrl;

    @Column(nullable = false)
    @NotNull(message = "Stock is required.")
    @PositiveOrZero(message = "Stock cannot be negative.")
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category ID is required.")
//    @JsonBackReference
    private Category category;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isInStock() {
        return stock > 0;
    }

    public boolean hasDiscount() {
        return discountedPrice != null
                && discountedPrice.compareTo(BigDecimal.ZERO) > 0
                && discountedPrice.compareTo(price) < 0;
    }

    public BigDecimal getEffectivePrice() {
        return hasDiscount() ? discountedPrice : price;
    }
}
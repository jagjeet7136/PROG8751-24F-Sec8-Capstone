package com.app.ecommerce.modules.product.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
public class ProductSearchCriteriaRequest {

    @Min(value = 0, message = "Page must be >= 0")
    private Integer page = 0;

    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must not exceed 100")
    private Integer size = 20;

    @Size(max = 100, message = "Search term too long")
    private String search;

    @Pattern(
            regexp = "name|price|createdAt",
            message = "SortBy must be one of: name, price, createdAt"
    )
    private String sortBy = "name";

    @Pattern(
            regexp = "(?i)asc|desc",
            message = "SortOrder must be 'asc' or 'desc'"
    )
    private String sortOrder = "asc";
}

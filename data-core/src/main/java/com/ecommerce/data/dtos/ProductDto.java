package com.ecommerce.data.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String name;

    private String shortDescription;

    private String description;

    private BigDecimal price;

    private BigInteger qty;

    private String sku;

    private List<String> categories;

    private UserDto user;

}

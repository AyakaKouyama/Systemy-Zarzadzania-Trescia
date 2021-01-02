package com.ecommerce.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity{

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity")
    private BigInteger quantity;

    @Column(name = "short_description", length = 1000)
    private String shortDescription;

    @Column(name = "description", length = 20000)
    private String description;

    @ManyToMany(mappedBy = "image")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<Image> images = new ArrayList<>();

    @ManyToMany(mappedBy = "category")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<Category> categories = new ArrayList<>();
}

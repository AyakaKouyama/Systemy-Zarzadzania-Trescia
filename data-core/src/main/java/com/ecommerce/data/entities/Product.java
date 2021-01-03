package com.ecommerce.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(exclude={"categories", "images"})
public class Product extends BaseEntity{

    @Column(name = "name", nullable = false, unique = true)
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

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User creator;

    @ManyToMany(mappedBy = "products")
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<Image> images = new ArrayList<>();

    @ManyToMany(cascade= CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name="product_category",
            joinColumns = {
                    @JoinColumn(name="category_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="product_id")
            })
    private List<Category> categories = new ArrayList<>();
}

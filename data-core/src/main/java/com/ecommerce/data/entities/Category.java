package com.ecommerce.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(cascade= CascadeType.ALL)
    @JoinTable(name="product_category",
            joinColumns = {
                    @JoinColumn(name="product_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="category_id")
            })
    private List<Product> products = new ArrayList<>();
}

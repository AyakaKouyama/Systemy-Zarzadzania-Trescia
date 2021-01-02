package com.ecommerce.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "image")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Image extends BaseEntity {

    @Column(name = "alt_name")
    private String altName;

    @Column(name = "path", nullable = false)
    private String path;

    @ManyToMany(cascade= CascadeType.ALL)
    @JoinTable(name="product_image",
            joinColumns = {
                    @JoinColumn(name="product_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="image_id")
            })
    private List<Product> products = new ArrayList<>();
}

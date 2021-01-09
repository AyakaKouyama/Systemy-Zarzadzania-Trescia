package com.ecommerce.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "image")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "products"}, ignoreUnknown = true)
@ToString(exclude={"products", "data"})
public class Image extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String fileName;

    @Column(name = "alt_name")
    private String altName;

    @Column(name = "path", nullable = false, unique = true)
    private String path;

    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] data;

    @ManyToMany(cascade= CascadeType.ALL)
    @JoinTable(name="product_image",
            joinColumns = {
                    @JoinColumn(name="image_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name="product_id")
            })
    private List<Product> products = new ArrayList<>();
}

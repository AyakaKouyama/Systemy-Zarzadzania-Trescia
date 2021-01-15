package com.ecommerce.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "image")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "products"}, ignoreUnknown = true)
@ToString(exclude = {"products", "data"})
public class Image extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String fileName;

    @Column(name = "alt_name")
    private String altName;

    @Lob
    @Column(name = "image_data", nullable = false)
    private byte[] data;

    @ManyToMany()
    @JoinTable(name = "product_image",
            joinColumns = {
                    @JoinColumn(name = "image_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "product_id")
            })
    private List<Product> products = new ArrayList<>();

    @Transient
    private String stringData;
}

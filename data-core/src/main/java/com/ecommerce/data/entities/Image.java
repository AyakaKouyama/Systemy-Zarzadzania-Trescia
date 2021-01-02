package com.ecommerce.data.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
}

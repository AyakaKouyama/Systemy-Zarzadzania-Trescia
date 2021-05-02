package com.ecommerce.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Cart extends BaseEntity {

    @Column(name = "sessionId", nullable = false, unique = true)
    private String sessionId;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "product_cart",
            joinColumns = {
                    @JoinColumn(name = "cart_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "product_id")
            })
    private List<Product> products = new ArrayList<>();

}

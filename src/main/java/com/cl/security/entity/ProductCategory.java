package com.cl.security.entity;


import com.cl.security.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "product_category")
@Getter
@Setter
public class ProductCategory extends BaseEntity {
    private String name;
    private Integer level;
    private Integer productCount;
    private String productUnit;
    private Integer navStatus;
    private Integer showStatus;
    private Integer sort;
    private String icon;
    private String keywords;
    private String description;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent",fetch = FetchType.EAGER)
    private Set<ProductCategory> children = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategory that = (ProductCategory) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(level, that.level) &&
                Objects.equals(productCount, that.productCount) &&
                Objects.equals(productUnit, that.productUnit) &&
                Objects.equals(navStatus, that.navStatus) &&
                Objects.equals(showStatus, that.showStatus) &&
                Objects.equals(sort, that.sort) &&
                Objects.equals(icon, that.icon) &&
                Objects.equals(keywords, that.keywords) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, level, productCount, productUnit, navStatus, showStatus, sort, icon, keywords, description);
    }
}

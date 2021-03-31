package com.cl.security.entity;

import com.cl.security.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@Getter
@Setter
public class Menu extends BaseEntity {
    private String name;
    private String path;
    @Column(nullable = false,columnDefinition = "varchar(255) default ''")
    private String redirect;
    private String component;
    private String icon;
    @Column(nullable = false)
    private Integer type;
    @Column(nullable = false)
    private Integer sort;
    @Column(columnDefinition = "boolean default false")
    private Boolean hidden;
    private Integer test;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Menu parent;
    @Column(name= "parent_id",updatable = false,insertable = false)
    private Long parentId;
    @OneToMany(mappedBy = "parent",cascade = CascadeType.PERSIST)
    @OrderBy("sort asc")
    private Set<Menu> children = new HashSet<>();








}

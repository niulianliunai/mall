package com.cl.security.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Table
@Entity
@Getter
@Setter
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;
    private String redirect;
    private String component;
    private String icon;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Menu parent;
    @OneToMany(mappedBy = "parent",cascade = CascadeType.PERSIST,fetch = FetchType.EAGER)
    private Set<Menu> children = new HashSet<>();






}

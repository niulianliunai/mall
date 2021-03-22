package com.cl.security.repository;

import com.cl.security.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Long>, JpaSpecificationExecutor<ProductCategory> {
}

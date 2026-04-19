package com.egequiz.repository;

import com.egequiz.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /** Kategori adına göre alfabetik sıralı liste; admin / kullanıcı sayfalarında kullanışlı. */
    List<Category> findAllByOrderByNameAsc();
}

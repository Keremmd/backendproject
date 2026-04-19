package com.egequiz.repository;

import com.egequiz.model.Category;
import com.egequiz.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    /** Belli bir kategoriye ait quizleri listele. */
    List<Quiz> findByCategoryOrderByCreatedAtDesc(Category category);

    /** Anasayfa / quiz listesi için en yeni quizleri öne çıkar. */
    List<Quiz> findAllByOrderByCreatedAtDesc();
}

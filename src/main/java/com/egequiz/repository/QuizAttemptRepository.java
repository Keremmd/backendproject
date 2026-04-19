package com.egequiz.repository;

import com.egequiz.model.Quiz;
import com.egequiz.model.QuizAttempt;
import com.egequiz.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /** Geçmiş sayfası: kullanıcının en yeniden eskiye doğru tüm denemeleri. */
    List<QuizAttempt> findByUserOrderByTakenAtDesc(User user);

    /** Admin sayfası: tüm denemeleri görüntülemek için en yeniden eskiye. */
    List<QuizAttempt> findAllByOrderByTakenAtDesc();

    /** Belli bir quiz'e ait tüm denemeler — quiz detay sayfasında istatistik için. */
    List<QuizAttempt> findByQuizOrderByTakenAtDesc(Quiz quiz);

    /**
     * Liderlik tablosu — tüm kullanıcıların en yüksek skor (yüzdesel) denemesini bul.
     * Önce yüzdeye, sonra (eşitlikte) daha kısa süreye, en son daha eski tarihe göre sıralar.
     *
     * Not: H2/MySQL/PostgreSQL ile uyumlu, JPQL üzerinden yazıldı.
     */
    @Query("""
            SELECT a FROM QuizAttempt a
            WHERE (a.score * 1.0 / a.totalQuestions) =
                  (SELECT MAX(b.score * 1.0 / b.totalQuestions) FROM QuizAttempt b WHERE b.user = a.user)
            ORDER BY (a.score * 1.0 / a.totalQuestions) DESC, a.durationSeconds ASC, a.takenAt ASC
           """)
    List<QuizAttempt> findLeaderboard(Pageable pageable);
}

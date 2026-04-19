package com.egequiz.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Bir kullanıcının bir quiz'i çözmesinin tek bir kaydı.
 * Liderlik tablosu (leaderboard) ve geçmiş listesi (history) bu tablodan üretilir.
 *
 * score: doğru cevap sayısı.
 * totalQuestions: o anki quizin toplam soru sayısı (quiz sonradan değiştirilse de
 *                 sonuç sayfasında doğru oran görünsün diye snapshot olarak tutulur).
 * durationSeconds: kullanıcının quizi bitirmek için harcadığı süre (saniye).
 */
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int durationSeconds;

    @Column(nullable = false)
    private LocalDateTime takenAt = LocalDateTime.now();

    /**
     * Kullanıcının her bir soru için verdiği cevabın detayı. Sonuç sayfasında
     * "doğru/yanlış" detayı göstermek için kullanılır.
     */
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptAnswer> answers = new ArrayList<>();

    public QuizAttempt() {}

    public QuizAttempt(User user, Quiz quiz, int score, int totalQuestions, int durationSeconds) {
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.durationSeconds = durationSeconds;
    }

    /** Yüzde olarak başarı oranı (0-100). */
    public int getPercentage() {
        if (totalQuestions == 0) return 0;
        return (int) Math.round(score * 100.0 / totalQuestions);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }

    public List<AttemptAnswer> getAnswers() { return answers; }
    public void setAnswers(List<AttemptAnswer> answers) { this.answers = answers; }
}

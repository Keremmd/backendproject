package com.egequiz.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Bir quiz'e ait çoktan seçmeli soru. Her sorunun en az iki şıkkı olur ve
 * bu şıklardan tam olarak biri "doğru" işaretlenir (basitlik için tek doğru cevap).
 */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    /** Sorunun ait olduğu quiz. Quiz silinirse sorular cascade ile silinir. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    /**
     * Şıklar. Sorunun şıkları soruya bağımlıdır; soru silinince hepsi silinir.
     * orphanRemoval sayesinde listeye yeni şık eklenip eskisi çıkarıldığında
     * veritabanında otomatik temizlik yapılır.
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AnswerOption> options = new ArrayList<>();

    public Question() {}

    public Question(String text, Quiz quiz) {
        this.text = text;
        this.quiz = quiz;
    }

    /** Soru üzerindeki doğru şık (yoksa null). */
    public AnswerOption getCorrectOption() {
        return options.stream().filter(AnswerOption::isCorrect).findFirst().orElse(null);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public List<AnswerOption> getOptions() { return options; }
    public void setOptions(List<AnswerOption> options) { this.options = options; }
}

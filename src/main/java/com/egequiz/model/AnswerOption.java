package com.egequiz.model;

import jakarta.persistence.*;

/**
 * Çoktan seçmeli sorulardaki bir şık. "Option" adı SQL'de rezerve kelime
 * olabildiği için entity adını AnswerOption olarak verdik.
 *
 * isCorrect: Bu şık sorunun doğru cevabı mı? Tek bir soru için yalnızca bir
 * şıkkın true olması beklenir; bunu uygulama katmanında zorluyoruz.
 */
@Entity
@Table(name = "answer_options")
public class AnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(nullable = false)
    private boolean correct;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public AnswerOption() {}

    public AnswerOption(String text, boolean correct, Question question) {
        this.text = text;
        this.correct = correct;
        this.question = question;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}

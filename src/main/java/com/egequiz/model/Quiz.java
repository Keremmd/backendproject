package com.egequiz.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Belirli bir kategoriye ait quiz. Quiz'in genel bir süresi (saniye) ve
 * başlık/açıklama gibi tanımlayıcı alanları vardır.
 *
 * timeLimitSeconds: kullanıcının quizi tamamlamak için sahip olduğu toplam süre.
 * Sayfada bir geri sayım sayacı (countdown) bu değerden başlar.
 */
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    /** Quiz çözümünde verilen toplam süre (saniye). */
    @Column(nullable = false)
    private Integer timeLimitSeconds = 300;

    /** Quizin ait olduğu kategori. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Quizin soruları. Quiz silinirse soruları da silinir.
     * UI tarafında soruların eklenme sırasına göre gösterilmesi yeterlidir.
     */
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public Quiz() {}

    public Quiz(String title, String description, Integer timeLimitSeconds, Category category) {
        this.title = title;
        this.description = description;
        this.timeLimitSeconds = timeLimitSeconds;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(Integer timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}

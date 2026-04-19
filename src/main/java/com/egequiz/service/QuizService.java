package com.egequiz.service;

import com.egequiz.model.*;
import com.egequiz.repository.QuizAttemptRepository;
import com.egequiz.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quiz çözüm akışını yöneten servis. Form'dan gelen cevapları puanlar,
 * QuizAttempt + AttemptAnswer kayıtlarını oluşturur.
 */
@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;

    public QuizService(QuizRepository quizRepository, QuizAttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
    }

    public List<Quiz> all() {
        return quizRepository.findAllByOrderByCreatedAtDesc();
    }

    public Quiz get(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz bulunamadı: " + id));
    }

    /**
     * Bir quiz denemesini sonlandırır:
     *  - Form'dan gelen cevapları (questionId → optionId) eşleştirip puanlar.
     *  - QuizAttempt oluşturup kaydeder, içine her sorunun AttemptAnswer'ını koyar.
     *  - Geri dönüş: kaydedilen QuizAttempt (id ile birlikte → result sayfasına yönlendirme).
     *
     * Eğer kullanıcı bir soruyu cevapsız bıraktıysa selectedOption null olarak kaydedilir.
     */
    @Transactional
    public QuizAttempt submitAttempt(User user, Quiz quiz, Map<Long, Long> answers, int durationSeconds) {
        // Form bind edilirken bazı questionId'ler gelmemiş olabilir; null-safe çalış.
        if (answers == null) answers = new HashMap<>();

        int correctCount = 0;
        QuizAttempt attempt = new QuizAttempt(user, quiz, 0, quiz.getQuestions().size(), Math.max(0, durationSeconds));

        for (Question q : quiz.getQuestions()) {
            Long selectedId = answers.get(q.getId());
            AnswerOption selected = null;
            boolean correct = false;
            if (selectedId != null) {
                // Sadece bu sorunun şıkları arasından eşleştir; başka sorudan id sıkıştırılırsa görmezden gel.
                for (AnswerOption opt : q.getOptions()) {
                    if (opt.getId().equals(selectedId)) {
                        selected = opt;
                        correct = opt.isCorrect();
                        break;
                    }
                }
            }
            if (correct) correctCount++;
            attempt.getAnswers().add(new AttemptAnswer(attempt, q, selected, correct));
        }

        attempt.setScore(correctCount);
        return attemptRepository.save(attempt);
    }
}

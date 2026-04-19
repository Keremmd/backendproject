package com.egequiz.controller;

import com.egequiz.model.Category;
import com.egequiz.model.Quiz;
import com.egequiz.model.QuizAttempt;
import com.egequiz.repository.CategoryRepository;
import com.egequiz.repository.QuizAttemptRepository;
import com.egequiz.repository.QuizRepository;
import com.egequiz.security.UserPrincipal;
import com.egequiz.service.QuizService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Quizlerin listelenmesi, çözülmesi, sonuç görüntülenmesi ve geçmiş.
 */
@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizRepository quizRepository;
    private final QuizService quizService;
    private final CategoryRepository categoryRepository;
    private final QuizAttemptRepository attemptRepository;

    public QuizController(QuizRepository quizRepository,
                          QuizService quizService,
                          CategoryRepository categoryRepository,
                          QuizAttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.quizService = quizService;
        this.categoryRepository = categoryRepository;
        this.attemptRepository = attemptRepository;
    }

    /** Tüm quizler — opsiyonel kategori filtresi ile. */
    @GetMapping
    public String list(@RequestParam(value = "category", required = false) Long categoryId, Model model) {
        List<Quiz> quizzes;
        Category selected = null;
        if (categoryId != null) {
            selected = categoryRepository.findById(categoryId).orElse(null);
            quizzes = (selected == null) ? List.of() : quizRepository.findByCategoryOrderByCreatedAtDesc(selected);
        } else {
            quizzes = quizRepository.findAllByOrderByCreatedAtDesc();
        }
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("selectedCategory", selected);
        return "quiz/list";
    }

    /** Quizin başlama sayfası — kuralları gösterir, "Başla" butonu ile take sayfasına götürür. */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.get(id);
        model.addAttribute("quiz", quiz);
        return "quiz/detail";
    }

    /** Quiz çözüm ekranı — geri sayım sayacı ve sorular. */
    @GetMapping("/{id}/take")
    public String take(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.get(id);
        model.addAttribute("quiz", quiz);
        return "quiz/take";
    }

    /**
     * Quiz cevaplarını al, puanla, denemeyi kaydet ve sonuç sayfasına yönlendir.
     *
     * Form alanları:
     *   answers[questionId] = optionId  (her bir soru için)
     *   duration            = saniye (JS'in formdaki gizli alana yazdığı süre)
     */
    @PostMapping("/{id}/submit")
    public String submit(@PathVariable Long id,
                         @RequestParam Map<String, String> formParams,
                         @AuthenticationPrincipal UserPrincipal principal) {
        Quiz quiz = quizService.get(id);
        // form alanı adlandırması: "q_<questionId>" → optionId
        java.util.Map<Long, Long> answers = new java.util.HashMap<>();
        for (var entry : formParams.entrySet()) {
            if (entry.getKey().startsWith("q_") && entry.getValue() != null && !entry.getValue().isBlank()) {
                try {
                    Long qid = Long.parseLong(entry.getKey().substring(2));
                    Long oid = Long.parseLong(entry.getValue());
                    answers.put(qid, oid);
                } catch (NumberFormatException ignored) {
                    // bozuk parametreyi yok say
                }
            }
        }
        int duration = 0;
        try { duration = Integer.parseInt(formParams.getOrDefault("duration", "0")); }
        catch (NumberFormatException ignored) { }

        QuizAttempt attempt = quizService.submitAttempt(principal.getUser(), quiz, answers, duration);
        return "redirect:/quiz/result/" + attempt.getId();
    }

    /** Sonuç sayfası — skor, yüzde, doğru/yanlış detay ve grafikli özet. */
    @GetMapping("/result/{attemptId}")
    public String result(@PathVariable Long attemptId,
                         @AuthenticationPrincipal UserPrincipal principal,
                         Model model) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Sonuç bulunamadı."));
        // Güvenlik: kullanıcı yalnızca kendi sonucunu görsün (admin hepsini görebilsin).
        boolean isAdmin = principal.getUser().getRole().name().equals("ADMIN");
        if (!isAdmin && !attempt.getUser().getId().equals(principal.getId())) {
            return "redirect:/dashboard";
        }
        model.addAttribute("attempt", attempt);
        return "quiz/result";
    }

    /** Kullanıcının tüm geçmiş denemeleri. */
    @GetMapping("/history")
    public String history(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        List<QuizAttempt> attempts = attemptRepository.findByUserOrderByTakenAtDesc(principal.getUser());
        model.addAttribute("attempts", attempts);
        // Grafik için son 10 denemeyi kronolojik (eski → yeni) sırada göster
        List<QuizAttempt> chartData = attempts.stream()
                .limit(10)
                .sorted((a, b) -> a.getTakenAt().compareTo(b.getTakenAt()))
                .toList();
        model.addAttribute("chartAttempts", chartData);
        return "quiz/history";
    }
}

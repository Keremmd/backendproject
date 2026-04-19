package com.egequiz.controller;

import com.egequiz.model.AnswerOption;
import com.egequiz.model.Category;
import com.egequiz.model.Question;
import com.egequiz.model.Quiz;
import com.egequiz.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* Kategori ikon seçicide kullanılan ön tanımlı emoji listesi.
 * Buradan gelen emojiler form ekranında butonlu grid olarak gösterilir;
 * admin emoji'yi listeden seçer, böylece sistem hep tutarlı simgeler kullanır. */

/**
 * Yalnızca ADMIN rolündeki kullanıcıların erişebildiği yönetim paneli.
 *
 * Yetkilendirme SecurityConfig içinde "/admin/**" → hasRole('ADMIN')
 * kuralıyla yapılır; bu controller içine ek kontrol koymak gerekmez.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    public AdminController(CategoryRepository categoryRepository,
                           QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           QuizAttemptRepository attemptRepository,
                           UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    /** Admin anasayfası — özet sayılar. */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("quizCount", quizRepository.count());
        model.addAttribute("questionCount", questionRepository.count());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("attemptCount", attemptRepository.count());
        model.addAttribute("recentAttempts", attemptRepository.findAllByOrderByTakenAtDesc()
                .stream().limit(8).toList());
        return "admin/dashboard";
    }

    // ----------------------- KATEGORİLER ---------------------------------

    /** Kategori ikonu için seçilebilecek hazır emoji havuzu. */
    private static final List<String> CATEGORY_ICONS = List.of(
            "📚", "💻", "🌍", "📐", "🔬", "🧪", "🧠", "🎓",
            "🎨", "🎵", "🎬", "🎮", "🏆", "⚽", "🏀", "🎯",
            "📊", "💡", "⚖️", "🌱", "🚀", "🛰️", "🩺", "🍔",
            "✈️", "🐾", "📜", "🏛️", "🗺️", "💼", "🔧", "🎭"
    );

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("iconChoices", CATEGORY_ICONS);
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(@RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) String icon,
                                 RedirectAttributes ra) {
        if (name == null || name.isBlank()) {
            ra.addFlashAttribute("error", "Kategori adı boş olamaz.");
            return "redirect:/admin/categories";
        }
        categoryRepository.save(new Category(name.trim(), description, (icon == null || icon.isBlank()) ? "📚" : icon));
        ra.addFlashAttribute("success", "Kategori oluşturuldu.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        categoryRepository.deleteById(id);
        ra.addFlashAttribute("success", "Kategori silindi.");
        return "redirect:/admin/categories";
    }

    // ----------------------- QUIZ'LER ------------------------------------

    @GetMapping("/quizzes")
    public String quizzes(Model model) {
        model.addAttribute("quizzes", quizRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        return "admin/quizzes";
    }

    @PostMapping("/quizzes")
    public String createQuiz(@RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam Long categoryId,
                             @RequestParam(defaultValue = "300") Integer timeLimitSeconds,
                             RedirectAttributes ra) {
        Category cat = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Kategori bulunamadı."));
        Quiz quiz = new Quiz(title.trim(), description, timeLimitSeconds, cat);
        Quiz saved = quizRepository.save(quiz);
        ra.addFlashAttribute("success", "Quiz oluşturuldu.");
        return "redirect:/admin/quizzes/" + saved.getId();
    }

    @PostMapping("/quizzes/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes ra) {
        quizRepository.deleteById(id);
        ra.addFlashAttribute("success", "Quiz silindi.");
        return "redirect:/admin/quizzes";
    }

    /** Tek bir quizin detay/düzenleme sayfası: meta bilgisi + soru CRUD. */
    @GetMapping("/quizzes/{id}")
    public String quizDetail(@PathVariable Long id, Model model) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz yok."));
        model.addAttribute("quiz", quiz);
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        return "admin/quiz-detail";
    }

    @PostMapping("/quizzes/{id}/update")
    public String updateQuiz(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam Long categoryId,
                             @RequestParam Integer timeLimitSeconds,
                             RedirectAttributes ra) {
        Quiz quiz = quizRepository.findById(id).orElseThrow();
        quiz.setTitle(title.trim());
        quiz.setDescription(description);
        quiz.setTimeLimitSeconds(timeLimitSeconds);
        quiz.setCategory(categoryRepository.findById(categoryId).orElseThrow());
        quizRepository.save(quiz);
        ra.addFlashAttribute("success", "Quiz güncellendi.");
        return "redirect:/admin/quizzes/" + id;
    }

    // ----------------------- SORU CRUD -----------------------------------

    /**
     * Yeni soru ekleme (4 sabit şıkla). Form alanları:
     *   text, options[0..3], correct (0..3 → doğru olan şıkkın indeksi)
     */
    @PostMapping("/quizzes/{quizId}/questions")
    public String addQuestion(@PathVariable Long quizId,
                              @RequestParam String text,
                              @RequestParam Map<String, String> all,
                              RedirectAttributes ra) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow();
        Question question = new Question(text.trim(), quiz);

        int correctIdx;
        try { correctIdx = Integer.parseInt(all.getOrDefault("correct", "0")); }
        catch (NumberFormatException e) { correctIdx = 0; }

        List<String> opts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String v = all.get("opt" + i);
            opts.add(v == null ? "" : v.trim());
        }
        // En az 2 dolu şık ve doğru şıkkın boş olmaması gerekir
        long filled = opts.stream().filter(s -> !s.isBlank()).count();
        if (filled < 2 || opts.get(correctIdx).isBlank()) {
            ra.addFlashAttribute("error", "En az 2 şık ekleyin ve doğru cevabın boş olmadığından emin olun.");
            return "redirect:/admin/quizzes/" + quizId;
        }

        for (int i = 0; i < opts.size(); i++) {
            if (opts.get(i).isBlank()) continue;
            question.getOptions().add(new AnswerOption(opts.get(i), i == correctIdx, question));
        }
        quiz.getQuestions().add(question);
        questionRepository.save(question);
        ra.addFlashAttribute("success", "Soru eklendi.");
        return "redirect:/admin/quizzes/" + quizId;
    }

    @PostMapping("/questions/{id}/delete")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes ra) {
        Question q = questionRepository.findById(id).orElseThrow();
        Long quizId = q.getQuiz().getId();
        questionRepository.delete(q);
        ra.addFlashAttribute("success", "Soru silindi.");
        return "redirect:/admin/quizzes/" + quizId;
    }

    /** Sorunun metnini ve şıklarını güncelle. */
    @PostMapping("/questions/{id}/update")
    public String updateQuestion(@PathVariable Long id,
                                 @RequestParam String text,
                                 @RequestParam Map<String, String> all,
                                 RedirectAttributes ra) {
        Question q = questionRepository.findById(id).orElseThrow();
        q.setText(text.trim());

        int correctIdx;
        try { correctIdx = Integer.parseInt(all.getOrDefault("correct", "0")); }
        catch (NumberFormatException e) { correctIdx = 0; }

        // Eski şıkları kaldırıp yenilerini ekle (orphanRemoval bunu temizler)
        q.getOptions().clear();
        for (int i = 0; i < 4; i++) {
            String v = all.get("opt" + i);
            if (v == null || v.isBlank()) continue;
            q.getOptions().add(new AnswerOption(v.trim(), i == correctIdx, q));
        }
        questionRepository.save(q);
        ra.addFlashAttribute("success", "Soru güncellendi.");
        return "redirect:/admin/quizzes/" + q.getQuiz().getId();
    }

    // ----------------------- SONUÇLAR ------------------------------------

    @GetMapping("/attempts")
    public String attempts(Model model) {
        model.addAttribute("attempts", attemptRepository.findAllByOrderByTakenAtDesc());
        return "admin/attempts";
    }

    // ----------------------- KULLANICILAR --------------------------------

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }
}

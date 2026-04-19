package com.egequiz.controller;

import com.egequiz.model.QuizAttempt;
import com.egequiz.repository.CategoryRepository;
import com.egequiz.repository.QuizAttemptRepository;
import com.egequiz.repository.QuizRepository;
import com.egequiz.repository.UserRepository;
import com.egequiz.security.UserPrincipal;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Anasayfa ve kullanıcı dashboard'ı (giriş sonrası karşılama sayfası).
 */
@Controller
public class HomeController {

    private final QuizRepository quizRepository;
    private final CategoryRepository categoryRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    public HomeController(QuizRepository quizRepository,
                          CategoryRepository categoryRepository,
                          QuizAttemptRepository attemptRepository,
                          UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.categoryRepository = categoryRepository;
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
    }

    /**
     * Landing page — giriş yapmamış ziyaretçilere uygulamayı tanıtır.
     * Giriş yapmışsa direkt dashboard'a yönlendirmek de mümkün, fakat
     * pazarlama amaçlı sayfayı herkese gösterelim.
     */
    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("totalQuizzes", quizRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalAttempts", attemptRepository.count());
        return "index";
    }

    /**
     * Giriş yapan kullanıcının kişisel kontrol paneli.
     * Burada en yeni quizler, son denemeleri ve mini istatistikler gösterilir.
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        model.addAttribute("user", principal.getUser());
        model.addAttribute("categories", categoryRepository.findAllByOrderByNameAsc());
        model.addAttribute("recentQuizzes", quizRepository.findAllByOrderByCreatedAtDesc()
                .stream().limit(6).toList());

        List<QuizAttempt> myAttempts = attemptRepository.findByUserOrderByTakenAtDesc(principal.getUser());
        model.addAttribute("recentAttempts", myAttempts.stream().limit(5).toList());
        model.addAttribute("attemptCount", myAttempts.size());

        // En iyi yüzde, basit özet kart için
        int best = myAttempts.stream().mapToInt(QuizAttempt::getPercentage).max().orElse(0);
        model.addAttribute("bestPercentage", best);

        // Top 5 leaderboard önizleme
        model.addAttribute("topPlayers", attemptRepository.findLeaderboard(PageRequest.of(0, 5)));
        return "dashboard";
    }
}

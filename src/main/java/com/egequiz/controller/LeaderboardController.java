package com.egequiz.controller;

import com.egequiz.repository.QuizAttemptRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Liderlik tablosu — en iyi 50 oyuncu (her kullanıcının en iyi denemesi).
 */
@Controller
public class LeaderboardController {

    private final QuizAttemptRepository attemptRepository;

    public LeaderboardController(QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @GetMapping("/leaderboard")
    public String leaderboard(Model model) {
        model.addAttribute("topAttempts", attemptRepository.findLeaderboard(PageRequest.of(0, 50)));
        return "leaderboard";
    }
}

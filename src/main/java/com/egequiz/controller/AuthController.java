package com.egequiz.controller;

import com.egequiz.dto.RegisterForm;
import com.egequiz.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Kayıt ve giriş sayfaları.
 *
 * Login sayfasını kendimiz render ediyoruz; form post'unu Spring Security'nin
 * UsernamePasswordAuthenticationFilter işliyor (SecurityConfig'de tanımlı).
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult binding,
                           Model model) {
        if (binding.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(form);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registerError", ex.getMessage());
            return "auth/register";
        }
        // Başarılı kayıttan sonra login sayfasına bilgi mesajıyla yönlendir
        return "redirect:/login?registered";
    }
}

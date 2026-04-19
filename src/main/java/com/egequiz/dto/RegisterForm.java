package com.egequiz.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Kayıt formundan gelen verileri taşıyan DTO. Bean Validation ile alanlar
 * doğrulanır; controller @Valid ile validation tetikler.
 */
public class RegisterForm {

    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter olmalı")
    private String username;

    @NotBlank(message = "Görünen ad boş olamaz")
    @Size(min = 2, max = 120)
    private String displayName;

    @NotBlank @Email(message = "Geçerli bir e-posta giriniz")
    private String email;

    @NotBlank
    @Size(min = 6, max = 100, message = "Şifre en az 6 karakter olmalı")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

package com.egequiz.service;

import com.egequiz.dto.RegisterForm;
import com.egequiz.model.Role;
import com.egequiz.model.User;
import com.egequiz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kullanıcı yönetimi servisi: kayıt ve sorgular.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Yeni kullanıcı oluşturur. Aynı kullanıcı adı veya e-posta varsa
     * IllegalArgumentException fırlatır → controller bunu yakalayıp
     * kullanıcıya hata mesajı gösterir.
     */
    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten alınmış.");
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Bu e-posta zaten kayıtlı.");
        }
        User user = new User(
                form.getUsername().trim(),
                form.getDisplayName().trim(),
                form.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(form.getPassword()),
                Role.USER
        );
        return userRepository.save(user);
    }
}

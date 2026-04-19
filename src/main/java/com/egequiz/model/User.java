package com.egequiz.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sistemdeki kullanıcıyı temsil eder. Spring Security ile entegre olabilmesi
 * için kullanıcı adı tekildir (UNIQUE) ve şifre BCrypt ile hash'lenmiş şekilde saklanır.
 *
 * QuizAttempt ile bire-çok ilişkisi vardır: bir kullanıcının birden fazla
 * quiz denemesi olabilir, fakat her deneme yalnızca bir kullanıcıya aittir.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 120)
    private String displayName;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** BCrypt hash; ham parola asla saklanmaz. */
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Bir kullanıcının tüm quiz denemeleri. Kullanıcı silinirse denemeleri de silinir.
     * Performans için LAZY (yalnızca ihtiyaç duyulduğunda yüklenir).
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizAttempt> attempts = new ArrayList<>();

    public User() {}

    public User(String username, String displayName, String email, String password, Role role) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<QuizAttempt> getAttempts() { return attempts; }
    public void setAttempts(List<QuizAttempt> attempts) { this.attempts = attempts; }
}

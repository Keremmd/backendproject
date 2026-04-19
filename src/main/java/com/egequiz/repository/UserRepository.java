package com.egequiz.repository;

import com.egequiz.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Kullanıcılar için Spring Data JPA repository'si.
 * Spring Data, arayüzü çalışma zamanında implementasyona dönüştürür;
 * temel CRUD ve sayfalama metotları otomatik gelir.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** Login sırasında username ile arama. Tekildir → en fazla 1 sonuç döner. */
    Optional<User> findByUsername(String username);

    /** Kayıt sırasında email çakışmasını engellemek için. */
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

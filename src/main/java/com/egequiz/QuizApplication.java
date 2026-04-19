package com.egequiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Uygulamanın giriş noktası. Spring Boot otomatik konfigürasyonu burada başlar:
 *  - Embedded Tomcat üzerinde Web sunucusu
 *  - JPA/Hibernate ile H2 veritabanı bağlantısı
 *  - Spring Security filtre zinciri
 */
@SpringBootApplication
public class QuizApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
    }
}

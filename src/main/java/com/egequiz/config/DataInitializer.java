package com.egequiz.config;

import com.egequiz.model.*;
import com.egequiz.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Uygulama açılışında çalışan seeder. Veritabanı boşsa örnek
 * kategoriler, quizler, sorular ve admin/test kullanıcıları oluşturur.
 *
 * H2 in-memory veritabanı kullandığımız için her açılışta sıfırlanır;
 * bu sayede demo her zaman tutarlı görünür.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // tekrar çalışmasın

        // --- Kullanıcılar ---
        userRepository.save(new User(
                "admin", "Yönetici", "admin@egequiz.dev",
                passwordEncoder.encode("admin123"), Role.ADMIN));
        userRepository.save(new User(
                "kerem", "Kerem Yılmaz", "kerem@egequiz.dev",
                passwordEncoder.encode("kerem123"), Role.USER));
        userRepository.save(new User(
                "ayse", "Ayşe Demir", "ayse@egequiz.dev",
                passwordEncoder.encode("ayse1234"), Role.USER));

        // --- Kategoriler ---
        Category cs    = categoryRepository.save(new Category("Bilgisayar Bilimi", "Algoritma, veri yapıları, programlama dilleri", "💻"));
        Category gen   = categoryRepository.save(new Category("Genel Kültür", "Tarih, coğrafya, sanat ve daha fazlası", "🌍"));
        Category math  = categoryRepository.save(new Category("Matematik", "Cebir, geometri, sayı teorisi", "📐"));
        Category sci   = categoryRepository.save(new Category("Bilim", "Fizik, kimya, biyoloji", "🔬"));

        // --- Quizler ---
        Quiz java    = quizRepository.save(new Quiz("Java Temelleri",
                "JVM, OOP ve Java sözdizimi üzerine giriş seviyesi quiz.", 240, cs));
        Quiz spring  = quizRepository.save(new Quiz("Spring Framework",
                "Spring Boot, IoC, DI ve Spring MVC bilginizi sınayın.", 300, cs));
        Quiz history = quizRepository.save(new Quiz("Türkiye Tarihi",
                "Cumhuriyet öncesi ve sonrası kısa bir tur.", 240, gen));
        Quiz mathQ   = quizRepository.save(new Quiz("Lise Matematiği",
                "Cebir, fonksiyon ve geometri karışık.", 180, math));
        Quiz physics = quizRepository.save(new Quiz("Klasik Fizik",
                "Newton mekaniği, ısı ve dalgalar.", 240, sci));

        // --- Sorular ---
        addQuestion(java, "Java'da ilkel veri tipi (primitive) hangisidir?",
                List.of("String", "int", "Integer", "Object"), 1);
        addQuestion(java, "JVM'in açılımı nedir?",
                List.of("Java Virtual Machine", "Java Verified Module", "Just Valid Memory", "Joint View Manager"), 0);
        addQuestion(java, "Aşağıdakilerden hangisi OOP prensibi DEĞİLDİR?",
                List.of("Encapsulation", "Polymorphism", "Inheritance", "Compilation"), 3);
        addQuestion(java, "`final` anahtar sözcüğü bir sınıfta kullanılırsa ne anlama gelir?",
                List.of("Sınıf abstract olur", "Sınıf miras alınamaz", "Sınıf serileştirilemez", "Sınıf static olur"), 1);
        addQuestion(java, "Java'da exception hangi sınıfın alt sınıfıdır?",
                List.of("Object", "Throwable", "Error", "RuntimeException"), 1);

        addQuestion(spring, "Spring'de IoC neyin kısaltmasıdır?",
                List.of("Inversion of Control", "Internal Object Container", "Injected Object Class", "Inheritance of Components"), 0);
        addQuestion(spring, "Spring Boot uygulamasını başlatmak için kullanılan sınıf annotation'ı?",
                List.of("@SpringConfig", "@EnableSpring", "@SpringBootApplication", "@Bootable"), 2);
        addQuestion(spring, "Bir REST controller işaretlemek için hangisi kullanılır?",
                List.of("@RestController", "@Service", "@Repository", "@Component"), 0);
        addQuestion(spring, "Bağımlılık enjeksiyonu için hangisi YANLIŞTIR?",
                List.of("Constructor injection", "Setter injection", "Field injection", "Reflection injection"), 3);

        addQuestion(history, "Türkiye Cumhuriyeti hangi yılda kuruldu?",
                List.of("1919", "1920", "1923", "1938"), 2);
        addQuestion(history, "İlk TBMM hangi şehirde açıldı?",
                List.of("İstanbul", "Ankara", "İzmir", "Sivas"), 1);
        addQuestion(history, "Cumhuriyet'in ilanından önceki son padişah kimdir?",
                List.of("Abdülhamid II", "Vahdettin", "Mehmet V", "Abdülmecid II"), 1);

        addQuestion(mathQ, "f(x) = 2x + 3 ise f(5) kaçtır?",
                List.of("10", "11", "12", "13"), 4 - 1); // 13
        addQuestion(mathQ, "Bir üçgenin iç açıları toplamı kaç derecedir?",
                List.of("90", "120", "180", "360"), 2);
        addQuestion(mathQ, "log10(1000) kaçtır?",
                List.of("1", "2", "3", "10"), 2);

        addQuestion(physics, "Newton'un ikinci yasası nedir?",
                List.of("F = m·a", "E = m·c²", "v = v₀ + a·t", "P = F/A"), 0);
        addQuestion(physics, "Bir cismin ağırlığı neye bağlıdır?",
                List.of("Sadece kütleye", "Kütle ve yer çekimi ivmesine", "Sadece hacme", "Sadece yoğunluğa"), 1);
        addQuestion(physics, "Suyun normal koşullarda kaynama sıcaklığı (°C)?",
                List.of("90", "95", "100", "110"), 2);
    }

    /** Bir quiz'e 4 şıklı soru ekler. correctIndex: 0..3 (doğru olan şıkkın sıfırdan başlayan indeksi). */
    private void addQuestion(Quiz quiz, String text, List<String> options, int correctIndex) {
        Question q = new Question(text, quiz);
        for (int i = 0; i < options.size(); i++) {
            q.getOptions().add(new AnswerOption(options.get(i), i == correctIndex, q));
        }
        quiz.getQuestions().add(q);
        questionRepository.save(q);
    }
}

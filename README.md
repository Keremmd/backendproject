# EgeQuiz — Online Quiz Uygulaması

Ege Üniversitesi Bilgisayar Mühendisliği · **Back-End Software Development** dersi proje çalışması.
Modern, minimalist bir kullanıcı arayüzü ile geri sayım sayacı, grafiksel sonuç gösterimi ve liderlik tablosu içeren tam yığın (full-stack) bir Spring Boot uygulaması.

![tech](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![tech](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![tech](https://img.shields.io/badge/Thymeleaf-3.x-005F0F?logo=thymeleaf&logoColor=white)
![tech](https://img.shields.io/badge/H2-DB-1f4e79?logo=h2&logoColor=white)

## Özellikler

### 👤 Kullanıcı
- Kayıt ol / Giriş yap (BCrypt + Spring Security)
- Kategoriye göre quiz seç ve çöz
- **Geri sayım sayacı** (süre dolunca otomatik gönderim)
- Skor görüntüleme + her soru için doğru/yanlış detayı
- **Grafikle sonuç gösterimi** (Chart.js donut grafiği)
- Geçmiş denemeler ve performans trend grafiği
- **Liderlik tablosu** (en yüksek skor, eşitlikte daha hızlı)

### 🛠️ Admin
- Kategori CRUD
- Quiz oluştur, düzenle, sil (kategori + süre limiti)
- Soru ekle / sil / güncelle (4 şıklı, çoktan seçmeli)
- Tüm kullanıcı sonuçlarını görüntüleme
- Kayıtlı kullanıcılar listesi

### 🎨 Bonus (Notlandırma kriterleri)
- Açık/koyu tema (kullanıcı tercihi `localStorage`'da saklanır)
- Glass-morphism, gradient, yumuşak gölgeler
- Mobil uyumlu (responsive grid)
- Inter font + tutarlı tasarım sistemi (`main.css`)

## Teknik Yığın

| Katman | Teknoloji |
| --- | --- |
| Framework | Spring Boot 3.3 (gereksinimi karşılar) |
| Web | Spring MVC + Thymeleaf (server-side rendering) |
| Persistence | Spring Data JPA + Hibernate |
| Veritabanı | H2 (in-memory, sıfır kurulum). MySQL/Postgres'e tek property değişikliği ile geçiş yapılabilir. |
| Güvenlik | Spring Security 6 + BCrypt + form-login |
| Şablonlar | Thymeleaf 3 + Spring Security extras |
| Grafik | Chart.js 4 (CDN) |
| Build | Maven |

## Çalıştırma

```bash
cd quiz-app
mvn spring-boot:run
```

Tarayıcıdan açın: **http://localhost:8080**

### Demo hesaplar

| Rol | Kullanıcı adı | Şifre |
| --- | --- | --- |
| Admin | `admin` | `admin123` |
| Kullanıcı | `kerem` | `kerem123` |
| Kullanıcı | `ayse` | `ayse1234` |

H2 web konsolu (sadece dev): http://localhost:8080/h2-console
JDBC URL: `jdbc:h2:mem:egequizdb`, kullanıcı: `sa`, şifre: boş

## Mimari

```
src/main/java/com/egequiz
├── QuizApplication.java         # giriş noktası
├── config/
│   ├── SecurityConfig.java      # form-login, rol bazlı yetkilendirme
│   └── DataInitializer.java     # örnek kategori/quiz/soru/kullanıcı seed
├── model/                       # JPA entity'leri
│   ├── User, Role
│   ├── Category, Quiz, Question, AnswerOption
│   └── QuizAttempt, AttemptAnswer
├── repository/                  # Spring Data JPA arayüzleri
├── service/                     # iş mantığı (UserService, QuizService)
├── security/                    # UserDetails adapter
├── dto/                         # form binding DTO'ları
└── controller/
    ├── HomeController           # /, /dashboard
    ├── AuthController           # /login, /register
    ├── QuizController           # /quiz, /quiz/{id}/take, /quiz/result/{id}, /quiz/history
    ├── LeaderboardController    # /leaderboard
    └── AdminController          # /admin/**

src/main/resources
├── application.properties
├── static/
│   ├── css/main.css             # tasarım sistemi (light + dark)
│   └── js/app.js                # tema toggle + geri sayım sayacı
└── templates/
    ├── fragments/layout.html    # ortak layout (nav + footer)
    ├── index.html               # landing page
    ├── dashboard.html           # kullanıcı paneli
    ├── leaderboard.html
    ├── auth/{login,register}.html
    ├── quiz/{list,detail,take,result,history}.html
    └── admin/{dashboard,categories,quizzes,quiz-detail,attempts,users}.html
```

## Veritabanı Şeması (özet)

```
User (id, username*, displayName, email*, password, role, createdAt)
  └── 1..N QuizAttempt
Category (id, name*, description, icon)
  └── 1..N Quiz (title, description, timeLimitSeconds, category)
        └── 1..N Question (text, quiz)
              └── 1..N AnswerOption (text, correct, question)
QuizAttempt (id, user, quiz, score, totalQuestions, durationSeconds, takenAt)
  └── 1..N AttemptAnswer (question, selectedOption, correct)
```

JPA `ddl-auto=update` ile şema otomatik oluşturulur. Sayfalar tamamen
veritabanından dinamik olarak render edilir; kullanıcı verisi (kayıtlar, denemeler,
cevaplar) veritabanına yazılır.

## Notlar

- Frontend tasarımı dersin notlandırma kriterlerine (görünüş, profesyonellik) göre
  özenle hazırlanmıştır; dış UI framework kullanılmadan tek bir CSS dosyasıyla
  tutarlı bir tasarım sistemi sağlanır.
- Tüm controller / service / repository sınıfları detaylı Türkçe yorum satırları
  içerir (rapor istenmese bile kaynak kodda açıklama gereksinimi karşılanır).

# Event Rating Service

Микросървис за управление на рейтинги на събития. Това е RESTful микросървис, който предоставя функционалност за създаване, управление и извличане на рейтинги за събития.

## 📋 Технологичен стек

- **Java:** 17
- **Spring Boot:** 3.5.7
- **Build Tool:** Maven
- **Database:** MySQL
- **ORM:** Spring Data JPA
- **API:** RESTful API
- **Validation:** Jakarta Validation

## 🎯 Основни функционалности

### 1. Управление на рейтинги
- **Създаване на рейтинг** - Създаване на нов рейтинг за събитие от потребител (оценка от 1 до 5)
- **Получаване на рейтинги** - Извличане на всички рейтинги за дадено събитие
- **Проверка за съществуващ рейтинг** - Проверка дали потребител вече е оценил събитие

### 2. Защита срещу дублирани рейтинги
- **Уникален constraint** - База данни гарантира, че всеки потребител може да оцени всяко събитие само веднъж
- **Валидация на приложението** - Допълнителна проверка преди създаване на рейтинг
- **Обработка на constraint violations** - Правилна обработка на грешки при опит за дублиран рейтинг

## 🗄️ База данни

- **Database:** MySQL
- **ORM:** Spring Data JPA
- **Primary Keys:** UUID
- **Unique Constraint:** Комбинацията `(eventId, userId)` е уникална

### Entity
**Rating** - Рейтинг на събитие
- `id` (UUID) - Уникален идентификатор
- `eventId` (UUID) - Идентификатор на събитието
- `userId` (UUID) - Идентификатор на потребителя
- `score` (Integer, 1-5) - Оценка от 1 до 5
- `createdOn` (LocalDateTime) - Дата на създаване
- `updatedOn` (LocalDateTime) - Дата на последна промяна

## 📦 Структура на проекта

```
event-rating-svc/
├── src/
│   ├── main/
│   │   ├── java/exam/eventratingsvc/
│   │   │   ├── model/          # Entity класове
│   │   │   ├── repository/     # JPA Repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── web/
│   │   │       ├── dto/        # Data Transfer Objects
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       └── RatingController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 🚀 Стартиране на микросървиса

### Предварителни изисквания
- Java 17 или по-висока версия
- Maven 3.6+
- MySQL 8.0+

### Инсталация

1. Клонирай репозитория:
```bash
git clone <repository-url>
cd event-rating-svc
```

2. Конфигурирай базата данни в `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/event_rating_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Стартирай микросървиса:
```bash
mvn spring-boot:run
```

4. Микросървисът ще стартира на порт **8081**

## 📋 API Endpoints

### Рейтинги

#### Създаване на рейтинг
```
POST /ratings
Content-Type: application/json

{
  "eventId": "uuid",
  "userId": "uuid",
  "score": 5
}
```

**Response:**
- `201 Created` - Рейтингът е създаден успешно
- `400 Bad Request` - Невалидни данни или потребителят вече е оценил това събитие

#### Получаване на рейтинги за събитие
```
GET /ratings/event/{eventId}
```

**Response:**
```json
{
  "eventId": "uuid",
  "averageScore": 4.5,
  "totalRatings": 10,
  "ratings": [
    {
      "id": "uuid",
      "eventId": "uuid",
      "userId": "uuid",
      "score": 5,
      "createdOn": "2024-01-01T10:00:00",
      "updatedOn": "2024-01-01T10:00:00"
    }
  ]
}
```

#### Проверка дали потребител е оценил събитие
```
GET /ratings/event/{eventId}/user/{userId}
```

**Response:**
- `200 OK` - `true` или `false`

## 🔧 Error Handling

Микросървисът включва глобална обработка на грешки:
- **GlobalExceptionHandler** - Централизирана обработка на изключения
- **IllegalArgumentException** - Връща HTTP 400 Bad Request
- **DataIntegrityViolationException** - Обработва се при нарушаване на уникалния constraint

## 🔐 Валидация

- **Score** - Трябва да е между 1 и 5
- **EventId и UserId** - Задължителни полета
- **Уникален constraint** - Всеки потребител може да оцени всяко събитие само веднъж

## 📊 DTOs

### RatingRequest
```java
{
  "eventId": UUID,
  "userId": UUID,
  "score": Integer (1-5)
}
```

### RatingResponse
```java
{
  "id": UUID,
  "eventId": UUID,
  "userId": UUID,
  "score": Integer,
  "createdOn": LocalDateTime,
  "updatedOn": LocalDateTime
}
```

### EventRatingSummaryResponse
```java
{
  "eventId": UUID,
  "averageScore": Double,
  "totalRatings": Long,
  "ratings": List<RatingResponse>
}
```

## 🔗 Интеграция с главното приложение

Микросървисът се използва от `EventApp` чрез Spring Cloud OpenFeign:
- **URL:** Конфигурира се в `EventApp` в `application.properties` (`rating.service.url`)
- **Порт:** По подразбиране 8081
- **Протокол:** HTTP REST

## 📝 Logging

Микросървисът логва:
- Успешни операции (на INFO ниво)
- Грешки (на ERROR/WARN ниво)
- Constraint violations (на WARN ниво)

## 🧪 Testing

Микросървисът включва изчерпателни тестове, които покриват всички изисквания:

### Test Coverage
- **Line Coverage: 83.3%** (45 покрити, 9 пропуснати реда)
- **Instructions Coverage: 76%**
- **Branches Coverage: 50%**
- **Methods Coverage: 69.2%** (9 покрити, 4 пропуснати)
- **Classes Coverage: 100%** (4 покрити класа)

### Unit Tests
- **RatingServiceTest** (2 теста) - Тества бизнес логиката на RatingService
  - `createRating_WhenRatingDoesNotExist_ShouldCreateRating`
  - `getRatingsForEvent_WhenRatingsExist_ShouldReturnSummary`

### Integration/API Tests
- **RatingControllerIntegrationTest** (2 теста) - Тества RatingController с реална база
  - `createRating_WithValidRequest_ShouldReturnCreated` (API test)
  - `getRatingsForEvent_WhenRatingsExist_ShouldReturnSummary` (Integration test)

**Общо тестове:** 5 (всички минават успешно)

### Технологии за тестване
- **JUnit 5** - За писане на тестове
- **Mockito** - За мокиране на dependencies в unit тестове
- **Spring Boot Test** - За integration тестове с реална база
- **MockMvc** - За тестване на HTTP заявки в API тестове
- **JaCoCo** - За измерване на code coverage

### Стартиране на тестовете
```bash
# Стартиране на всички тестове
mvn clean test

# Генериране на coverage репорт
mvn clean test jacoco:report

# Coverage репортът се намира в: target/site/jacoco/index.html
```

### Ръчно тестване на API endpoints
За ръчно тестване на API endpoints можеш да използваш:
- Postman
- cURL
- HTTP файл (Rating.http) в проекта

Пример за тестване:
```bash
curl -X POST http://localhost:8081/ratings \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "your-event-id",
    "userId": "your-user-id",
    "score": 5
  }'
```

## 📝 License

Този проект е част от учебна програма на SoftUni.

## 👨‍💻 Автор

Разработен като част от Spring Advanced курс в SoftUni.


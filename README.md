# 🎯 Система маркетингового таргетингу

Це Quarkus додаток для створення та управління складними фільтрами маркетингового таргетингу з підтримкою динамічних умов та логічних виразів.

## 📋 Зміст

- [Огляд системи](#огляд-системи)
- [Як працює таргетинг](#як-працює-таргетинг)
- [Типи полів та операторів](#типи-полів-та-операторів)
- [API endpoints](#api-endpoints)
- [Приклади використання](#приклади-використання)
- [Запуск додатку](#запуск-додатку)

## 🎪 Огляд системи

Система дозволяє створювати **гнучкі фільтри** для таргетингу користувачів на основі різних параметрів:
- SMID (ідентифікатор користувача)
- Країна
- Операційна система
- Версія клієнта
- Браузер
- Тип інтернету
- Файли виборки користувачів

## 🔧 Як працює таргетинг

### Структура фільтра

Кожен фільтр складається з:
- **Корневі умови** - завжди з'єднуються через `AND`
- **Групи умов** - кожна група має свій логічний оператор (`AND` або `OR`)
- **Фінальна логіка**: `корневі_умови AND група1 AND група2 AND ...`

### Приклади логіки

#### 1. Простий фільтр (тільки корневі умови)
```json
{
  "conditions": [
    {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA"},
    {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "Android"}
  ]
}
```
**Результат**: `КРАЇНА=UA AND ANDROID`

#### 2. Складний фільтр з групами
```json
{
  "conditions": [
    {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA"}
  ],
  "groups": [
    {
      "groupName": "SMID група",
      "logicalOperator": "OR",
      "conditions": [
        {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "111"},
        {"fieldType": "SMID", "operator": "EQUAL", "fieldValue": "222"}
      ]
    }
  ]
}
```
**Результат**: `КРАЇНА=UA AND (SMID=111 OR SMID=222)`

#### 3. Дуже складний фільтр
```json
{
  "conditions": [
    {"fieldType": "DISTRIBUTION_GROUPS_FILE", "operator": "NOT_IN", "fieldValue": "file-id"}
  ],
  "groups": [
    {
      "logicalOperator": "OR",
      "conditions": [
        {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "Android"},
        {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "iOS"}
      ]
    },
    {
      "logicalOperator": "OR", 
      "conditions": [
        {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Chrome"},
        {"fieldType": "BROWSER", "operator": "CONTAINS", "fieldValue": "Safari"}
      ]
    }
  ]
}
```
**Результат**: `НЕ_В_ФАЙЛІ AND (Android OR iOS) AND (Chrome OR Safari)`

## 📊 Типи полів та операторів

### Типи полів (FilterFieldType)
- `SMID` - ідентифікатор користувача
- `COUNTRY` - країна (UA, PL, DE, тощо)
- `OPERATING_SYSTEM` - операційна система
- `CLIENT_VERSION` - версія клієнта
- `BROWSER` - браузер користувача
- `INTERNET_TYPE` - тип інтернету (Wi-Fi, 4G)
- `DISTRIBUTION_GROUPS_FILE` - файл виборки користувачів
- `INTERNAL_TRANSITION` - внутрішні переходи
- `CLE_CAMPAIGN` - CLE кампанія

### Оператори (FilterOperator)
- **Рівність**: `EQUAL` (=), `NOT_EQUAL` (≠)
- **Текст**: `CONTAINS`, `NOT_CONTAINS`, `STARTS_WITH`, `ENDS_WITH`
- **Числа**: `GREATER_THAN` (>), `LESS_THAN` (<), `GREATER_THAN_OR_EQUAL` (>=), `LESS_THAN_OR_EQUAL` (<=)
- **Списки**: `IN`, `NOT_IN`
- **Спеціальні**: `REGEX`, `IS_NULL`, `IS_NOT_NULL`

## 🚀 API Endpoints

### Основні операції з фільтрами
- `GET /api/marketing-target-filters` - отримати всі фільтри
- `GET /api/marketing-target-filters/{id}` - отримати фільтр за ID
- `GET /api/marketing-target-filters/by-target/{targetId}` - фільтри за ID таргету
- `POST /api/marketing-target-filters` - створити новий фільтр
- `PUT /api/marketing-target-filters/{id}` - оновити фільтр
- `DELETE /api/marketing-target-filters/{id}` - видалити фільтр

### Валідація
- `POST /api/marketing-target-filters/validate` - перевірити структуру фільтра

### Оцінка фільтрів
- `POST /api/marketing-target-filters/evaluate/{id}` - перевірити користувача за ID фільтра
- `POST /api/marketing-target-filters/evaluate` - перевірити користувача з фільтром у запиті

### Довідкові дані
- `GET /api/marketing-target-filters/field-types` - доступні типи полів
- `GET /api/marketing-target-filters/operators` - доступні оператори
- `GET /api/marketing-target-filters/logical-operators` - логічні оператори

## 💡 Приклади використання

### Створення простого фільтра
```bash
curl -X POST http://localhost:8080/api/marketing-target-filters \
  -H "Content-Type: application/json" \
  -d '{
    "filterName": "Тільки Україна",
    "marketingTargetId": 1,
    "description": "Користувачі з України",
    "isActive": true,
    "conditions": [
      {
        "fieldType": "COUNTRY",
        "operator": "EQUAL",
        "fieldValue": "UA",
        "orderIndex": 0
      }
    ]
  }'
```

### Створення складного фільтра
```bash
curl -X POST http://localhost:8080/api/marketing-target-filters \
  -H "Content-Type: application/json" \
  -d '{
    "filterName": "Мобільні користувачі України",
    "marketingTargetId": 1,
    "description": "Android або iOS користувачі з України",
    "isActive": true,
    "conditions": [
      {
        "fieldType": "COUNTRY",
        "operator": "EQUAL",
        "fieldValue": "UA",
        "orderIndex": 0
      }
    ],
    "groups": [
      {
        "groupName": "Мобільні ОС",
        "logicalOperator": "OR",
        "orderIndex": 0,
        "conditions": [
          {
            "fieldType": "OPERATING_SYSTEM",
            "operator": "CONTAINS",
            "fieldValue": "Android",
            "orderIndex": 0
          },
          {
            "fieldType": "OPERATING_SYSTEM",
            "operator": "CONTAINS",
            "fieldValue": "iOS",
            "orderIndex": 1
          }
        ]
      }
    ]
  }'
```

### Перевірка користувача
```bash
curl -X POST http://localhost:8080/api/marketing-target-filters/evaluate/1 \
  -H "Content-Type: application/json" \
  -d '{
    "smid": "3586067540",
    "country": "UA",
    "operatingSystem": "Android 12",
    "clientVersion": "2.1.0",
    "browser": "Chrome",
    "language": "uk"
  }'
```

### Валідація фільтра
```bash
curl -X POST http://localhost:8080/api/marketing-target-filters/validate \
  -H "Content-Type: application/json" \
  -d '{
    "filterName": "Тест фільтр",
    "marketingTargetId": 1,
    "conditions": [
      {
        "fieldType": "COUNTRY",
        "operator": "EQUAL",
        "fieldValue": "UA"
      }
    ]
  }'
```

## 🎯 Реальні сценарії використання

### 1. Премиум користувачі
**Мета**: Таргетинг на користувачів iOS з новими версіями з розвинених країн
```
iOS AND VERSION>=3.0.0 AND (US OR DE OR UK)
```

### 2. Регіональна кампанія
**Мета**: Українські користувачі з певними SMID, але не заблоковані
```
КРАЇНА=UA AND (SMID_380* OR SMID_050*) AND НЕ_В_ФАЙЛІ_БЛОКУВАННЯ
```

### 3. Тестування нових функцій
**Мета**: Активні користувачі Android з новими версіями
```
Android AND VERSION>2.0.0 AND НЕ_ТЕСТОВІ_КОРИСТУВАЧІ
```

## 🛠 Запуск додатку

### Режим розробки
```bash
./gradlew quarkusDev
```
Додаток буде доступний на http://localhost:8080

### Збірка проекту
```bash
./gradlew clean build -x test
```

### Запуск тестів
```bash
./gradlew test
```

### Dev UI
У режимі розробки доступний Dev UI: http://localhost:8080/q/dev/

## 📁 Структура проекту

```
src/main/java/com/minio/
├── dto/                    # DTO класи
│   ├── MarketingTargetFilterDto.java
│   ├── FilterConditionDto.java
│   ├── FilterGroupDto.java
│   ├── UserRequestDto.java
│   └── EvaluationRequestDto.java
├── model/                  # Entity класи
│   ├── MarketingTargetFilter.java
│   ├── MarketingTargetFilterCondition.java
│   ├── MarketingTargetFilterGroup.java
│   ├── FilterFieldType.java
│   ├── FilterOperator.java
│   └── LogicalOperator.java
├── repository/             # Репозиторії
│   ├── MarketingTargetFilterRepository.java
│   ├── MarketingTargetFilterConditionRepository.java
│   ├── MarketingTargetFilterGroupRepository.java
│   └── MarketingTargetListItemRepository.java
├── service/                # Бізнес логіка
│   ├── MarketingTargetFilterService.java
│   └── FilterEvaluationService.java
└── resource/               # REST endpoints
    └── MarketingTargetFilterResource.java

examples/                   # Приклади API
├── filter-api-examples.json
└── all-filter-variations.json
```

## 🔍 Особливості системи

### Валідація
- Автоматична валідація при створенні/оновленні фільтрів
- Перевірка обов'язкових полів
- Валідація операторів для різних типів полів
- Перевірка формату версій клієнта

### Оцінка фільтрів
- Рекурсивна оцінка складних логічних виразів
- Підтримка всіх операторів
- Спеціальна обробка файлів виборки
- Кешування результатів

### База даних
- PostgreSQL з Hibernate ORM
- Named queries в XML для оптимізації
- Транзакційність операцій
- Підтримка міграцій через Liquibase

## 🔧 Розширення системи

Система спроектована для легкого розширення новими типами полів та операторами. Ось що потрібно зробити:

### Додавання нового типу поля

#### 1. Додати новий тип в enum `FilterFieldType`
```java
// src/main/java/com/minio/model/FilterFieldType.java
public enum FilterFieldType {
    // ... існуючі типи
    NEW_FIELD_TYPE("NEW_FIELD_TYPE");  // Додати новий тип
}
```

#### 2. Оновити `UserRequestDto`
```java
// src/main/java/com/minio/dto/UserRequestDto.java
public class UserRequestDto {
    // Додати нове поле
    private String newFieldValue;
    
    // Додати getter/setter
    public String getNewFieldValue() { return newFieldValue; }
    public void setNewFieldValue(String value) { this.newFieldValue = value; }
    
    // Оновити метод getFieldValue()
    public String getFieldValue(String fieldType) {
        switch (fieldType) {
            // ... існуючі кейси
            case "NEW_FIELD_TYPE": return newFieldValue;
        }
    }
}
```

#### 3. Додати логіку оцінки (якщо потрібна спеціальна обробка)
```java
// src/main/java/com/minio/service/FilterEvaluationService.java
private boolean evaluateCondition(FilterConditionDto condition, UserRequestDto userRequest) {
    // Спеціальна обробка для нового типу поля
    if (condition.getFieldType() == FilterFieldType.NEW_FIELD_TYPE) {
        return evaluateNewFieldType(condition, userRequest);
    }
    // ... решта логіки
}

private boolean evaluateNewFieldType(FilterConditionDto condition, UserRequestDto userRequest) {
    // Ваша спеціальна логіка тут
    String fieldValue = userRequest.getNewFieldValue();
    // ... обробка
}
```

#### 4. Оновити валідацію (якщо потрібні спеціальні правила)
```java
// src/main/java/com/minio/service/MarketingTargetFilterService.java
private void validateConditions(List<FilterConditionDto> conditions, String context) {
    for (FilterConditionDto condition : conditions) {
        // Спеціальна валідація для нового типу
        if (condition.getFieldType() == FilterFieldType.NEW_FIELD_TYPE) {
            // Ваші правила валідації
            if (condition.getOperator() != FilterOperator.EQUAL) {
                throw new RuntimeException("NEW_FIELD_TYPE supports only EQUAL operator");
            }
        }
    }
}
```

### Додавання нового оператора

#### 1. Додати в enum `FilterOperator`
```java
// src/main/java/com/minio/model/FilterOperator.java
public enum FilterOperator {
    // ... існуючі оператори
    NEW_OPERATOR("NewOp");  // Додати новий оператор
}
```

#### 2. Додати логіку в `FilterEvaluationService`
```java
// src/main/java/com/minio/service/FilterEvaluationService.java
switch (operator) {
    // ... існуючі кейси
    case NEW_OPERATOR:
        return evaluateNewOperator(fieldValue, conditionValue);
}

private boolean evaluateNewOperator(String fieldValue, String conditionValue) {
    // Ваша логіка для нового оператора
    return fieldValue.someNewLogic(conditionValue);
}
```

### Приклад: Додавання поля "AGE" з оператором "BETWEEN"

#### 1. Додати тип поля
```java
public enum FilterFieldType {
    // ... існуючі
    AGE("AGE");
}
```

#### 2. Додати оператор
```java
public enum FilterOperator {
    // ... існуючі
    BETWEEN("Between");
}
```

#### 3. Оновити UserRequestDto
```java
private Integer age;

public String getFieldValue(String fieldType) {
    switch (fieldType) {
        // ... існуючі
        case "AGE": return age != null ? age.toString() : null;
    }
}
```

#### 4. Додати логіку оцінки
```java
case BETWEEN:
    // Очікуємо формат "18-65"
    String[] range = conditionValue.split("-");
    int userAge = Integer.parseInt(fieldValue);
    int minAge = Integer.parseInt(range[0]);
    int maxAge = Integer.parseInt(range[1]);
    return userAge >= minAge && userAge <= maxAge;
```

### Міграція бази даних

При додаванні нових типів полів, можливо потрібно оновити схему БД:

```sql
-- Якщо потрібно додати нові значення в enum
ALTER TYPE filter_field_type ADD VALUE 'NEW_FIELD_TYPE';
ALTER TYPE filter_operator ADD VALUE 'NEW_OPERATOR';
```

### Тестування нових полів

Додайте приклади в `examples/all-filter-variations.json`:

```json
{
  "newFieldExample": {
    "filterName": "Тест нового поля",
    "conditions": [
      {
        "fieldType": "NEW_FIELD_TYPE",
        "operator": "NEW_OPERATOR",
        "fieldValue": "test-value"
      }
    ]
  }
}
```

### Чек-лист для додавання нового поля:

- [ ] Додати в `FilterFieldType` enum
- [ ] Додати поле в `UserRequestDto`
- [ ] Оновити метод `getFieldValue()` в `UserRequestDto`
- [ ] Додати спеціальну логіку в `FilterEvaluationService` (якщо потрібно)
- [ ] Додати валідацію в `MarketingTargetFilterService` (якщо потрібно)
- [ ] Оновити міграції БД (якщо потрібно)
- [ ] Додати приклади в документацію
- [ ] Написати тести

**Система дуже гнучка для розширення!** Більшість нових полів потребують лише додавання в enum та DTO, без змін основної логіки.

## 🚀 Готово до використання!

Система повністю функціональна та готова для створення складних маркетингових кампаній з гнучким таргетингом користувачів!

# Маркетингові Фільтри - API для Фронту

## API Ендпоінти

### CRUD операції
```
POST   /api/marketing-target-filters              - Створити фільтр
PUT    /api/marketing-target-filters/{id}         - Оновити фільтр
GET    /api/marketing-target-filters/{id}         - Отримати фільтр
DELETE /api/marketing-target-filters/{id}         - Видалити фільтр
```

### Енуми для фронту
```
GET /api/marketing-target-filters/field-types      - Типи полів
GET /api/marketing-target-filters/operators        - Оператори
GET /api/marketing-target-filters/logical-operators - Логічні оператори
```

## Енуми

### FilterFieldType
```
SMID, DISTRIBUTION_GROUPS_FILE, COUNTRY, OPERATING_SYSTEM, 
CLIENT_VERSION, BROWSER, INTERNET_TYPE, INTERNAL_TRANSITION, CLE_CAMPAIGN
```

### FilterOperator  
```
EQUAL, NOT_EQUAL, CONTAINS, NOT_CONTAINS, STARTS_WITH, ENDS_WITH,
GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL,
IN, NOT_IN, REGEX, IS_NULL, IS_NOT_NULL
```

### LogicalOperator
```
AND, OR
```

## Структура JSON

### Базова структура фільтра
```json
{
  "filterName": "Назва фільтра",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [],  // Root-level умови
  "groups": []       // Групи умов
}
```

### Структура умови
```json
{
  "fieldType": "COUNTRY",
  "operator": "EQUAL", 
  "fieldValue": "UA",
  "logicalOperator": "AND",  // null для першої умови
  "orderIndex": 0
}
```

### Структура групи
```json
{
  "groupName": "Назва групи",
  "logicalOperator": "AND",
  "orderIndex": 0,
  "conditions": [
    {
      "fieldType": "SMID",
      "operator": "IN",
      "fieldValue": "[\"123\",\"456\"]",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ]
}
```

## Приклади "Хочу це - роби так"

### 1. Хочу тільки користувачів з України
```json
{
  "filterName": "Тільки Україна",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "COUNTRY",
      "operator": "EQUAL",
      "fieldValue": "UA",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 2. Хочу користувачів з України з версією від 2.2.2
```json
{
  "filterName": "Україна + версія 2.2.2+",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "COUNTRY",
      "operator": "EQUAL",
      "fieldValue": "UA",
      "logicalOperator": null,
      "orderIndex": 0
    },
    {
      "fieldType": "CLIENT_VERSION",
      "operator": "GREATER_THAN_OR_EQUAL",
      "fieldValue": "2.2.2",
      "logicalOperator": "AND",
      "orderIndex": 1
    }
  ],
  "groups": []
}
```

### 3. Хочу користувачів з України АБО Німеччини
```json
{
  "filterName": "Україна або Німеччина",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [],
  "groups": [
    {
      "groupName": "Країни",
      "logicalOperator": "AND",
      "orderIndex": 0,
      "conditions": [
        {
          "fieldType": "COUNTRY",
          "operator": "EQUAL",
          "fieldValue": "UA",
          "logicalOperator": null,
          "orderIndex": 0
        },
        {
          "fieldType": "COUNTRY",
          "operator": "EQUAL",
          "fieldValue": "DE",
          "logicalOperator": "OR",
          "orderIndex": 1
        }
      ]
    }
  ]
}
```

### 4. Хочу конкретних користувачів за SMID
```json
{
  "filterName": "VIP користувачі",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "SMID",
      "operator": "IN",
      "fieldValue": "[\"3586067540\",\"9876543210\",\"1111111111\"]",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 5. Хочу всіх КРІМ користувачів з Росії та Білорусі
```json
{
  "filterName": "Без РФ та BY",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "COUNTRY",
      "operator": "NOT_IN",
      "fieldValue": "[\"RU\",\"BY\"]",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 6. Хочу тільки мобільних користувачів (Android або iOS)
```json
{
  "filterName": "Тільки мобільні",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [],
  "groups": [
    {
      "groupName": "Мобільні ОС",
      "logicalOperator": "AND",
      "orderIndex": 0,
      "conditions": [
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "Android",
          "logicalOperator": null,
          "orderIndex": 0
        },
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "iOS",
          "logicalOperator": "OR",
          "orderIndex": 1
        }
      ]
    }
  ]
}
```

### 7. Хочу користувачів з Chrome або Safari
```json
{
  "filterName": "Chrome або Safari",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "BROWSER",
      "operator": "IN",
      "fieldValue": "[\"Chrome\",\"Safari\"]",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 8. Хочу користувачів з версією від 2.0.0 до 3.0.0
```json
{
  "filterName": "Версія 2.x",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "CLIENT_VERSION",
      "operator": "GREATER_THAN_OR_EQUAL",
      "fieldValue": "2.0.0",
      "logicalOperator": null,
      "orderIndex": 0
    },
    {
      "fieldType": "CLIENT_VERSION",
      "operator": "LESS_THAN",
      "fieldValue": "3.0.0",
      "logicalOperator": "AND",
      "orderIndex": 1
    }
  ],
  "groups": []
}
```

### 9. Хочу користувачів з України з Android АБО з Німеччини з iOS
```json
{
  "filterName": "UA+Android або DE+iOS",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [],
  "groups": [
    {
      "groupName": "Україна Android",
      "logicalOperator": "AND",
      "orderIndex": 0,
      "conditions": [
        {
          "fieldType": "COUNTRY",
          "operator": "EQUAL",
          "fieldValue": "UA",
          "logicalOperator": null,
          "orderIndex": 0
        },
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "Android",
          "logicalOperator": "AND",
          "orderIndex": 1
        }
      ]
    },
    {
      "groupName": "Німеччина iOS",
      "logicalOperator": "OR",
      "orderIndex": 1,
      "conditions": [
        {
          "fieldType": "COUNTRY",
          "operator": "EQUAL",
          "fieldValue": "DE",
          "logicalOperator": null,
          "orderIndex": 0
        },
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "iOS",
          "logicalOperator": "AND",
          "orderIndex": 1
        }
      ]
    }
  ]
}
```

### 10. Хочу VIP користувачів з мобільних платформ з сучасними браузерами
```json
{
  "filterName": "VIP мобільні сучасні",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [],
  "groups": [
    {
      "groupName": "VIP SMID",
      "logicalOperator": "AND",
      "orderIndex": 0,
      "conditions": [
        {
          "fieldType": "SMID",
          "operator": "IN",
          "fieldValue": "[\"3586067540\",\"9876543210\",\"1111111111\"]",
          "logicalOperator": null,
          "orderIndex": 0
        }
      ]
    },
    {
      "groupName": "Мобільні ОС",
      "logicalOperator": "AND",
      "orderIndex": 1,
      "conditions": [
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "Android",
          "logicalOperator": null,
          "orderIndex": 0
        },
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "iOS",
          "logicalOperator": "OR",
          "orderIndex": 1
        }
      ]
    },
    {
      "groupName": "Сучасні браузери",
      "logicalOperator": "AND",
      "orderIndex": 2,
      "conditions": [
        {
          "fieldType": "BROWSER",
          "operator": "NOT_IN",
          "fieldValue": "[\"Internet Explorer\",\"Opera Mini\"]",
          "logicalOperator": null,
          "orderIndex": 0
        }
      ]
    }
  ]
}
```

### 11. Хочу користувачів які НЕ з Білорусі І мають версію більше 2.2.0 І (Android АБО iOS)
```json
{
  "filterName": "Не BY + версія 2.2.0+ + мобільні",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "COUNTRY",
      "operator": "NOT_EQUAL",
      "fieldValue": "BY",
      "logicalOperator": null,
      "orderIndex": 0
    },
    {
      "fieldType": "CLIENT_VERSION",
      "operator": "GREATER_THAN",
      "fieldValue": "2.2.0",
      "logicalOperator": "AND",
      "orderIndex": 1
    }
  ],
  "groups": [
    {
      "groupName": "Мобільні платформи",
      "logicalOperator": "AND",
      "orderIndex": 0,
      "conditions": [
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "Android",
          "logicalOperator": null,
          "orderIndex": 0
        },
        {
          "fieldType": "OPERATING_SYSTEM",
          "operator": "CONTAINS",
          "fieldValue": "iOS",
          "logicalOperator": "OR",
          "orderIndex": 1
        }
      ]
    }
  ]
}
```

### 12. Хочу користувачів з SMID що починається на "358"
```json
{
  "filterName": "SMID починається на 358",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "SMID",
      "operator": "STARTS_WITH",
      "fieldValue": "358",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 13. Хочу користувачів з браузером що містить "Chrome"
```json
{
  "filterName": "Браузери з Chrome",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "BROWSER",
      "operator": "CONTAINS",
      "fieldValue": "Chrome",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 14. Хочу користувачів з WiFi підключенням
```json
{
  "filterName": "Тільки WiFi",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "INTERNET_TYPE",
      "operator": "EQUAL",
      "fieldValue": "WiFi",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

### 15. Хочу користувачів з активною CLE кампанією
```json
{
  "filterName": "З CLE кампанією",
  "marketingTargetId": 1,
  "isActive": true,
  "conditions": [
    {
      "fieldType": "CLE_CAMPAIGN",
      "operator": "IS_NOT_NULL",
      "fieldValue": "",
      "logicalOperator": null,
      "orderIndex": 0
    }
  ],
  "groups": []
}
```

## Важливі правила

1. **logicalOperator = null** тільки для першої умови в root або в групі
2. **logicalOperator для груп** завжди вказувати (навіть для першої групи використовувати `"AND"`)⚠️
3. **orderIndex** обов'язковий і починається з 0
4. **fieldValue** для IN/NOT_IN як JSON масив: `"[\"val1\",\"val2\"]"`
5. **Групи** для складної логіки типу (A OR B) AND (C OR D)
6. **Root умови** виконуються першими, потім групи

⚠️ **Примітка:** Валідація API вимагає обов'язкове вказання `logicalOperator` для всіх груп. Навіть якщо група перша, використовуйте `"logicalOperator": "AND"` замість `null`.

## Пояснення Conditions vs Groups

### Conditions (Root-level умови)
**Для чого:** Прості умови на верхньому рівні фільтра
**Як працює:** Всі умови об'єднуються логічними операторами послідовно

**Приклад:** `COUNTRY = "UA" AND VERSION >= "2.2.0"`
```json
"conditions": [
  {
    "fieldType": "COUNTRY",
    "operator": "EQUAL", 
    "fieldValue": "UA",
    "logicalOperator": null,
    "orderIndex": 0
  },
  {
    "fieldType": "CLIENT_VERSION",
    "operator": "GREATER_THAN_OR_EQUAL",
    "fieldValue": "2.2.0", 
    "logicalOperator": "AND",
    "orderIndex": 1
  }
]
```

### Groups (Групи умов)
**Для чого:** Складні логічні блоки з дужками
**Як працює:** Кожна група - окремий блок з власною логікою всередині

**Приклад:** `(COUNTRY = "UA" OR COUNTRY = "DE") AND (OS Co "Android" OR OS Co "iOS")`
```json
"groups": [
  {
    "groupName": "Країни",
    "logicalOperator": "AND",
    "orderIndex": 0,
    "conditions": [
      {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "UA", "logicalOperator": null, "orderIndex": 0},
      {"fieldType": "COUNTRY", "operator": "EQUAL", "fieldValue": "DE", "logicalOperator": "OR", "orderIndex": 1}
    ]
  },
  {
    "groupName": "Мобільні ОС", 
    "logicalOperator": "AND",
    "orderIndex": 1,
    "conditions": [
      {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "Android", "logicalOperator": null, "orderIndex": 0},
      {"fieldType": "OPERATING_SYSTEM", "operator": "CONTAINS", "fieldValue": "iOS", "logicalOperator": "OR", "orderIndex": 1}
    ]
  }
]
```

### Коли використовувати що:

**Використовуй Conditions коли:**
- Прості умови через AND: `країна = UA І версія >= 2.2.0`
- Прості умови через OR: `країна = UA АБО країна = DE` (але краще через IN)
- Одна умова: `тільки Україна`

**Використовуй Groups коли:**
- Потрібні дужки: `(A OR B) AND (C OR D)`
- Складна логіка: `VIP користувачі І (мобільні АБО планшети) І (не старі браузери)`
- Комбінації умов: `(Україна + Android) АБО (Німеччина + iOS)`

### Логіка обчислення:
1. **Спочатку** обчислюються всі root conditions
2. **Потім** обчислюється кожна група окремо  
3. **В кінці** результати об'єднуються: `ROOT_RESULT AND GROUP1_RESULT AND GROUP2_RESULT`

**Приклад:**
```
ROOT: version >= "2.2.0" AND country != "BY"  → true/false
GROUP1: (smid = "123" OR smid = "456")        → true/false  
GROUP2: (os Co "Android" OR os Co "iOS")      → true/false

ФІНАЛЬНИЙ РЕЗУЛЬТАТ: ROOT AND GROUP1 AND GROUP2
```

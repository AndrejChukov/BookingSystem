# 🏨 Booking System REST API

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0%2B-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

RESTful API для автоматизации бронирования переговорных комнат и управления офисным оборудованием. Проект демонстрирует реализацию многослойной архитектуры, ролевой модели доступа на базе JWT-токенов, практик оптимизации работы с реляционной базой данных и автоматизированного тестирования в контейнерах.

## 📑 Оглавление
- [Технологический стек](#-технологический-стек)
- [Ключевые архитектурные решения](#-ключевые-архитектурные-решения)
- [Локальный запуск и Swagger](#-локальный-запуск-и-swagger)
- [API Документация](#-api-документация)
- [Тестирование](#-тестирование)
- [Направления для развития (Roadmap)](#-направления-для-развития-roadmap)

---

## 🛠 Технологический стек

* **Core:** Java 17, Spring Boot 3
* **Security:** Spring Security, OAuth2 Resource Server, Nimbus JOSE JWT
* **Data Access & Migrations:** Spring Data JPA, Hibernate, PostgreSQL, Flyway (миграция)
* **API Documentation:** Springdoc OpenAPI, Swagger UI
* **Testing:** JUnit 5, Mockito, Testcontainers (PostgreSQL для интеграционных тестов)

---

## ✨ Ключевые архитектурные решения

1. 🔐 **Stateless Аутентификация:** Использование JWT токенов.
2. 👮‍♂️ **Ролевая модель доступа (RBAC):** Настроены роли `USER`, `WORKER` и `ADMIN`.
3. 🚀 **Предотвращение N+1 проблем:** При получении списков комнат вместе с их оборудованием (связь Many-to-Many) используется `@EntityGraph` для загрузки зависимостей в рамках одного запроса.
4. 🔄 **Разделение сущностей и DTO:** Применение паттерна DTO минимизирует передачу избыточных данных.
5. ⏱ **Автоматический аудит изменений:** Использование возможностей `@EnableJpaAuditing` для автоматического отслеживания времени создания (`createdAt`) и редактирования (`updatedAt`) сущностей, унаследованных от `BaseEntity`.
6. 🗃 **Контроль версий БД:** Настроены инструменты миграции (Flyway).
7. 🩺 **Интеграционное тестирование в контейнерах**
8. 🛡 **Валидация входящих данных**

---

## 🚀 Локальный запуск и Swagger

### Предварительные требования
* **JDK 17** или выше
* **Maven 3.8** или выше
* **Docker** (необходим для запуска интеграционных тестов через Testcontainers)

### Инструкция по сборке и запуску

1. Склонируйте исходный код:
   ```bash
   git clone https://github.com/AndrejChukov/bookingsystem.git
   cd bookingsystem
   ```

2. Настройте конфигурационные параметры в файле настроек `application.yml` или `application.properties`:
   ```properties
   spring.security.jwt.secret-key=your_256_bit_secret_key_here_must_be_long_and_secure
   spring.security.jwt.expiration-time=60
   spring.security.jwt.algorithm=HS256
   
   # Настройки подключения к PostgreSQL (пример для локальной БД)
   spring.datasource.url=jdbc:postgresql://localhost:5432/bookingsystem
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. Выполните сборку и запуск приложения:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

По умолчанию приложение запускается на порту `8080`.

### 📖 Интерактивная документация (Swagger UI)
Спецификация API автоматически генерируется при запуске приложения. Ознакомиться со всеми эндпоинтами и отправить тестовые запросы можно через веб-интерфейс:
* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 📚 API Документация (Краткая выжимка)

> **Примечание:** Для работы с защищенными эндпоинтами необходимо передавать HTTP-заголовок `Authorization: Bearer <token>`. Полное описание схем запросов и ответов доступно в Swagger UI.

### 🔑 Аутентификация и пользователи
| HTTP Метод | Эндпоинт | Описание | Роль / Доступ |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Регистрация нового аккаунта | Публичный |
| `POST` | `/api/auth/signin` | Вход и получение JWT-токена | Публичный |
| `GET`  | `/api/user` | Получение информации о текущей сессии | `USER`, `WORKER`, `ADMIN` |

### 🚪 Комнаты (Rooms)
| HTTP Метод | Эндпоинт | Описание | Роль / Доступ |
| :--- | :--- | :--- | :--- |
| `GET`  | `/api/rooms/available` | Список свободных комнат | Публичный |
| `GET`  | `/api/rooms` | Получить список всех комнат (с сортировкой) | `WORKER`, `ADMIN` |
| `GET`  | `/api/room/{id}` | Детальная информация о комнате | Публичный |
| `POST` | `/api/room` | Добавление новой комнаты | `ADMIN` |
| `PUT`  | `/api/room/{id}` | Обновление характеристик комнаты | `WORKER`, `ADMIN` |
| `DELETE`| `/api/room/{id}` | Удаление комнаты | `ADMIN` |

### 💻 Оборудование (Equipment)
| HTTP Метод | Эндпоинт | Описание | Роль / Доступ |
| :--- | :--- | :--- | :--- |
| `GET`  | `/api/equipments` | Список всего инвентаря | `WORKER`, `ADMIN` |
| `GET`  | `/api/equipment/{id}`| Информация об единице оборудования | `WORKER`, `ADMIN` |
| `POST` | `/api/equipment` | Добавление новой единицы оборудования | `WORKER`, `ADMIN` |
| `PUT`  | `/api/equipment/{id}`| Редактирование оборудования | `WORKER`, `ADMIN` |
| `DELETE`| `/api/equipment/{id}`| Удаление/списание оборудования | `WORKER`, `ADMIN` |

---

## 🧪 Тестирование

Проект содержит как модульные (Unit), так и интеграционные тесты.

* **Unit-тесты:** Изолированное тестирование бизнес-логики сервисов с использованием JUnit 5 и Mockito.
* **Интеграционные тесты:** Тестирование взаимодействия с базой данных в реальном времени с использованием Testcontainers (PostgreSQL).

Запуск полного тестового набора:
```bash
mvn test
```

---

## 📈 Направления для развития (Roadmap)

Для трансформации данного решения в отказоустойчивую промышленную систему намечены следующие шаги:

- [ ] **Кеширование данных:** Подключение **Redis** для снижения нагрузки на БД при частых запросах к спискам доступных комнат.
- [ ] **Инфраструктура:** Настройка Docker-контейнеризации приложения и СУБД (создание `Dockerfile` и `docker-compose.yml`) для быстрого развертывания.
- [ ] **Логирование:** Перевод логов в формат JSON (через Logback) для последующей интеграции со стеком централизованного сбора логов (ELK/Graylog).

---
*Developed by [Andrej Chuchkalov](https://t.me/Andrej_ch)*

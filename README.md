# 🏨 Booking System REST API

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0%2B-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

RESTful API для автоматизации бронирования переговорных комнат и управления офисным оборудованием. Проект демонстрирует реализацию многослойной архитектуры (N-Tier), ролевой модели доступа на базе JWT-токенов и практик оптимизации работы с реляционной базой данных.

## 📑 Оглавление
- [Технологический стек](#-технологический-стек)
- [Ключевые архитектурные решения](#-ключевые-архитектурные-решения)
- [Локальный запуск](#-локальный-запуск)
- [API Документация](#-api-документация)
- [Тестирование](#-тестирование)
- [Направления для развития (Roadmap)](#-направления-для-развития-roadmap)

---

## 🛠 Технологический стек

* **Core:** Java 17, Spring Boot 3
* **Security:** Spring Security, OAuth2 Resource Server, Nimbus JOSE JWT
* **Data Access:** Spring Data JPA, Hibernate, H2 Database (In-memory среда для разработки)
* **Mapping & Boilerplate:** MapStruct, Lombok
* **Testing:** JUnit 5, Mockito

---

## ✨ Ключевые архитектурные решения

1. 🔐 **Stateless Аутентификация:** Использование JWT токенов (библиотека Nimbus). Хеширование паролей пользователей выполняется через `BCryptPasswordEncoder`.
2. 👮‍♂️ **Ролевая модель доступа (RBAC):** Настроены роли `USER`, `WORKER` и `ADMIN`. Ограничение доступа к эндпоинтам реализовано с помощью аннотаций `@PreAuthorize`.
3. 🚀 **Предотвращение N+1 проблем:** При получении списков комнат вместе с их оборудованием (связь Many-to-Many) используется `@EntityGraph` для загрузки зависимостей в рамках одного запроса.
4. 🔄 **Разделение сущностей и DTO:** Применение паттерна DTO минимизирует передачу избыточных данных. Маппинг сущностей в DTO и обратно автоматизирован через генерацию кода компилятором с помощью `MapStruct`.
5. ⏱ **Автоматический аудит изменений:** Использование возможностей `@EnableJpaAuditing` для автоматического отслеживания времени создания (`createdAt`) и редактирования (`updatedAt`) сущностей.

---

## 🚀 Локальный запуск

### Предварительные требования
* **JDK 17** или выше
* **Maven 3.8** или выше

### Инструкция по сборке и запуску

1. Склонируйте исходный код:
   ```bash
   git clone https://github.com/your-username/bookingsystem.git
   cd bookingsystem
   ```

2. Настройте конфигурационные параметры в файле настроек `application.yml` или `application.properties`:
   ```properties
   spring.security.jwt.secret-key=your_256_bit_secret_key_here_must_be_long_and_secure
   spring.security.jwt.expiration-time=60
   spring.security.jwt.algorithm=HS256
   ```

3. Выполните сборку и запуск приложения:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

По умолчанию приложение запускается на порту `8080`.
Консоль базы данных H2 доступна по адресу: `http://localhost:8080/h2-console`.

---

## 📚 API Документация

> **Примечание:** Для работы с защищенными эндпоинтами необходимо передавать HTTP-заголовок `Authorization: Bearer <token>`.

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

Бизнес-логика покрыта модульными тестами с изоляцией зависимостей через моки. Для тестирования используются JUnit 5 и Mockito.

Запуск тестового набора:
```bash
mvn test
```

---

## 📈 Направления для развития (Roadmap)

Для трансформации данного MVP в масштабируемое промышленное решение планируется реализация следующих задач:

- [ ] **Миграции базы данных:** Интеграция инструмента **Liquibase** или **Flyway** для версионирования схемы БД вместо автогенерации через Hibernate.
- [ ] **Интеграционное тестирование:** Добавление тестов с использованием библиотеки **Testcontainers** (PostgreSQL) для верификации работы репозиториев и мапперов в реальной СУБД.
- [ ] **Документирование API:** Интеграция библиотеки `springdoc-openapi` для интерактивной визуализации спецификации API через Swagger UI.
- [ ] **Кеширование данных:** Подключение **Redis** для снижения нагрузки на БД при частых запросах к спискам доступных комнат.
- [ ] **Инфраструктура:** Настройка Docker-контейнеризации приложения и СУБД для упрощения развертывания проекта.

---
*Developed by [Andrej Chuchkalov]([https://github.сылка](https://github.com/AndrejChukov))*

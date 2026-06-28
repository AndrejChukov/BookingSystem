# 🏨 Booking System REST API

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4%2B-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

REST API для автоматизации бронирования переговорных комнат и учета офисного оборудования. Включает авторизацию на базе JWT, кеширование данных через Redis, оптимизированную работу с JPA/Hibernate, версионирование схемы БД и логирование в формате ECS.

## 📑 Оглавление
- [Бизнес-логика](#-бизнес-логика)
- [Технологический стек](#-технологический-стек)
- [Ключевые архитектурные решения](#-ключевые-архитектурные-решения)
- [Профили окружения (Spring Profiles)](#-профили-окружения-spring-profiles)
- [Быстрый запуск (Docker Compose)](#-быстрый-запуск-docker-compose)
- [Локальный запуск без контейнеров](#-локальный-запуск-без-контейнеров)
- [API Документация (Краткая выжимка)](#-api-документация-краткая-выжимка)
- [Тестирование](#-тестирование)
- [Направления для развития (Roadmap)](#-направления-для-развития-roadmap)

---

## 💼 Бизнес-логика
1. ⚙️**Продвинутая система бронирования (Booking Engine)**: Умный алгоритм проверки доступности, который предотвращает двойное бронирование (Double Booking) и строго валидирует временные интервалы.
2. 🔑**Ролевая модель доступа**: Разграничение прав между ролями USER (создание и просмотр своих броней), WORKER (обслуживание) и ADMIN (полное управление ресурсами и чужими бронированиями).
3. 📚**Управление ресурсами**: Полноценный CRUD для комнат/ресурсов с возможностью изменения их статуса (например, вывод из эксплуатации).
4. ⏱**Аудит действий**: Автоматическое отслеживание того, кто и когда создал или изменил запись в системе.

---

## 🛠 Технологический стек

* **Core:** Java 17, Spring Boot 3.4+
* **Security:** Spring Security, OAuth2 Resource Server, Nimbus JOSE JWT
* **Data Access & Migrations:** Spring Data JPA, Hibernate, PostgreSQL, Flyway (миграции)
* **Caching:** Redis, Spring Cache
* **Infrastructure:** Docker, Docker Compose, Spring Profiles
* **Logging:** Structured Logback logging (стандарт Elastic Common Schema - ECS)
* **API Documentation:** Springdoc OpenAPI, Swagger UI
* **Testing:** JUnit 5, Mockito, Testcontainers (PostgreSQL для интеграционных тестов)

---

## ✨ Ключевые архитектурные решения

1. 🔐 **Безопасность (JWT + RBAC):** Stateless-аутентификация на JWT-токенах и разграничение доступа для ролей `USER`, `WORKER` и `ADMIN`.
2. ⚡ **Оптимизация работы с JPA/Hibernate:** Решение проблемы N+1 с помощью графов сущностей, пакетная предвыборка (batch fetching) для Lazy Loading и строгая валидация схемы БД при запуске.
3. 🔄 **Разделение слоев (DTO & MapStruct):** Четкое разделение доменной модели и внешнего API.
5. 🗃 **Управление схемой данных (Flyway):** Версионирование структуры таблиц с помощью миграционных скриптов.
6. 💾 **Кеширование на базе Redis:** Оптимизация чтения часто запрашиваемых данных с автоматической инвалидацией кеша при внесении изменений.
7. 🩺 **Интеграционное тестирование (Testcontainers):** Верификация слоя доступа к данным в изолированных контейнерах с реальной СУБД PostgreSQL.
8. 🐳 **Контейнеризация и оркестрация:** Развертывание приложения и всей сопутствующей инфраструктуры через Docker и Docker Compose.
9. 📊 **Структурированное логирование (ECS):** Перевод логов в JSON-формат стандарта Elastic Common Schema для упрощения интеграции со стеками ELK / Graylog.

---

## ⚙️ Профили окружения (Spring Profiles)

В проекте настроена конфигурация под разные среды выполнения:

* **Профиль по умолчанию (`default` / `application.yaml`):** Оптимизирован для локального запуска и отладки. База данных и Redis ожидаются на `localhost`, логи выводятся в стандартном текстовом представлении.
* **Профиль `prod` (`application-prod.yaml`):** Активируется в контейнеризированной среде. Переводит логирование в структурированный JSON-формат (ECS) и устанавливает уровень логирования `INFO` для пакетов приложения.

---

## 🐳 Быстрый запуск (Docker Compose)

Для автоматического запуска приложения в профиле `prod` вместе с базой данных и сервером кеширования:

1. Склонируйте репозиторий:
   ```bash
   git clone https://github.com/AndrejChukov/bookingsystem.git
   cd bookingsystem
   ```

2. Запустите сборку и стек контейнеров:
   ```bash
   docker-compose up --build
   ```

3. После успешного старта сервисы будут доступны по адресам:
   * **Приложение REST API:** `http://localhost:8080`
   * **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## 🚀 Локальный запуск без контейнеров

### Предварительные требования
* **JDK 17** или выше
* **Maven 3.8** или выше
* Локально запущенные СУБД **PostgreSQL** (имя БД по умолчанию: `bookingdb`) и **Redis**

### Настройка и запуск

1. Сверьте конфигурационные параметры локальной среды в `application.yaml`:
   * Порты PostgreSQL: `5432`, Redis: `6379`

2. Соберите и запустите проект:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

---

## 📚 API Документация (Краткая выжимка)

> **Примечание:** Для работы с защищенными эндпоинтами необходимо передавать HTTP-заголовок `Authorization: Bearer <token>`. Полная спецификация доступна в интерфейсе Swagger UI.

### 🔑 Аутентификация и пользователи
| HTTP Метод | Эндпоинт | Описание | Роль / Доступ |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Регистрация нового аккаунта | Публичный |
| `POST` | `/api/auth/signin` | Вход и получение JWT-токена | Публичный |
| `GET`  | `/api/user` | Получение информации о текущей сессии | `USER`, `WORKER`, `ADMIN` |

### 🚪 Комнаты (Rooms)
| HTTP Метод | Эндпоинт | Описание | Роль / Доступ |
| :--- | :--- | :--- | :--- |
| `GET`  | `/api/rooms/available` | Список свободных комнат *(Кешируется)* | Публичный |
| `GET`  | `/api/rooms` | Получить список всех комнат (с сортировкой) | `WORKER`, `ADMIN` |
| `GET`  | `/api/room/{id}` | Детальная информация о комнате | Публичный |
| `POST` | `/api/room` | Добавление новой комнаты *(Сброс кеша)* | `ADMIN` |
| `PUT`  | `/api/room/{id}` | Обновление характеристик комнаты *(Сброс кеша)*| `WORKER`, `ADMIN` |
| `DELETE`| `/api/room/{id}` | Удаление комнаты *(Сброс кеша)* | `ADMIN` |

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

Проект содержит модульные и интеграционные тесты:

* **Unit-тесты:** Изолированное тестирование бизнес-логики сервисов с использованием JUnit 5 и Mockito.
* **Интеграционные тесты:** Проверка взаимодействия с СУБД в реальном времени с использованием Testcontainers (PostgreSQL).

Запуск тестов:
```bash
mvn test
```

---

## 📈 Направления для развития (Roadmap)

Основные компоненты промышленной архитектуры успешно интегрированы. Дальнейшее развитие проекта предполагает:

- [ ] **Мониторинг:** Добавление Spring Boot Actuator и интеграция с Prometheus и Grafana для сбора метрик JVM и приложения.
- [ ] **CI/CD:** Настройка пайплайна автоматической сборки, тестирования и контейнеризации приложения на базе GitHub Actions.

---
*Developed by [Andrej Chuchkalov](https://t.me/Andrej_ch)*

# 🔐 Authentication and Registration API - Spring Boot

This project is a REST API for user authentication and management, developed with **Spring Boot**, **JWT** for authentication, and **MySQL** as the database. It is deployed on **Railway**.

## 🚀 Features

- User registration.
- Login with JWT authentication.
- Route protection with tokens.
- Retrieve authenticated user information.

---

## 🛠 Technologies Used

- **Spring Boot** (Spring Security, Spring Data JPA, Spring Web)
- **JWT (JSON Web Token)** for authentication
- **MySQL** as the database
- **JPA / Hibernate** for data persistence
- **Lombok** to simplify code
- **Maven** for dependency management
- **Railway** for cloud deployment

---

## 📌 Prerequisites

- **Java 17+**
- **Maven**
- **MySQL** (Optional for local execution)

---

## 📦 Installation and Execution

1. **Clone the repository**
```sh
   git clone https://github.com/your-username/your-repo.git
   cd your-repo
```

2. **Configure `application.properties`**
   
Create a file in `src/main/resources/application.properties` and set up the database and JWT secret:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/my_database
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
jwt.secret=super-secret-key
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=emailtosendcodes@example.co
spring.mail.password=app password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

3. **Build and run**
```sh
   mvn clean install
   mvn spring-boot:run
```

---

## 🔑 Available Endpoints

### 📌 Authentication
| Method | Endpoint         | Description |
|--------|-----------------|-------------|
| `POST`  | `/auth/signup` | Register user |
| `POST`  | `/auth/login`    | Log in (Returns a token) |
| `POST`  | `/auth/send-reset-code` | Send an email with a validation code |
| `POST`  | `/auth/reset-password`    | Requires new password, email and validation code |


### 📌 Users (Protected with JWT)
| Method | Endpoint         | Description |
|--------|-----------------|-------------|
| `GET`   | `/users/me`  | Retrieve authenticated user information |

---

## 🛡 Authentication with JWT

To access protected routes, include the token in the `Authorization` header:
```sh
Authorization: Bearer your_token_here
```

Example using **cURL**:
```sh
curl -H "Authorization: Bearer your_token_here" -X GET http://localhost:8081/users/me
```

---

## 🚀 Deployment on Railway

The project is deployed on Railway. To deploy manually:
1. Connect the repository to Railway.
2. Configure environment variables (`DATABASE_URL`, `JWT_SECRET`).
3. Railway will handle the automatic deployment. 🚀

4. **Access the API**
1. Open your browser and navigate to `http://localhost:8081/swagger-ui/index.html`
2. Use Postman or any other API client to test the endpoints.

---

## 📝 License

This project is licensed under the MIT License. You are free to use, modify, and distribute it. 😊


# Chat-Chit: Ứng dụng Chat tích hợp AI

Đây là một dự án ứng dụng web full-stack, bao gồm một backend RESTful API được xây dựng bằng Spring Boot và một frontend được xây dựng bằng React (TypeScript). Ứng dụng cho phép người dùng đăng ký, đăng nhập, chat với nhau và tương tác với một mô hình ngôn ngữ lớn (LLM) được cung cấp bởi Ollama.

## Tính năng chính

- **Xác thực người dùng:** Đăng ký, đăng nhập bằng email/mật khẩu và đăng nhập qua Google, Facebook (OAuth2).
- **Quản lý phiên:** Sử dụng JSON Web Tokens (JWT) với Refresh Token.
- **Chat:** Giao diện chat real-time (sẽ được cập nhật).
- **Tích hợp AI:** Trò chuyện với các mô hình AI từ Ollama.
- **Quản lý người dùng:** Giao diện quản trị để xem và quản lý trạng thái người dùng.
- **Tài liệu API:** Tự động tạo tài liệu API với Swagger (OpenAPI).

## Cây thư mục

```
/
├── client/         # Mã nguồn Frontend (React + Vite)
├── src/            # Mã nguồn Backend (Spring Boot)
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── .gitignore
├── mvnw
├── pom.xml         # File cấu hình Maven cho backend
└── README.md
```

## Công nghệ sử dụng

### Backend
- **Java 17**
- **Spring Boot 3:** Framework chính cho ứng dụng.
- **Spring Security:** Xử lý xác thực và phân quyền (JWT, OAuth2).
- **Spring Data JPA:** Tương tác với cơ sở dữ liệu.
- **PostgreSQL/MySQL:** Cơ sở dữ liệu quan hệ (có thể cấu hình trong `application.properties`).
- **Maven:** Quản lý gói và xây dựng dự án.
- **Ollama:** Tích hợp mô hình ngôn ngữ lớn.
- **Swagger/OpenAPI 3:** Cung cấp tài liệu cho API.

### Frontend
- **React 18**
- **TypeScript**
- **Vite:** Công cụ xây dựng frontend.
- **Tailwind CSS:** Framework CSS cho việc tạo giao diện.
- **Axios:** HTTP client để gọi API từ backend.
- **ESLint/Prettier:** Công cụ kiểm tra và định dạng mã nguồn.

## Yêu cầu hệ thống

- **JDK 17** trở lên.
- **Maven 3.8** trở lên.
- **Node.js 18** trở lên và **npm**.
- **Ollama** đã được cài đặt và đang chạy (tham khảo tại [https://ollama.com/](https://ollama.com/)).
- Một hệ quản trị cơ sở dữ liệu (ví dụ: PostgreSQL, MySQL).

## Hướng dẫn cài đặt và chạy dự án

### 1. Clone dự án

```bash
git clone <URL_repository>
cd Chat-Chit
```

### 2. Cài đặt Backend

1.  **Cấu hình:**
    Mở file `src/main/resources/application.properties` và cấu hình các thông tin sau:
    - **Cơ sở dữ liệu:**
      ```properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      spring.jpa.hibernate.ddl-auto=update
      ```
    - **Ollama:**
      ```properties
      ollama.base.url=http://localhost:11434
      ```
    - **JWT Secret & Expiration:**
      ```properties
      jwt.secret=your_super_secret_key_with_at_least_256_bits
      jwt.expiration.ms=86400000 # 24 hours
      jwt.refresh.token.expiration.ms=604800000 # 7 days
      ```

2.  **Chạy ứng dụng:**
    Mở terminal tại thư mục gốc của dự án và chạy lệnh:
    ```bash
    ./mvnw spring-boot:run
    ```
    Backend sẽ khởi động và chạy tại `http://localhost:8081`.

### 3. Cài đặt Frontend

1.  **Di chuyển vào thư mục client:**
    ```bash
    cd client
    ```

2.  **Cài đặt các gói phụ thuộc:**
    ```bash
    npm install
    ```

3.  **Chạy ứng dụng:**
    ```bash
    npm run dev
    ```
    Frontend sẽ khởi động và chạy tại `http://localhost:5173`.

### 4. Truy cập ứng dụng

- **Giao diện người dùng:** [http://localhost:5173](http://localhost:5173)
- **Tài liệu API (Swagger):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Định nghĩa OpenAPI:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---
Chúc bạn thành công!

# Tour Booking System

Hệ thống đặt tour du lịch trực tuyến xây dựng bằng **Spring Boot 4** + **Thymeleaf** + **MySQL**.
Hỗ trợ hai vai trò chính: **Khách hàng (Customer)** và **Quản trị viên (Admin)**.

> Tác giả: **Nguyễn Công Mạnh** — congmanh835@gmail.com

---

## 1. Tech stack

| Lớp | Công nghệ |
|---|---|
| Backend | Spring Boot 4.0.3, Spring MVC, Spring Data JPA, Spring Security, Spring Mail |
| Auth | Form login + OAuth2 Google login |
| Frontend | Thymeleaf + Bootstrap, JS thuần |
| Database | MySQL 8 (`tourbookingdb`) |
| Build | Maven (Java 21, đóng gói WAR) |
| Khác | Spring Boot Actuator, DevTools, Validation, Gemini AI (chat tư vấn) |

---

## 2. Cấu trúc thư mục

```
project/
├── config/
│   ├── ConfigApplication.properties   # Override config (mail, gemini key, oauth client id/secret...)
│   └── OAuthGG.json                   # Google OAuth credentials (KHÔNG commit)
├── src/main/java/com/spring/project/
│   ├── config/        # SecurityConfig, WebConfig, OAuth2, Mail...
│   ├── controller/
│   │   ├── admin/     # Trang quản trị
│   │   ├── client/    # Trang khách hàng
│   │   └── ChatController.java
│   ├── dto/           # Request/Response DTOs
│   ├── entity/        # JPA entities (User, Tour, Booking, Payment, Review...)
│   ├── repository/    # Spring Data JPA repositories
│   ├── security/      # CustomUserDetails, OAuth2 success handler...
│   └── service/       # Business logic + impl/
├── src/main/resources/
│   ├── application.properties
│   ├── schema-init.sql                # DDL + seed roles, chạy mỗi lần khởi động (idempotent)
│   ├── ssl/                           # SSL cert/key cho HTTPS local (KHÔNG commit)
│   ├── static/                        # CSS, JS, images
│   └── templates/
│       ├── admin/                     # Dashboard, CRUD nhân viên/tour/booking/promotion...
│       └── client/                    # Home, tour list/detail, booking, payment, review...
└── uploads/tours/                     # Ảnh tour do admin upload (runtime)
```

---

## 3. Yêu cầu hệ thống

- **JDK 21+**
- **Maven 3.9+** (hoặc dùng `mvnw` đi kèm)
- **MySQL 8** đang chạy ở `localhost:3306`, có database `tourbookingdb`

---

## 4. Cài đặt & chạy

### 4.1. Clone & tạo database

```bash
git clone <repo-url>
cd project

mysql -u root -p -e "CREATE DATABASE tourbookingdb DEFAULT CHARACTER SET utf8mb4;"
```

Schema và seed roles sẽ được tự động chạy từ `schema-init.sql` mỗi lần app khởi động.

### 4.2. Cấu hình

Mở `src/main/resources/application.properties` và sửa username/password MySQL nếu khác mặc định:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tourbookingdb
spring.datasource.username=root
spring.datasource.password=12345
```

Tạo file `config/ConfigApplication.properties` (ngoài source tree) để override các thông tin nhạy cảm:

```properties
# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=<your-client-id>
spring.security.oauth2.client.registration.google.client-secret=<your-secret>

# Spring Mail (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your-email>
spring.mail.password=<app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Gemini AI (chat tư vấn)
gemini.api-key=<your-gemini-key>
```

### 4.3. Chạy app

```bash
./mvnw spring-boot:run         # Linux/macOS
mvnw.cmd spring-boot:run       # Windows
```

App khởi động tại: <http://localhost:8080>

### 4.4. Tài khoản mặc định

Sau khi `schema-init.sql` chạy, có thể đăng nhập với admin seed (xem trong file SQL),
hoặc bật cờ tự động đăng nhập admin khi dev:

```properties
app.dev-auto-login.enabled=true
```

---

## 5. Tính năng

### 5.1. Khách hàng

- Đăng ký, đăng nhập (form + Google OAuth2)
- Quên mật khẩu (gửi mail reset)
- Cập nhật thông tin cá nhân, đổi mật khẩu
- Tìm kiếm & lọc tour (theo điểm đến, danh mục, thời gian)
- Xem chi tiết tour (lịch trình, ảnh, đánh giá, ngày khởi hành)
- Đặt tour, sửa booking, hủy booking (theo trạng thái PENDING/CONFIRMED)
- Thanh toán (demo, tự đánh dấu PAID)
- Xem lịch sử đặt tour
- Đánh giá tour sau khi hoàn thành
- Chat tư vấn (Gemini AI)

### 5.2. Quản trị viên

- Dashboard tổng quan
- CRUD **Nhân viên** (USER có role STAFF/ADMIN)
- CRUD **Tour** (kèm ảnh, lịch trình, ngày khởi hành)
- Quản lý **Đơn đặt** (cập nhật trạng thái, xóa)
- CRUD **Khuyến mãi** (mã giảm giá)
- Quản lý **Khách hàng** (xem, tìm kiếm, khóa/mở khóa tài khoản)

---

## 6. Bảng dữ liệu chính

`users`, `roles`, `user_auth_providers`, `destinations`, `tour_categories`, `tours`,
`tour_images`, `tour_itineraries`, `tour_departures`, `tour_promotions`, `promotions`,
`bookings`, `booking_travelers`, `payments`, `reviews`.

> `schema-init.sql` đã bao gồm phần `ADD INDEXES` cho dataset 1M+ bookings —
> tối ưu các query sort theo `created_at`, filter theo `booking_status`, `payment_status`...

---

## 7. URL quan trọng

| Path | Vai trò |
|---|---|
| `/` | Trang chủ khách hàng |
| `/tours`, `/tours/detail?id=` | Danh sách & chi tiết tour |
| `/booking/create`, `/booking/history` | Đặt & xem booking |
| `/payment` | Thanh toán |
| `/login`, `/register` | Auth |
| `/admin/**` | Khu vực quản trị (yêu cầu role ADMIN) |
| `/actuator/health` | Health check |

---

## 8. Các thành phần chưa hoàn chỉnh (nếu xét như sản phẩm thật)

Trích từ `usecase.txt`:

1. **Thanh toán thật**: hiện tự set PAID, chưa tích hợp VNPay/MoMo callback, chưa xử lý refund khi hủy.
2. **Reset mật khẩu**: hiện reset cứng về `123456`. Cần token qua email + rate-limit.
3. **Contact form**: chỉ là UI, chưa có controller POST.
4. **Admin quản lý đánh giá**: customer review được nhưng admin chưa có màn ẩn/xóa.
5. **Cập nhật lịch khởi hành**: mới có add/delete, chưa có update.
6. **CRUD danh mục tour & điểm đến**: admin mới chỉ chọn dữ liệu seed.
7. **Role STAFF chưa có quyền**: `/admin/**` chỉ cho `ROLE_ADMIN`.
8. **Lọc tour theo giá**: repository hỗ trợ nhưng UI/service chưa nối.
9. **Khuyến mãi theo tour cụ thể**: hiện chỉ áp global theo code.
10. **Test nghiệp vụ**: mới có 1 context-load test.

---

## 9. Lưu ý bảo mật

Các file sau **KHÔNG** được commit lên Git:

- `config/OAuthGG.json` — Google OAuth client secret
- `config/ConfigApplication.properties` — mail password, API keys
- `src/main/resources/ssl/tourbooking.local.key` — SSL private key
- `uploads/` — ảnh do người dùng upload

Hãy chắc chắn các đường dẫn này có trong `.gitignore` trước khi push.

---

## 10. License

Sử dụng nội bộ cho mục đích học tập / đồ án.

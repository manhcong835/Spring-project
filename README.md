# Tour Booking System

Website đặt tour du lịch được xây dựng bằng Spring Boot, Thymeleaf và MySQL.

## Công nghệ sử dụng

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

## Chức năng chính

- Đăng ký, đăng nhập và đăng nhập bằng Google
- Tìm kiếm, xem và đặt tour
- Quản lý lịch sử đặt tour và thanh toán
- Đánh giá tour, chat tư vấn
- Quản trị tour, đơn đặt, khách hàng và khuyến mãi

## Cách chạy

1. Cài đặt JDK 21 và MySQL 8.
2. Tạo database `tourbookingdb`.
3. Cập nhật thông tin kết nối database trong `application.properties`.
4. Chạy ứng dụng:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

Truy cập: [http://localhost:8080](http://localhost:8080)

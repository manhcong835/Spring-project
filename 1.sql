-- ============================================================
--  CSDL ỨNG DỤNG ĐẶT TOUR DU LỊCH
--  MySQL 8.0+  |  ENGINE: InnoDB  |  CHARSET: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS tour_booking
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE tour_booking;

-- ------------------------------------------------------------
-- 1. roles — Vai trò người dùng
-- ------------------------------------------------------------
CREATE TABLE roles (
    id          INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)     NOT NULL COMMENT 'admin | staff | customer',
    description VARCHAR(255)        NULL DEFAULT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_name (name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Vai trò phân quyền người dùng';

INSERT INTO roles (name, description) VALUES
    ('admin',    'Quản trị viên hệ thống'),
    ('staff',    'Nhân viên điều hành tour'),
    ('customer', 'Khách hàng đặt tour');

-- ------------------------------------------------------------
-- 2. users — Tài khoản người dùng
-- ------------------------------------------------------------
CREATE TABLE users (
    id            INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    role_id       INT UNSIGNED  NOT NULL DEFAULT 3 COMMENT 'FK → roles.id  (3 = customer)',
    full_name     VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL COMMENT 'bcrypt hash',
    phone         VARCHAR(20)       NULL DEFAULT NULL,
    avatar_url    VARCHAR(500)      NULL DEFAULT NULL,
    is_locked     TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '0 = hoạt động  1 = bị khóa',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    KEY idx_users_role (role_id),
    KEY idx_users_is_locked (is_locked),

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Tài khoản hệ thống — dùng chung cho mọi vai trò';

-- ------------------------------------------------------------
-- 3. tour_categories — Danh mục tour
-- ------------------------------------------------------------
CREATE TABLE tour_categories (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)  NOT NULL,
    slug        VARCHAR(120)  NOT NULL COMMENT 'URL-friendly, unique',
    description TEXT              NULL DEFAULT NULL,
    icon_url    VARCHAR(500)      NULL DEFAULT NULL,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_categories_slug (slug)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Phân loại tour: biển, núi, văn hóa, mạo hiểm...';

-- ------------------------------------------------------------
-- 4. destinations — Điểm đến
-- ------------------------------------------------------------
CREATE TABLE destinations (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    name        VARCHAR(150)  NOT NULL,
    slug        VARCHAR(170)  NOT NULL,
    country     VARCHAR(100)  NOT NULL DEFAULT 'Việt Nam',
    region      VARCHAR(100)      NULL DEFAULT NULL COMMENT 'Tỉnh / vùng',
    description TEXT              NULL DEFAULT NULL,
    cover_image VARCHAR(500)      NULL DEFAULT NULL,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_destinations_slug (slug),
    KEY idx_destinations_country (country)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Điểm đến du lịch';

-- ------------------------------------------------------------
-- 5. tours — Thông tin tour
-- ------------------------------------------------------------
CREATE TABLE tours (
    id               INT UNSIGNED         NOT NULL AUTO_INCREMENT,
    category_id      INT UNSIGNED         NOT NULL,
    destination_id   INT UNSIGNED             NULL DEFAULT NULL COMMENT 'Điểm đến chính',
    title            VARCHAR(255)         NOT NULL,
    slug             VARCHAR(280)         NOT NULL,
    description      TEXT                 NOT NULL,
    highlights       TEXT                     NULL DEFAULT NULL COMMENT 'Điểm nổi bật (JSON array)',
    price_adult      DECIMAL(12,2)        NOT NULL DEFAULT 0.00 COMMENT 'Giá niêm yết người lớn (VNĐ)',
    price_child      DECIMAL(12,2)        NOT NULL DEFAULT 0.00 COMMENT 'Giá niêm yết trẻ em',
    duration_days    TINYINT UNSIGNED     NOT NULL DEFAULT 1,
    duration_nights  TINYINT UNSIGNED     NOT NULL DEFAULT 0,
    max_capacity     SMALLINT UNSIGNED    NOT NULL DEFAULT 20,
    status           ENUM('draft','active','hidden','cancelled')
                                          NOT NULL DEFAULT 'draft',
    created_by       INT UNSIGNED             NULL DEFAULT NULL COMMENT 'FK → users.id',
    created_at       TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_tours_slug (slug),
    KEY idx_tours_category (category_id),
    KEY idx_tours_destination (destination_id),
    KEY idx_tours_status (status),
    KEY idx_tours_price_adult (price_adult),

    CONSTRAINT fk_tours_category
        FOREIGN KEY (category_id) REFERENCES tour_categories (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tours_destination
        FOREIGN KEY (destination_id) REFERENCES destinations (id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_tours_created_by
        FOREIGN KEY (created_by) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Thông tin gốc của tour';

-- ------------------------------------------------------------
-- 6. tour_images — Hình ảnh tour
-- ------------------------------------------------------------
CREATE TABLE tour_images (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    tour_id     INT UNSIGNED  NOT NULL,
    image_url   VARCHAR(500)  NOT NULL,
    alt_text    VARCHAR(200)      NULL DEFAULT NULL,
    is_cover    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '1 = ảnh đại diện',
    sort_order  TINYINT      NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    KEY idx_tour_images_tour (tour_id),
    KEY idx_tour_images_cover (tour_id, is_cover),

    CONSTRAINT fk_tour_images_tour
        FOREIGN KEY (tour_id) REFERENCES tours (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Hình ảnh minh hoạ cho tour';

-- ------------------------------------------------------------
-- 7. tour_itineraries — Lịch trình theo ngày
-- ------------------------------------------------------------
CREATE TABLE tour_itineraries (
    id            INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    tour_id       INT UNSIGNED  NOT NULL,
    day_number    TINYINT       NOT NULL DEFAULT 1 COMMENT 'Ngày thứ mấy trong tour',
    title         VARCHAR(200)  NOT NULL,
    description   TEXT          NOT NULL COMMENT 'Hoạt động chi tiết trong ngày',
    meals         VARCHAR(100)      NULL DEFAULT NULL COMMENT 'VD: Sáng, Trưa, Tối',
    accommodation VARCHAR(200)      NULL DEFAULT NULL COMMENT 'Nơi lưu trú đêm đó',

    PRIMARY KEY (id),
    KEY idx_itineraries_tour (tour_id),
    UNIQUE KEY uq_itineraries_tour_day (tour_id, day_number),

    CONSTRAINT fk_itineraries_tour
        FOREIGN KEY (tour_id) REFERENCES tours (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Lịch trình chi tiết từng ngày của tour';

-- ------------------------------------------------------------
-- 8. tour_departures — Lịch khởi hành
-- ------------------------------------------------------------
CREATE TABLE tour_departures (
    id               INT UNSIGNED         NOT NULL AUTO_INCREMENT,
    tour_id          INT UNSIGNED         NOT NULL,
    departure_date   DATE                 NOT NULL COMMENT 'Ngày khởi hành',
    return_date      DATE                 NOT NULL COMMENT 'Ngày về',
    available_slots  SMALLINT UNSIGNED    NOT NULL DEFAULT 20 COMMENT 'Số chỗ còn trống',
    price_adult      DECIMAL(12,2)        NOT NULL DEFAULT 0.00 COMMENT 'Giá chuyến này (override tours.price_adult)',
    price_child      DECIMAL(12,2)        NOT NULL DEFAULT 0.00,
    status           ENUM('open','full','cancelled','completed')
                                          NOT NULL DEFAULT 'open',
    note             VARCHAR(255)             NULL DEFAULT NULL,
    created_at       TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_departures_tour (tour_id),
    KEY idx_departures_date (departure_date),
    KEY idx_departures_status (status),

    CONSTRAINT fk_departures_tour
        FOREIGN KEY (tour_id) REFERENCES tours (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Lịch khởi hành cụ thể — một tour có nhiều chuyến';

-- ------------------------------------------------------------
-- 9. promotions — Khuyến mãi
-- ------------------------------------------------------------
CREATE TABLE promotions (
    id            INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    code          VARCHAR(50)     NOT NULL COMMENT 'Mã khuyến mãi, UNIQUE',
    name          VARCHAR(150)    NOT NULL COMMENT 'Tên chương trình',
    type          ENUM('percent','fixed')
                                  NOT NULL DEFAULT 'percent',
    value         DECIMAL(10,2)   NOT NULL DEFAULT 0.00 COMMENT '% hoặc số tiền VNĐ',
    min_order     DECIMAL(12,2)       NULL DEFAULT NULL COMMENT 'Giá trị đơn tối thiểu',
    max_discount  DECIMAL(12,2)       NULL DEFAULT NULL COMMENT 'Giảm tối đa (áp dụng cho type=percent)',
    max_uses      INT UNSIGNED        NULL DEFAULT NULL COMMENT 'NULL = không giới hạn',
    used_count    INT UNSIGNED    NOT NULL DEFAULT 0,
    start_date    DATETIME        NOT NULL,
    end_date      DATETIME        NOT NULL,
    is_active     TINYINT(1)     NOT NULL DEFAULT 1,
    created_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_promotions_code (code),
    KEY idx_promotions_active (is_active, start_date, end_date)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Mã khuyến mãi / giảm giá';

-- ------------------------------------------------------------
-- 10. tour_promotions — Áp dụng khuyến mãi cho tour (N-N)
-- ------------------------------------------------------------
CREATE TABLE tour_promotions (
    id           INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    tour_id      INT UNSIGNED  NOT NULL,
    promotion_id INT UNSIGNED  NOT NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_tour_promotion (tour_id, promotion_id),
    KEY idx_tp_promotion (promotion_id),

    CONSTRAINT fk_tp_tour
        FOREIGN KEY (tour_id) REFERENCES tours (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_tp_promotion
        FOREIGN KEY (promotion_id) REFERENCES promotions (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Junction table: khuyến mãi áp dụng cho tour cụ thể';

-- ------------------------------------------------------------
-- 11. bookings — Đơn đặt tour
-- ------------------------------------------------------------
CREATE TABLE bookings (
    id                INT UNSIGNED         NOT NULL AUTO_INCREMENT,
    user_id           INT UNSIGNED         NOT NULL,
    departure_id      INT UNSIGNED         NOT NULL,
    promotion_id      INT UNSIGNED             NULL DEFAULT NULL,
    num_adults        TINYINT UNSIGNED     NOT NULL DEFAULT 1,
    num_children      TINYINT UNSIGNED     NOT NULL DEFAULT 0,
    unit_price_adult  DECIMAL(12,2)        NOT NULL DEFAULT 0.00 COMMENT 'Snapshot giá lúc đặt',
    unit_price_child  DECIMAL(12,2)        NOT NULL DEFAULT 0.00,
    discount_amount   DECIMAL(12,2)        NOT NULL DEFAULT 0.00,
    total_price       DECIMAL(12,2)        NOT NULL DEFAULT 0.00 COMMENT 'Tổng sau giảm giá',
    status            ENUM('pending','confirmed','cancelled','completed')
                                           NOT NULL DEFAULT 'pending',
    note              TEXT                     NULL DEFAULT NULL COMMENT 'Ghi chú của khách',
    created_at        TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_bookings_user (user_id),
    KEY idx_bookings_departure (departure_id),
    KEY idx_bookings_promotion (promotion_id),
    KEY idx_bookings_status (status),
    KEY idx_bookings_created (created_at),

    CONSTRAINT fk_bookings_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_departure
        FOREIGN KEY (departure_id) REFERENCES tour_departures (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_promotion
        FOREIGN KEY (promotion_id) REFERENCES promotions (id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Đơn đặt tour của khách hàng';

-- ------------------------------------------------------------
-- 12. booking_travelers — Danh sách hành khách
-- ------------------------------------------------------------
CREATE TABLE booking_travelers (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    booking_id  INT UNSIGNED  NOT NULL,
    full_name   VARCHAR(100)  NOT NULL,
    dob         DATE              NULL DEFAULT NULL COMMENT 'Ngày sinh',
    gender      ENUM('male','female','other')
                                  NULL DEFAULT NULL,
    id_number   VARCHAR(20)       NULL DEFAULT NULL COMMENT 'CMND / Passport',
    type        ENUM('adult','child','infant')
                              NOT NULL DEFAULT 'adult',
    note        VARCHAR(255)      NULL DEFAULT NULL COMMENT 'Yêu cầu đặc biệt (ăn chay, wheelchair...)',

    PRIMARY KEY (id),
    KEY idx_travelers_booking (booking_id),

    CONSTRAINT fk_travelers_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Thông tin từng hành khách trong đoàn';

-- ------------------------------------------------------------
-- 13. payments — Thanh toán
-- ------------------------------------------------------------
CREATE TABLE payments (
    id              INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    booking_id      INT UNSIGNED    NOT NULL,
    amount          DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    method          ENUM('vnpay','momo','bank_transfer','cash')
                                    NOT NULL DEFAULT 'bank_transfer',
    status          ENUM('pending','success','failed','refunded')
                                    NOT NULL DEFAULT 'pending',
    transaction_ref VARCHAR(100)        NULL DEFAULT NULL COMMENT 'Mã GD từ cổng thanh toán',
    payment_url     TEXT                NULL DEFAULT NULL COMMENT 'URL redirect (VNPay / Momo)',
    paid_at         TIMESTAMP           NULL DEFAULT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_payments_transaction_ref (transaction_ref),
    KEY idx_payments_booking (booking_id),
    KEY idx_payments_status (status),

    CONSTRAINT fk_payments_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Giao dịch thanh toán — tách riêng để lưu nhiều lần thử';

-- ------------------------------------------------------------
-- 14. reviews — Đánh giá tour
-- ------------------------------------------------------------
CREATE TABLE reviews (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    user_id     INT UNSIGNED  NOT NULL,
    tour_id     INT UNSIGNED  NOT NULL,
    booking_id  INT UNSIGNED  NOT NULL COMMENT 'Xác minh đã đi tour',
    rating      TINYINT       NOT NULL DEFAULT 5 COMMENT '1 – 5 sao',
    title       VARCHAR(150)      NULL DEFAULT NULL,
    comment     TEXT              NULL DEFAULT NULL,
    is_visible  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT 'Admin có thể ẩn',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_reviews_booking (booking_id) COMMENT 'Mỗi booking chỉ review 1 lần',
    KEY idx_reviews_tour (tour_id),
    KEY idx_reviews_user (user_id),
    KEY idx_reviews_rating (rating),

    CONSTRAINT fk_reviews_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_tour
        FOREIGN KEY (tour_id) REFERENCES tours (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_reviews_booking
        FOREIGN KEY (booking_id) REFERENCES bookings (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='Đánh giá sau chuyến đi — chỉ được tạo khi booking completed';

-- ============================================================
--  TRIGGER: Giảm available_slots khi booking được confirmed
-- ============================================================
DELIMITER $$

CREATE TRIGGER trg_booking_confirmed_decrease_slots
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF NEW.status = 'confirmed' AND OLD.status != 'confirmed' THEN
        UPDATE tour_departures
        SET available_slots = available_slots - (NEW.num_adults + NEW.num_children)
        WHERE id = NEW.departure_id;
    END IF;
END$$

-- Trigger: Hoàn trả slots khi booking bị cancelled
CREATE TRIGGER trg_booking_cancelled_restore_slots
AFTER UPDATE ON bookings
FOR EACH ROW
BEGIN
    IF NEW.status = 'cancelled' AND OLD.status = 'confirmed' THEN
        UPDATE tour_departures
        SET available_slots = available_slots + (OLD.num_adults + OLD.num_children)
        WHERE id = OLD.departure_id;
    END IF;
END$$

-- Trigger: Tăng used_count khi promotion được dùng
CREATE TRIGGER trg_booking_promotion_count
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    IF NEW.promotion_id IS NOT NULL THEN
        UPDATE promotions
        SET used_count = used_count + 1
        WHERE id = NEW.promotion_id;
    END IF;
END$$

DELIMITER ;
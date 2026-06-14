-- MySQL dump 10.13  Distrib 9.4.0, for Win64 (x86_64)
--
-- Host: localhost    Database: tourbookingdb
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */
;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */
;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */
;
/*!50503 SET NAMES utf8mb4 */
;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */
;
/*!40103 SET TIME_ZONE='+00:00' */
;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */
;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */
;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */
;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */
;

--
-- Dumping data for table `booking_travelers`
--

LOCK TABLES `booking_travelers` WRITE;
/*!40000 ALTER TABLE `booking_travelers` DISABLE KEYS */
;
INSERT INTO
    `booking_travelers` (
        `id`,
        `booking_id`,
        `full_name`,
        `date_of_birth`,
        `gender`,
        `traveler_type`,
        `identity_number`,
        `nationality`,
        `note`
    )
VALUES (
        1,
        1,
        'Le Minh An',
        '1998-03-10',
        'MALE',
        'ADULT',
        '079098000001',
        'Viet Nam',
        'Group leader'
    ),
    (
        2,
        1,
        'Nguyen Hoai Nam',
        '1997-08-18',
        'MALE',
        'ADULT',
        '079097000002',
        'Viet Nam',
        NULL
    ),
    (
        3,
        1,
        'Le An Nhien',
        '2016-04-12',
        'FEMALE',
        'CHILD',
        NULL,
        'Viet Nam',
        'Travels with parents'
    ),
    (
        4,
        2,
        'Pham Thu Ha',
        '2000-11-05',
        'FEMALE',
        'ADULT',
        '025100000004',
        'Viet Nam',
        NULL
    ),
    (
        5,
        2,
        'Dang Minh Quan',
        '1999-02-01',
        'MALE',
        'ADULT',
        '025099000005',
        'Viet Nam',
        NULL
    ),
    (
        6,
        2,
        'Dang Khanh An',
        '2024-01-15',
        'FEMALE',
        'INFANT',
        NULL,
        'Viet Nam',
        'Infant under 2 years old'
    ),
    (
        7,
        3,
        'Hoang Gia Bao',
        '1992-09-22',
        'MALE',
        'ADULT',
        '001092000006',
        'Viet Nam',
        'Vegetarian meal'
    ),
    (
        8,
        3,
        'Vu Ngoc Mai',
        '1993-12-14',
        'FEMALE',
        'ADULT',
        '001093000007',
        'Viet Nam',
        NULL
    ),
    (
        9,
        3,
        'Hoang Minh Anh',
        '2014-05-21',
        'FEMALE',
        'CHILD',
        NULL,
        'Viet Nam',
        NULL
    ),
    (
        10,
        3,
        'Hoang Minh Khang',
        '2018-07-09',
        'MALE',
        'CHILD',
        NULL,
        'Viet Nam',
        NULL
    );
/*!40000 ALTER TABLE `booking_travelers` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */
;
INSERT INTO
    `bookings` (
        `id`,
        `booking_code`,
        `user_id`,
        `tour_departure_id`,
        `promotion_id`,
        `contact_name`,
        `contact_email`,
        `contact_phone`,
        `adult_count`,
        `child_count`,
        `infant_count`,
        `total_people`,
        `original_amount`,
        `discount_amount`,
        `final_amount`,
        `special_requests`,
        `booking_status`,
        `payment_status`,
        `created_at`,
        `updated_at`
    )
VALUES (
        1,
        'BK202605070001',
        9,
        1,
        1,
        'Le Minh An',
        'customer1.seed@tourbooking.local',
        '0988000003',
        2,
        1,
        0,
        3,
        15270000.00,
        1000000.00,
        14270000.00,
        'Prefer nearby rooms and non-smoking rooms.',
        'COMPLETED',
        'PAID',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        2,
        'BK202605070002',
        10,
        3,
        3,
        'Pham Thu Ha',
        'customer2.seed@tourbooking.local',
        '0988000004',
        2,
        0,
        1,
        3,
        8780000.00,
        300000.00,
        8480000.00,
        'Traveling with an infant, need seat support on bus.',
        'CONFIRMED',
        'PARTIAL',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        3,
        'BK202605070003',
        11,
        4,
        2,
        'Hoang Gia Bao',
        'customer3.seed@tourbooking.local',
        '0988000005',
        2,
        2,
        0,
        4,
        11560000.00,
        500000.00,
        11060000.00,
        'One vegetarian meal.',
        'COMPLETED',
        'PAID',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        8,
        'BK-07B67C99',
        5,
        3,
        NULL,
        'Administrator',
        'admin@tourbooking.com',
        '0398679042',
        5,
        1,
        0,
        6,
        22940000.00,
        0.00,
        22940000.00,
        '',
        'PENDING',
        'UNPAID',
        '2026-05-25 09:39:27',
        '2026-05-25 09:39:27'
    ),
    (
        9,
        'BK-74961573',
        5,
        1,
        NULL,
        'Administrator',
        'admin@tourbooking.com',
        '0398679042',
        5,
        3,
        0,
        8,
        40320000.00,
        0.00,
        40320000.00,
        '',
        'PENDING',
        'PAID',
        '2026-05-25 10:02:25',
        '2026-05-25 10:20:31'
    ),
    (
        10,
        'BK-4A112A1E',
        22,
        1,
        NULL,
        'Đoàn Phương Anh',
        'doanphuonganh2607@gmail.com',
        '0889280621',
        2,
        0,
        0,
        2,
        10980000.00,
        0.00,
        10980000.00,
        'Lý do hủy: Thay đổi kế hoạch',
        'CANCELLED',
        'UNPAID',
        '2026-06-02 00:55:30',
        '2026-06-02 00:55:48'
    ),
    (
        11,
        'BK-20FB27DD',
        22,
        5,
        NULL,
        'Đoàn Phương Anh',
        'doanphuonganh2607@gmail.com',
        '0889280621',
        2,
        0,
        0,
        2,
        13980000.00,
        0.00,
        13980000.00,
        '',
        'PENDING',
        'PAID',
        '2026-06-02 00:56:30',
        '2026-06-02 00:56:55'
    ),
    (
        12,
        'BK-41B856DC',
        22,
        1,
        NULL,
        'Đoàn Phương Anh',
        'doanphuonganh2607@gmail.com',
        '0889280621',
        1,
        1,
        0,
        2,
        9780000.00,
        0.00,
        9780000.00,
        '',
        'PENDING',
        'UNPAID',
        '2026-06-02 00:58:43',
        '2026-06-02 00:58:43'
    ),
    (
        13,
        'BK-EE9C7098',
        1,
        1,
        NULL,
        'Công Mạnh Nguyễn',
        'congmanh835@gmail.com',
        '0',
        2,
        0,
        0,
        2,
        10980000.00,
        0.00,
        10980000.00,
        'Lý do hủy: Thay đổi kế hoạch',
        'CANCELLED',
        'UNPAID',
        '2026-06-02 00:58:51',
        '2026-06-02 00:59:54'
    );
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `destinations`
--

LOCK TABLES `destinations` WRITE;
/*!40000 ALTER TABLE `destinations` DISABLE KEYS */
;
INSERT INTO
    `destinations` (
        `id`,
        `name`,
        `province`,
        `country`,
        `description`,
        `image_url`,
        `status`
    )
VALUES (
        1,
        'Da Nang - Hoi An',
        'Da Nang',
        'Viet Nam',
        'Modern coastal city connected with the heritage town of Hoi An.',
        'https://images.unsplash.com/photo-1559592413-7cec4d0cae2b',
        'ACTIVE'
    ),
    (
        2,
        'Da Lat',
        'Lam Dong',
        'Viet Nam',
        'Cool highland city known for pine forests, flowers and waterfalls.',
        'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee',
        'ACTIVE'
    ),
    (
        3,
        'Ha Long',
        'Quang Ninh',
        'Viet Nam',
        'World-famous bay with limestone islands and overnight cruises.',
        'https://images.unsplash.com/photo-1528127269322-539801943592',
        'ACTIVE'
    ),
    (
        4,
        'Phu Quoc',
        'Kien Giang',
        'Viet Nam',
        'Island destination with beaches, resorts and seafood experiences.',
        'https://images.unsplash.com/photo-1507525428034-b723cf961d3e',
        'ACTIVE'
    ),
    (
        5,
        'Sa Pa',
        'Lao Cai',
        'Viet Nam',
        'Mountain town with terraced rice fields and ethnic culture.',
        'https://images.unsplash.com/photo-1501785888041-af3ef285b470',
        'ACTIVE'
    );
/*!40000 ALTER TABLE `destinations` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */
;
INSERT INTO
    `payments` (
        `id`,
        `booking_id`,
        `payment_code`,
        `amount`,
        `payment_method`,
        `payment_type`,
        `transaction_ref`,
        `paid_at`,
        `status`,
        `note`,
        `created_at`
    )
VALUES (
        1,
        1,
        'PAY202605070001',
        14270000.00,
        'BANK_TRANSFER',
        'FULL',
        'VCB202605070001',
        '2026-05-07 10:30:00',
        'SUCCESS',
        'Paid in full by bank transfer',
        '2026-05-07 23:22:33'
    ),
    (
        2,
        2,
        'PAY202605070002',
        3000000.00,
        'MOMO',
        'DEPOSIT',
        'MOMO202605070002',
        '2026-05-07 11:05:00',
        'SUCCESS',
        'Deposit paid, remaining balance pending',
        '2026-05-07 23:22:33'
    ),
    (
        3,
        3,
        'PAY202605070003',
        11060000.00,
        'VNPAY',
        'FULL',
        'VNPAY202605070003',
        '2026-05-07 14:15:00',
        'SUCCESS',
        'Paid in full by VNPAY',
        '2026-05-07 23:22:33'
    ),
    (
        8,
        9,
        'PM-A83157D5',
        40320000.00,
        'CASH',
        'FULL',
        NULL,
        '2026-05-25 10:20:30',
        'SUCCESS',
        '',
        '2026-05-25 10:20:30'
    ),
    (
        9,
        11,
        'PM-06BAB4AE',
        13980000.00,
        'BANK_TRANSFER',
        'FULL',
        NULL,
        '2026-06-02 00:56:55',
        'SUCCESS',
        '',
        '2026-06-02 00:56:55'
    );
/*!40000 ALTER TABLE `payments` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */
;
INSERT INTO
    `promotions` (
        `id`,
        `code`,
        `name`,
        `description`,
        `discount_type`,
        `discount_value`,
        `max_discount_amount`,
        `min_booking_amount`,
        `start_date`,
        `end_date`,
        `usage_limit`,
        `used_count`,
        `status`,
        `created_at`,
        `updated_at`
    )
VALUES (
        1,
        'SUMMER2026',
        'Summer 2026',
        'Discount 10 percent for summer tours.',
        'PERCENT',
        10.00,
        1000000.00,
        3000000.00,
        '2026-05-01 00:00:00',
        '2026-08-31 23:59:59',
        200,
        1,
        'ACTIVE',
        '2026-05-07 15:48:20',
        '2026-05-07 23:22:33'
    ),
    (
        2,
        'FAMILY500K',
        'Family discount',
        'Fixed discount for family bookings from 8,000,000 VND.',
        'FIXED',
        500000.00,
        500000.00,
        8000000.00,
        '2026-05-01 00:00:00',
        '2026-12-31 23:59:59',
        100,
        1,
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        3,
        'NEWCUS300K',
        'New customer',
        'Fixed discount for new customers.',
        'FIXED',
        300000.00,
        300000.00,
        3000000.00,
        '2026-01-01 00:00:00',
        '2026-12-31 23:59:59',
        500,
        1,
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    );
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */
;
INSERT INTO
    `reviews` (
        `id`,
        `booking_id`,
        `user_id`,
        `tour_id`,
        `rating`,
        `title`,
        `content`,
        `status`,
        `created_at`,
        `updated_at`
    )
VALUES (
        1,
        1,
        9,
        1,
        5,
        'Worth the price',
        'Good schedule, helpful guide and clean hotel.',
        'VISIBLE',
        '2026-05-07 23:22:33',
        '2026-06-03 20:49:14'
    ),
    (
        2,
        3,
        11,
        3,
        4,
        'Beautiful scenery',
        'Ha Long Bay was beautiful and the service was reliable.',
        'VISIBLE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    );
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */
;
INSERT INTO
    `roles` (`id`, `name`, `description`)
VALUES (
        1,
        'ADMIN',
        'System administrator'
    ),
    (
        2,
        'STAFF',
        'Tour booking staff'
    ),
    (
        3,
        'CUSTOMER',
        'Customer account'
    );
/*!40000 ALTER TABLE `roles` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `tour_categories`
--

LOCK TABLES `tour_categories` WRITE;
/*!40000 ALTER TABLE `tour_categories` DISABLE KEYS */
;
INSERT INTO
    `tour_categories` (
        `id`,
        `name`,
        `description`,
        `status`
    )
VALUES (
        1,
        'Du lịch biển',
        'Beach and island tours',
        'ACTIVE'
    ),
    (
        2,
        'Du lịch núi',
        'Mountain and highland tours',
        'ACTIVE'
    ),
    (
        3,
        'Du lịch văn hóa',
        'Culture, history and heritage tours',
        'ACTIVE'
    ),
    (
        4,
        'Nghỉ dưỡng',
        'Premium resort and leisure tours',
        'ACTIVE'
    );
/*!40000 ALTER TABLE `tour_categories` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `tour_departures`
--

LOCK TABLES `tour_departures` WRITE;
/*!40000 ALTER TABLE `tour_departures` DISABLE KEYS */
;
INSERT INTO
    `tour_departures` (
        `id`,
        `tour_id`,
        `departure_date`,
        `return_date`,
        `capacity`,
        `available_slots`,
        `adult_price`,
        `child_price`,
        `infant_price`,
        `status`,
        `created_at`,
        `updated_at`
    )
VALUES (
        1,
        1,
        '2026-06-10',
        '2026-06-12',
        35,
        22,
        5490000.00,
        4290000.00,
        1200000.00,
        'OPEN',
        '2026-05-07 23:22:33',
        '2026-06-02 00:59:54'
    ),
    (
        2,
        1,
        '2026-07-05',
        '2026-07-07',
        35,
        35,
        5790000.00,
        4490000.00,
        1200000.00,
        'OPEN',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        3,
        2,
        '2026-06-20',
        '2026-06-23',
        40,
        31,
        3990000.00,
        2990000.00,
        800000.00,
        'OPEN',
        '2026-05-07 23:22:33',
        '2026-05-25 09:39:27'
    ),
    (
        4,
        3,
        '2026-06-15',
        '2026-06-16',
        30,
        26,
        3290000.00,
        2490000.00,
        600000.00,
        'OPEN',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        5,
        4,
        '2026-07-12',
        '2026-07-14',
        30,
        28,
        6990000.00,
        5290000.00,
        1500000.00,
        'OPEN',
        '2026-05-07 23:22:33',
        '2026-06-02 00:56:30'
    ),
    (
        6,
        5,
        '2026-08-08',
        '2026-08-10',
        28,
        28,
        4590000.00,
        3490000.00,
        900000.00,
        'OPEN',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    );
/*!40000 ALTER TABLE `tour_departures` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `tour_images`
--

LOCK TABLES `tour_images` WRITE;
/*!40000 ALTER TABLE `tour_images` DISABLE KEYS */
;
INSERT INTO
    `tour_images` (
        `id`,
        `tour_id`,
        `image_url`,
        `alt_text`,
        `is_thumbnail`,
        `sort_order`,
        `created_at`
    )
VALUES (
        1,
        1,
        'https://images.unsplash.com/photo-1559592413-7cec4d0cae2b',
        'Da Nang thumbnail',
        1,
        1,
        '2026-05-07 23:22:33'
    ),
    (
        2,
        1,
        'https://images.unsplash.com/photo-1548013146-72479768bada',
        'Hoi An evening',
        0,
        2,
        '2026-05-07 23:22:33'
    ),
    (
        3,
        2,
        'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee',
        'Da Lat highland',
        1,
        1,
        '2026-05-07 23:22:33'
    ),
    (
        4,
        3,
        'https://images.unsplash.com/photo-1528127269322-539801943592',
        'Ha Long Bay cruise',
        1,
        1,
        '2026-05-07 23:22:33'
    ),
    (
        5,
        4,
        'https://images.unsplash.com/photo-1507525428034-b723cf961d3e',
        'Phu Quoc beach',
        1,
        1,
        '2026-05-07 23:22:33'
    ),
    (
        6,
        5,
        'https://images.unsplash.com/photo-1501785888041-af3ef285b470',
        'Sa Pa mountain',
        1,
        1,
        '2026-05-07 23:22:33'
    );
/*!40000 ALTER TABLE `tour_images` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `tour_itineraries`
--

LOCK TABLES `tour_itineraries` WRITE;
/*!40000 ALTER TABLE `tour_itineraries` DISABLE KEYS */
;
INSERT INTO
    `tour_itineraries` (
        `id`,
        `tour_id`,
        `day_number`,
        `title`,
        `description`,
        `meals`,
        `accommodation`,
        `sort_order`
    )
VALUES (
        1,
        1,
        1,
        'Ho Chi Minh City - Da Nang - Ba Na Hills',
        'Fly to Da Nang and visit Ba Na Hills, Golden Bridge and French village.',
        'Lunch, dinner',
        'Da Nang 3-star hotel',
        1
    ),
    (
        2,
        1,
        2,
        'My Khe Beach - Hoi An',
        'Free time at My Khe beach, then visit Hoi An ancient town in the evening.',
        'Breakfast, lunch, dinner',
        'Da Nang 3-star hotel',
        2
    ),
    (
        3,
        1,
        3,
        'Da Nang - Ho Chi Minh City',
        'Shop for local specialties and fly back to Ho Chi Minh City.',
        'Breakfast',
        NULL,
        3
    ),
    (
        4,
        2,
        1,
        'Ho Chi Minh City - Da Lat',
        'Travel to Da Lat, visit Lam Vien square and night market.',
        'Lunch, dinner',
        'Da Lat 3-star hotel',
        1
    ),
    (
        5,
        2,
        2,
        'Langbiang - City Flower Garden',
        'Visit Langbiang, city flower garden and Domaine de Marie church.',
        'Breakfast, lunch, dinner',
        'Da Lat 3-star hotel',
        2
    ),
    (
        6,
        3,
        1,
        'Ha Noi - Ha Long',
        'Depart from Ha Noi, board the cruise and explore Ha Long Bay.',
        'Lunch, dinner',
        '4-star cruise or hotel',
        1
    ),
    (
        7,
        3,
        2,
        'Sung Sot Cave - Ha Noi',
        'Visit Sung Sot cave, have lunch and return to Ha Noi.',
        'Breakfast, lunch',
        NULL,
        2
    ),
    (
        8,
        4,
        1,
        'Ho Chi Minh City - Phu Quoc',
        'Fly to Phu Quoc, check in at the resort and relax on the beach.',
        'Lunch, dinner',
        'Phu Quoc 4-star resort',
        1
    ),
    (
        9,
        4,
        2,
        'Bai Sao - Grand World',
        'Visit Bai Sao beach, Grand World and enjoy local seafood.',
        'Breakfast, lunch, dinner',
        'Phu Quoc 4-star resort',
        2
    ),
    (
        10,
        5,
        1,
        'Ha Noi - Sa Pa - Cat Cat Village',
        'Travel to Sa Pa and visit Cat Cat village in the afternoon.',
        'Lunch, dinner',
        'Sa Pa 3-star hotel',
        1
    ),
    (
        11,
        5,
        2,
        'Fansipan Peak',
        'Take the cable car to Fansipan and enjoy the mountain view.',
        'Breakfast, lunch, dinner',
        'Sa Pa 3-star hotel',
        2
    );
/*!40000 ALTER TABLE `tour_itineraries` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `tour_promotions`
--

LOCK TABLES `tour_promotions` WRITE;
/*!40000 ALTER TABLE `tour_promotions` DISABLE KEYS */
;
INSERT INTO
    `tour_promotions` (
        `id`,
        `tour_id`,
        `promotion_id`
    )
VALUES (1, 1, 1),
    (2, 2, 3),
    (3, 3, 2),
    (4, 4, 1),
    (5, 4, 2),
    (6, 5, 3);
/*!40000 ALTER TABLE `tour_promotions` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `tours`
--

LOCK TABLES `tours` WRITE;
/*!40000 ALTER TABLE `tours` DISABLE KEYS */
;
INSERT INTO
    `tours` (
        `id`,
        `category_id`,
        `destination_id`,
        `code`,
        `name`,
        `slug`,
        `departure_location`,
        `duration_days`,
        `duration_nights`,
        `transport`,
        `hotel_standard`,
        `description`,
        `policy`,
        `included_services`,
        `excluded_services`,
        `notes`,
        `status`,
        `created_at`,
        `updated_at`
    )
VALUES (
        1,
        1,
        1,
        'TOUR-DNHA-3N2D',
        'Da Nang - Hoi An 3 ngay 2 dem',
        'da-nang-hoi-an-3-ngay-2-dem',
        'Ho Chi Minh City',
        3,
        2,
        'Flight, bus',
        '3 sao',
        'Visit Ba Na Hills, My Khe beach and Hoi An ancient town.',
        'Cancel before 7 days: refund 70 percent.',
        'Round-trip flight, hotel, transfer bus, guide, scheduled meals.',
        'Personal expenses, VAT, single room surcharge.',
        'Bring personal identification documents.',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        2,
        2,
        2,
        'TOUR-DL-4N3D',
        'Da Lat mong mo 4 ngay 3 dem',
        'da-lat-mong-mo-4-ngay-3-dem',
        'Ho Chi Minh City',
        4,
        3,
        'Bus',
        '3 sao',
        'Explore Datanla waterfall, Langbiang, city flower garden and night market.',
        'Cancel before 5 days: refund 60 percent.',
        'Tour bus, hotel, guide, entrance tickets, scheduled meals.',
        'Personal expenses and drinks outside the program.',
        'Prepare a light jacket because the weather is cool.',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        3,
        3,
        3,
        'TOUR-HL-2N1D',
        'Ha Long 2 ngay 1 dem',
        'ha-long-2-ngay-1-dem',
        'Ha Noi',
        2,
        1,
        'Bus, cruise',
        '4 sao',
        'Cruise Ha Long Bay, visit Sung Sot cave and fishing village.',
        'Cancel before 3 days: refund 50 percent.',
        'Transfer bus, cruise, hotel or cabin, entrance tickets, meals.',
        'Personal expenses and optional kayak service.',
        'Schedule can change depending on weather.',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        4,
        4,
        4,
        'TOUR-PQ-3N2D',
        'Phu Quoc nghi duong 3 ngay 2 dem',
        'phu-quoc-nghi-duong-3-ngay-2-dem',
        'Ho Chi Minh City',
        3,
        2,
        'Flight, bus',
        '4 sao',
        'Relax at Phu Quoc island, visit Grand World and Bai Sao beach.',
        'Cancel before 10 days: refund 80 percent.',
        'Flight, resort, transfer, breakfast, guide.',
        'Personal expenses and optional fishing or snorkeling tours.',
        'Bring sunscreen and swimwear.',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    ),
    (
        5,
        2,
        5,
        'TOUR-SP-3N2D',
        'Sa Pa Fansipan 3 ngay 2 dem',
        'sa-pa-fansipan-3-ngay-2-dem',
        'Ha Noi',
        3,
        2,
        'Bus, cable car',
        '3 sao',
        'Visit Fansipan, Cat Cat village and terraced rice fields.',
        'Cancel before 7 days: refund 70 percent.',
        'Bus, hotel, guide, entrance tickets, scheduled meals.',
        'Personal expenses and optional ethnic costume rental.',
        'Bring comfortable shoes and warm clothes.',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33'
    );
/*!40000 ALTER TABLE `tours` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `user_auth_providers`
--

LOCK TABLES `user_auth_providers` WRITE;
/*!40000 ALTER TABLE `user_auth_providers` DISABLE KEYS */
;
INSERT INTO
    `user_auth_providers` (
        `id`,
        `user_id`,
        `provider`,
        `provider_user_id`,
        `password`,
        `email_verified`,
        `provider_email`,
        `created_at`,
        `updated_at`
    )
VALUES (
        1,
        1,
        'GOOGLE',
        '108379719654495251183',
        NULL,
        1,
        'congmanh835@gmail.com',
        '2026-04-17 17:23:38',
        '2026-04-17 17:23:38'
    ),
    (
        2,
        2,
        'GOOGLE',
        '110792735596661953862',
        NULL,
        1,
        'manhc2868@gmail.com',
        '2026-04-17 17:24:00',
        '2026-04-17 17:24:00'
    ),
    (
        3,
        3,
        'LOCAL',
        NULL,
        '$2a$10$7nSFQ/M9LqIonne.5Ojc5OV5wP7KnfPWAuf60l3Zc.xwwaoal69YS',
        0,
        'nguyencongmanh08032005@gmail.com',
        '2026-04-17 19:41:06',
        '2026-04-17 19:41:06'
    ),
    (
        4,
        4,
        'LOCAL',
        NULL,
        '$2a$10$HQed480SjOAkPzvPgUAD8e.fHTA6Npz.K1rx699BgmKQfOLYGiqse',
        0,
        'manhcong835@gmail.com',
        '2026-04-17 20:08:00',
        '2026-04-17 20:08:00'
    ),
    (
        5,
        5,
        'LOCAL',
        NULL,
        '$2a$10$9hpHxsjph5Q2qlhUt5PpjuLg5OC1IjPaJkaV9qAUaaYxoRCUVcgL.',
        1,
        'admin@tourbooking.com',
        '2026-04-24 19:57:54',
        '2026-04-24 19:57:54'
    ),
    (
        6,
        6,
        'LOCAL',
        NULL,
        '$2a$10$HKKZsUq8Zslu5SDfX4.vWe70Yn5yX/eV90qex6brTHMLm4CyQK6Ja',
        0,
        '123abc@gmail.com',
        '2026-05-07 00:18:48',
        '2026-05-07 00:18:48'
    ),
    (
        7,
        7,
        'LOCAL',
        NULL,
        '$2a$10$mBGIufX40EOaFvSAkr5gwuSQ9VESbJ10Y8hqZuXQ.SlDypdGQmMCC',
        1,
        'admin.seed@tourbooking.local',
        '2026-05-07 23:22:33',
        '2026-05-07 23:25:42'
    ),
    (
        8,
        8,
        'LOCAL',
        NULL,
        '$2a$10$BB89lq3/MZh37e8YwIzVi.CvDZDxT/5wKuOKZg5ItU/onXqCBI6Ou',
        1,
        'staff.seed@tourbooking.local',
        '2026-05-07 23:22:33',
        '2026-05-07 23:25:42'
    ),
    (
        9,
        9,
        'LOCAL',
        NULL,
        '$2a$10$q8rIRVX4.VZVuKXIYz7K2eFSujRBmbPFZrW1wDDTDLm5NKoBdM2Ai',
        1,
        'customer1.seed@tourbooking.local',
        '2026-05-07 23:22:33',
        '2026-05-07 23:25:42'
    ),
    (
        10,
        10,
        'LOCAL',
        NULL,
        '$2a$10$q8rIRVX4.VZVuKXIYz7K2eFSujRBmbPFZrW1wDDTDLm5NKoBdM2Ai',
        1,
        'customer2.seed@tourbooking.local',
        '2026-05-07 23:22:33',
        '2026-05-07 23:25:42'
    ),
    (
        11,
        11,
        'LOCAL',
        NULL,
        '$2a$10$q8rIRVX4.VZVuKXIYz7K2eFSujRBmbPFZrW1wDDTDLm5NKoBdM2Ai',
        1,
        'customer3.seed@tourbooking.local',
        '2026-05-07 23:22:33',
        '2026-05-07 23:25:42'
    ),
    (
        21,
        21,
        'LOCAL',
        NULL,
        '$2a$10$J2kYij8aJK965IGNOC7MfOAwR/IAqePUVfL9HPMNbTPB2E66bmbu2',
        1,
        'congmanhnguyen835@gmail.com',
        '2026-05-27 14:52:04',
        '2026-05-31 00:07:22'
    ),
    (
        22,
        22,
        'LOCAL',
        NULL,
        '$2a$10$NRDuooiQo7UPYDIs3wCvWOCxbfkilU1nvQU1aKsYAxzd5wvw7y5Ru',
        1,
        'doanphuonganh2607@gmail.com',
        '2026-06-02 00:35:56',
        '2026-06-02 00:35:56'
    ),
    (
        23,
        22,
        'GOOGLE',
        '118264255128790530912',
        NULL,
        1,
        'doanphuonganh2607@gmail.com',
        '2026-06-02 00:36:24',
        '2026-06-02 00:36:24'
    );
/*!40000 ALTER TABLE `user_auth_providers` ENABLE KEYS */
;
UNLOCK TABLES;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */
;
INSERT INTO
    `users` (
        `id`,
        `role_id`,
        `full_name`,
        `email`,
        `phone`,
        `gender`,
        `date_of_birth`,
        `address`,
        `avatar_url`,
        `status`,
        `created_at`,
        `updated_at`,
        `last_password_reset_at`
    )
VALUES (
        1,
        1,
        'Công Mạnh Nguyễn',
        'congmanh835@gmail.com',
        '',
        'Nam',
        '2005-03-08',
        '',
        'https://lh3.googleusercontent.com/a/ACg8ocKPHX1_Ql_nq8UcJ-5OsAwdQcZd6QVcwPlWxYIySyl4089k_WhL=s96-c',
        'ACTIVE',
        '2026-04-17 17:23:38',
        '2026-04-23 13:30:07',
        NULL
    ),
    (
        2,
        3,
        'Mạnh Công',
        'manhc2868@gmail.com',
        NULL,
        NULL,
        NULL,
        NULL,
        'https://lh3.googleusercontent.com/a/ACg8ocKwffy3cPvf-R5c6e6yrYgHkgcCu5kdG9EJOOcsURLHXwu8Mw=s96-c',
        'ACTIVE',
        '2026-04-17 17:24:00',
        '2026-05-07 15:50:32',
        NULL
    ),
    (
        3,
        1,
        'Công Mạnh',
        'nguyencongmanh08032005@gmail.com',
        '0398679042',
        'Nam',
        '2005-03-08',
        '',
        NULL,
        'ACTIVE',
        '2026-04-17 19:41:06',
        '2026-04-23 13:27:43',
        NULL
    ),
    (
        4,
        1,
        'aaaaaaaaa',
        'manhcong835@gmail.com',
        '0398679043',
        NULL,
        NULL,
        NULL,
        NULL,
        'ACTIVE',
        '2026-04-17 20:08:00',
        '2026-05-22 10:11:10',
        NULL
    ),
    (
        5,
        1,
        'Administrator',
        'admin@tourbooking.com',
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        'ACTIVE',
        '2026-04-24 19:57:54',
        '2026-04-24 19:57:54',
        NULL
    ),
    (
        6,
        2,
        'Công Mạnh Nguyễn',
        '123abc@gmail.com',
        '0598679043',
        NULL,
        NULL,
        NULL,
        NULL,
        'ACTIVE',
        '2026-05-07 00:18:47',
        '2026-05-07 00:18:47',
        NULL
    ),
    (
        7,
        1,
        'Nguyen Van Admin',
        'admin.seed@tourbooking.local',
        '0988000001',
        'MALE',
        '1990-01-15',
        'District 1, Ho Chi Minh City',
        'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33',
        NULL
    ),
    (
        8,
        2,
        'Tran Thi Staff',
        'staff.seed@tourbooking.local',
        '0988000002',
        'FEMALE',
        '1995-06-20',
        'Cau Giay, Ha Noi',
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33',
        NULL
    ),
    (
        9,
        3,
        'Le Minh An',
        'customer1.seed@tourbooking.local',
        '0988000003',
        'MALE',
        '1998-03-10',
        'Hai Chau, Da Nang',
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33',
        NULL
    ),
    (
        10,
        3,
        'Pham Thu Ha',
        'customer2.seed@tourbooking.local',
        '0988000004',
        'FEMALE',
        '2000-11-05',
        'Nha Trang, Khanh Hoa',
        'https://images.unsplash.com/photo-1438761681033-6461ffad8d80',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33',
        NULL
    ),
    (
        11,
        3,
        'Hoang Gia Bao',
        'customer3.seed@tourbooking.local',
        '0988000005',
        'MALE',
        '1992-09-22',
        'Da Lat, Lam Dong',
        'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d',
        'ACTIVE',
        '2026-05-07 23:22:33',
        '2026-05-07 23:22:33',
        NULL
    ),
    (
        21,
        3,
        'Công Mạnh Nguyễn',
        'congmanhnguyen835@gmail.com',
        '0987654321',
        NULL,
        NULL,
        NULL,
        NULL,
        'ACTIVE',
        '2026-05-27 14:52:04',
        '2026-05-31 00:07:22',
        '2026-05-31 00:07:22'
    ),
    (
        22,
        3,
        'Đoàn Phương Anh',
        'doanphuonganh2607@gmail.com',
        '0889280621',
        NULL,
        NULL,
        NULL,
        NULL,
        'ACTIVE',
        '2026-06-02 00:35:56',
        '2026-06-02 00:35:56',
        NULL
    );
/*!40000 ALTER TABLE `users` ENABLE KEYS */
;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */
;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */
;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */
;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */
;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */
;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */
;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */
;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */
;

-- Dump completed on 2026-06-14  0:43:56
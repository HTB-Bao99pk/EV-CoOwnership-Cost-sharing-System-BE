-- ==========================================
-- SAMPLE DATA FOR EV CO-OWNERSHIP SYSTEM
-- ==========================================
-- Thứ tự insert: Users -> Vehicles -> Groups -> Members -> Schedules -> Votes -> VoteResponses -> GroupTransactions -> Notifications -> Payments

-- ==========================================
-- 1. USERS (10 users: 1 admin, 9 regular users)
-- ==========================================
-- Password mã hóa cho tất cả: "password123"
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke

INSERT INTO users (full_name, email, password_hash, cccd, driver_license, birthday, role, verification_status, created_at, location, cccd_front_url, cccd_back_url, driver_license_url) VALUES
(N'Nguyễn Văn Admin', 'admin@evcs.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001199001234', 'B2-001199', '1990-01-15', 'admin', 'verified', '2025-01-01 08:00:00', 'Hà Nội', '/uploads/cccd/admin_front.jpg', '/uploads/cccd/admin_back.jpg', '/uploads/license/admin_license.jpg'),
(N'Trần Thị Lan', 'lan.tran@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001295002345', 'B2-001295', '1995-03-20', 'user', 'verified', '2025-01-05 09:30:00', 'Hồ Chí Minh', '/uploads/cccd/lan_front.jpg', '/uploads/cccd/lan_back.jpg', '/uploads/license/lan_license.jpg'),
(N'Lê Văn Minh', 'minh.le@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001392003456', 'B2-001392', '1992-07-10', 'user', 'verified', '2025-01-08 10:15:00', 'Đà Nẵng', '/uploads/cccd/minh_front.jpg', '/uploads/cccd/minh_back.jpg', '/uploads/license/minh_license.jpg'),
(N'Phạm Thị Hương', 'huong.pham@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001493004567', 'B2-001493', '1993-11-25', 'user', 'verified', '2025-01-10 14:20:00', 'Hà Nội', '/uploads/cccd/huong_front.jpg', '/uploads/cccd/huong_back.jpg', '/uploads/license/huong_license.jpg'),
('Hoàng Văn Tuấn', 'tuan.hoang@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001594005678', 'B2-001594', '1994-05-15', 'user', 'verified', '2025-01-12 11:45:00', 'Hải Phòng', '/uploads/cccd/tuan_front.jpg', '/uploads/cccd/tuan_back.jpg', '/uploads/license/tuan_license.jpg'),
('Đỗ Thị Mai', 'mai.do@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001696006789', 'B2-001696', '1996-09-30', 'user', 'verified', '2025-01-15 16:00:00', 'Cần Thơ', '/uploads/cccd/mai_front.jpg', '/uploads/cccd/mai_back.jpg', '/uploads/license/mai_license.jpg'),
('Vũ Văn Nam', 'nam.vu@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001791007890', 'B2-001791', '1991-12-05', 'user', 'verified', '2025-01-18 09:00:00', 'Hồ Chí Minh', '/uploads/cccd/nam_front.jpg', '/uploads/cccd/nam_back.jpg', '/uploads/license/nam_license.jpg'),
('Bùi Thị Thảo', 'thao.bui@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001898008901', 'B2-001898', '1998-02-14', 'user', 'verified', '2025-01-20 13:30:00', 'Nha Trang', '/uploads/cccd/thao_front.jpg', '/uploads/cccd/thao_back.jpg', '/uploads/license/thao_license.jpg'),
('Ngô Văn Khoa', 'khoa.ngo@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '001997009012', 'B2-001997', '1997-06-22', 'user', 'verified', '2025-01-22 10:45:00', 'Huế', '/uploads/cccd/khoa_front.jpg', '/uploads/cccd/khoa_back.jpg', '/uploads/license/khoa_license.jpg'),
('Đinh Thị Linh', 'linh.dinh@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9qOkEk3LN2o2S9LzHvdJtN8xZke', '002095010123', 'B2-002095', '1995-08-18', 'user', 'pending', '2025-01-25 15:20:00', 'Hà Nội', '/uploads/cccd/linh_front.jpg', '/uploads/cccd/linh_back.jpg', '/uploads/license/linh_license.jpg');

-- ==========================================
-- 2. VEHICLES (5 vehicles: 3 approved, 1 pending, 1 rejected)
-- ==========================================
INSERT INTO vehicles (model, brand, license_plate, location, status, owner_id, registration_info, battery_capacity, year_of_manufacture, image_url_1, image_url_2, image_url_3, verification_status, verified_by, verified_at, reject_reason, created_at) VALUES
('VF 8', 'VinFast', '30A-12345', 'Hà Nội', 'available', 2, 'Đăng ký lần đầu: 2023-05-10, Hạn đến: 2025-05-10', 87, 2023, '/uploads/vehicles/vf8_1.jpg', '/uploads/vehicles/vf8_2.jpg', '/uploads/vehicles/vf8_3.jpg', 'approved', 1, '2025-01-06 10:00:00', NULL, '2025-01-05 09:30:00'),
('VF e34', 'VinFast', '51F-67890', 'Hồ Chí Minh', 'available', 3, 'Đăng ký lần đầu: 2024-01-15, Hạn đến: 2026-01-15', 42, 2024, '/uploads/vehicles/vfe34_1.jpg', '/uploads/vehicles/vfe34_2.jpg', '/uploads/vehicles/vfe34_3.jpg', 'approved', 1, '2025-01-09 11:30:00', NULL, '2025-01-08 10:15:00'),
('Model 3', 'Tesla', '43A-11111', 'Đà Nẵng', 'available', 5, 'Đăng ký lần đầu: 2022-08-20, Hạn đến: 2024-08-20', 75, 2022, '/uploads/vehicles/tesla3_1.jpg', '/uploads/vehicles/tesla3_2.jpg', '/uploads/vehicles/tesla3_3.jpg', 'approved', 1, '2025-01-13 09:45:00', NULL, '2025-01-12 11:45:00'),
('VF 9', 'VinFast', '92B-22222', 'Hải Phòng', 'pending_approval', 7, 'Đăng ký lần đầu: 2024-06-01, Hạn đến: 2026-06-01', 123, 2024, '/uploads/vehicles/vf9_1.jpg', '/uploads/vehicles/vf9_2.jpg', '/uploads/vehicles/vf9_3.jpg', 'pending', NULL, NULL, NULL, '2025-01-18 09:00:00'),
('Leaf', 'Nissan', '79C-33333', 'Cần Thơ', 'rejected', 6, 'Đăng ký lần đầu: 2020-03-10, Hạn đến: 2022-03-10', 40, 2020, '/uploads/vehicles/leaf_1.jpg', '/uploads/vehicles/leaf_2.jpg', '/uploads/vehicles/leaf_3.jpg', 'rejected', 1, '2025-01-16 14:00:00', 'Giấy tờ đăng ký đã hết hạn', '2025-01-15 16:00:00');

-- ==========================================
-- 3. GROUPS (3 groups: 1 approved locked, 1 recruiting, 1 pending)
-- ==========================================
INSERT INTO groups (vehicle_id, created_by, approved_by, name, description, status, estimated_value, created_at, approval_status, reject_reason, max_members, min_ownership_percentage, total_ownership_percentage, is_locked, contract_url, balance) VALUES
(1, 2, 1, 'Nhóm VF8 Hà Nội', 'Chia sẻ xe VF8 khu vực Hà Nội, phù hợp cho người đi làm', 'locked', 850000000, '2025-01-06 11:00:00', 'approved', NULL, 5, 15.00, 100.00, 1, '/uploads/contracts/group1_contract.pdf', 12500000.00),
(2, 3, 1, 'Nhóm VF e34 TP.HCM', 'Xe điện nhỏ gọn cho sinh viên và người mới đi làm', 'recruiting', 480000000, '2025-01-09 12:00:00', 'approved', NULL, 5, 15.00, 50.00, 0, NULL, 3200000.00),
(3, 5, NULL, 'Nhóm Tesla Model 3 Đà Nẵng', 'Chia sẻ Tesla Model 3 tại Đà Nẵng', 'pending', 1200000000, '2025-01-13 10:00:00', 'pending', NULL, 5, 15.00, 0.00, 0, NULL, 0.00);

-- ==========================================
-- 4. MEMBERS (Group 1: 5 members locked, Group 2: 3 members recruiting)
-- ==========================================
INSERT INTO members (group_id, user_id, ownership_percentage, join_status, join_date, reason, proposed_ownership_percentage, counter_offer_percentage, counter_offer_status) VALUES
-- Group 1 (VF8 Hà Nội) - LOCKED với 5 members
(1, 2, 40.00, 'approved', '2025-01-06 11:00:00', 'Tôi là chủ xe, muốn chia sẻ để tối ưu chi phí', 40.00, NULL, NULL),
(1, 4, 20.00, 'approved', '2025-01-07 09:00:00', 'Tôi cần xe đi làm hàng ngày, cam kết giữ gìn xe cẩn thận', 20.00, NULL, NULL),
(1, 5, 15.00, 'approved', '2025-01-08 14:30:00', 'Làm việc gần khu vực, muốn tham gia chia sẻ', 15.00, NULL, NULL),
(1, 7, 15.00, 'approved', '2025-01-09 10:15:00', 'Có nhu cầu sử dụng xe cuối tuần', 20.00, 15.00, 'accepted'),
(1, 9, 10.00, 'approved', '2025-01-10 16:45:00', 'Muốn tham gia với tỷ lệ nhỏ để trải nghiệm', 15.00, 10.00, 'accepted'),

-- Group 2 (VF e34 TP.HCM) - RECRUITING với 3 members
(2, 3, 30.00, 'approved', '2025-01-09 12:00:00', 'Tôi là chủ xe, muốn chia sẻ với sinh viên', 30.00, NULL, NULL),
(2, 6, 20.00, 'approved', '2025-01-11 08:30:00', 'Sinh viên cần xe đi học', 20.00, NULL, NULL),
(2, 8, 0.00, 'pending', '2025-01-23 13:20:00', 'Mới tốt nghiệp, muốn tham gia nhóm chia sẻ', 25.00, 18.00, 'pending_user_response'),

-- Group 3 (Tesla Model 3) - PENDING, chưa có member nào ngoài chủ xe
(3, 5, 0.00, 'pending', '2025-01-13 10:00:00', 'Tôi là chủ xe muốn tạo nhóm chia sẻ', 35.00, NULL, NULL);

-- ==========================================
-- 5. SCHEDULES (Bookings cho các members của Group 1 và 2)
-- ==========================================
INSERT INTO schedules (group_id, user_id, start_time, end_time, status, created_at, check_in_time, check_out_time, battery_level_before, battery_level_after, vehicle_condition, penalty_amount, notes) VALUES
-- Group 1 bookings (VF8)
(1, 2, '2025-01-15 08:00:00', '2025-01-15 18:00:00', 'completed', '2025-01-14 20:00:00', '2025-01-15 08:05:00', '2025-01-15 17:55:00', 95, 45, 'Xe sạch sẽ, không vấn đề', 0.00, 'Đi công tác Hải Phòng'),
(1, 4, '2025-01-16 07:00:00', '2025-01-16 17:30:00', 'completed', '2025-01-15 18:30:00', '2025-01-16 07:10:00', '2025-01-16 17:25:00', 85, 35, 'Xe hoạt động tốt', 0.00, 'Đi làm hàng ngày'),
(1, 5, '2025-01-17 09:00:00', '2025-01-17 12:00:00', 'completed', '2025-01-16 19:00:00', '2025-01-17 09:05:00', '2025-01-17 12:30:00', 90, 80, 'Không có vấn đề', 30000.00, 'Trả xe muộn 30 phút'),
(1, 7, '2025-01-20 06:00:00', '2025-01-20 20:00:00', 'completed', '2025-01-19 21:00:00', '2025-01-20 06:00:00', '2025-01-20 20:05:00', 100, 85, 'Xe sạch sẽ', 50000.00, 'Pin dưới 90% khi trả - phạt 50k'),
(1, 9, '2025-01-25 14:00:00', '2025-01-25 18:00:00', 'booked', '2025-01-24 10:00:00', NULL, NULL, NULL, NULL, NULL, 0.00, 'Đi mua sắm cuối tuần'),
(1, 2, '2025-01-27 08:00:00', '2025-01-27 12:00:00', 'booked', '2025-01-26 20:00:00', NULL, NULL, NULL, NULL, NULL, 0.00, 'Họp khách hàng'),

-- Group 2 bookings (VF e34)
(2, 3, '2025-01-18 07:00:00', '2025-01-18 17:00:00', 'completed', '2025-01-17 22:00:00', '2025-01-18 07:05:00', '2025-01-18 16:55:00', 98, 55, 'Xe tốt', 0.00, 'Đi công việc'),
(2, 6, '2025-01-21 08:00:00', '2025-01-21 12:00:00', 'completed', '2025-01-20 19:00:00', '2025-01-21 08:00:00', '2025-01-21 12:10:00', 92, 78, 'Không vấn đề', 10000.00, 'Trả xe muộn 10 phút'),
(2, 3, '2025-01-26 09:00:00', '2025-01-26 15:00:00', 'booked', '2025-01-25 21:00:00', NULL, NULL, NULL, NULL, NULL, 0.00, 'Đi dạy học'),
(2, 6, '2025-01-28 07:30:00', '2025-01-28 11:30:00', 'booked', '2025-01-27 18:00:00', NULL, NULL, NULL, NULL, NULL, 0.00, 'Đi học và thư viện');

-- ==========================================
-- 6. VOTES (2 votes: 1 open, 1 closed)
-- ==========================================
INSERT INTO votes (group_id, topic, description, created_at, status) VALUES
(1, 'Tăng phí bảo dưỡng định kỳ', 'Đề xuất tăng phí bảo dưỡng từ 500k/tháng lên 700k/tháng do chi phí thực tế cao hơn dự kiến', '2025-01-22 10:00:00', 'closed'),
(2, 'Quy định sử dụng xe vào ban đêm', 'Đề xuất cho phép sử dụng xe sau 22h với điều kiện thông báo trước 24h', '2025-01-24 14:30:00', 'open');

-- ==========================================
-- 7. VOTE_RESPONSES (Responses cho vote đã đóng)
-- ==========================================
INSERT INTO vote_responses (vote_id, user_id, response, voted_at) VALUES
(1, 2, 'YES', '2025-01-22 11:00:00'),
(1, 4, 'YES', '2025-01-22 13:30:00'),
(1, 5, 'NO', '2025-01-22 15:45:00'),
(1, 7, 'YES', '2025-01-23 09:00:00'),
(1, 9, 'YES', '2025-01-23 16:20:00');

-- ==========================================
-- 8. GROUP_TRANSACTIONS (Financial transactions)
-- ==========================================
INSERT INTO group_transactions (group_id, user_id, transaction_type, amount, description, created_at, status) VALUES
-- Group 1 transactions
(1, 2, 'deposit', 3400000.00, 'Đóng vốn ban đầu 40%', '2025-01-06 12:00:00', 'completed'),
(1, 4, 'deposit', 1700000.00, 'Đóng vốn ban đầu 20%', '2025-01-07 10:00:00', 'completed'),
(1, 5, 'deposit', 1275000.00, 'Đóng vốn ban đầu 15%', '2025-01-08 15:00:00', 'completed'),
(1, 7, 'deposit', 1275000.00, 'Đóng vốn ban đầu 15%', '2025-01-09 11:00:00', 'completed'),
(1, 9, 'deposit', 850000.00, 'Đóng vốn ban đầu 10%', '2025-01-10 17:00:00', 'completed'),
(1, NULL, 'maintenance', -500000.00, 'Bảo dưỡng định kỳ tháng 1', '2025-01-15 14:00:00', 'completed'),
(1, NULL, 'insurance', -2800000.00, 'Phí bảo hiểm quý 1/2025', '2025-01-20 10:00:00', 'completed'),
(1, 5, 'penalty', 30000.00, 'Phạt trả xe muộn 30 phút', '2025-01-17 12:35:00', 'completed'),
(1, 7, 'penalty', 50000.00, 'Phạt pin dưới 90%', '2025-01-20 20:10:00', 'completed'),

-- Group 2 transactions
(2, 3, 'deposit', 1440000.00, 'Đóng vốn ban đầu 30%', '2025-01-09 13:00:00', 'completed'),
(2, 6, 'deposit', 960000.00, 'Đóng vốn ban đầu 20%', '2025-01-11 09:00:00', 'completed'),
(2, NULL, 'maintenance', -300000.00, 'Bảo dưỡng định kỳ tháng 1', '2025-01-18 15:00:00', 'completed'),
(2, 6, 'penalty', 10000.00, 'Phạt trả xe muộn 10 phút', '2025-01-21 12:15:00', 'completed');

-- ==========================================
-- 9. NOTIFICATIONS (System notifications)
-- ==========================================
INSERT INTO notifications (user_id, title, message, type, is_read, created_at, related_entity_type, related_entity_id) VALUES
-- Notifications cho user 2 (owner của group 1)
(2, 'Nhóm đã được duyệt', 'Nhóm "Nhóm VF8 Hà Nội" của bạn đã được admin phê duyệt!', 'group_approval', 1, '2025-01-06 10:05:00', 'Group', 1),
(2, 'Thành viên mới tham gia', 'Trần Thị Hương đã tham gia nhóm của bạn', 'member_join', 1, '2025-01-07 09:05:00', 'Member', 2),
(2, 'Biểu quyết mới', 'Có biểu quyết mới: "Tăng phí bảo dưỡng định kỳ"', 'vote_new', 1, '2025-01-22 10:05:00', 'Vote', 1),
(2, 'Đặt lịch thành công', 'Bạn đã đặt lịch sử dụng xe từ 08:00 đến 12:00 ngày 27/01', 'schedule_booked', 0, '2025-01-26 20:05:00', 'Schedule', 6),

-- Notifications cho user 4
(4, 'Yêu cầu tham gia được chấp nhận', 'Yêu cầu tham gia "Nhóm VF8 Hà Nội" đã được phê duyệt!', 'member_approved', 1, '2025-01-07 09:10:00', 'Member', 2),
(4, 'Nhắc nhở sử dụng xe', 'Bạn có lịch sử dụng xe vào 07:00 ngày mai', 'schedule_reminder', 1, '2025-01-15 20:00:00', 'Schedule', 2),
(4, 'Biểu quyết mới', 'Có biểu quyết mới: "Tăng phí bảo dưỡng định kỳ"', 'vote_new', 1, '2025-01-22 10:05:00', 'Vote', 1),

-- Notifications cho user 5
(5, 'Yêu cầu tham gia được chấp nhận', 'Yêu cầu tham gia "Nhóm VF8 Hà Nội" đã được phê duyệt!', 'member_approved', 1, '2025-01-08 14:35:00', 'Member', 3),
(5, 'Phạt vi phạm', 'Bạn bị phạt 30,000 VND do trả xe muộn 30 phút', 'penalty', 1, '2025-01-17 12:35:00', 'Schedule', 3),

-- Notifications cho user 7
(7, 'Đề xuất lại phần trăm sở hữu', 'Chủ nhóm đề xuất phần trăm sở hữu 15% thay vì 20%', 'counter_offer', 1, '2025-01-09 10:20:00', 'Member', 4),
(7, 'Phạt vi phạm', 'Bạn bị phạt 50,000 VND do pin dưới 90% khi trả xe', 'penalty', 1, '2025-01-20 20:10:00', 'Schedule', 4),

-- Notifications cho user 3 (owner của group 2)
(3, 'Nhóm đã được duyệt', 'Nhóm "Nhóm VF e34 TP.HCM" của bạn đã được admin phê duyệt!', 'group_approval', 1, '2025-01-09 11:35:00', 'Group', 2),
(3, 'Biểu quyết mới', 'Bạn đã tạo biểu quyết: "Quy định sử dụng xe vào ban đêm"', 'vote_new', 0, '2025-01-24 14:35:00', 'Vote', 2),

-- Notifications cho user 8
(8, 'Đề xuất lại phần trăm sở hữu', 'Chủ nhóm đề xuất phần trăm sở hữu 18% thay vì 25%', 'counter_offer', 0, '2025-01-23 14:00:00', 'Member', 8),

-- Notifications cho user 6
(6, 'Yêu cầu tham gia được chấp nhận', 'Yêu cầu tham gia "Nhóm VF e34 TP.HCM" đã được phê duyệt!', 'member_approved', 1, '2025-01-11 08:35:00', 'Member', 7),
(6, 'Phạt vi phạm', 'Bạn bị phạt 10,000 VND do trả xe muộn 10 phút', 'penalty', 1, '2025-01-21 12:15:00', 'Schedule', 8);

-- ==========================================
-- 10. PAYMENTS (Payment records)
-- ==========================================
INSERT INTO payments (group_id, member_id, amount, payment_method, qr_code_url, status, created_at, confirmed_at, transaction_reference) VALUES
-- Group 1 payments
(1, 1, 3400000.00, 'bank_transfer', '/uploads/qr/payment_1.png', 'confirmed', '2025-01-06 11:30:00', '2025-01-06 12:00:00', 'TXN001-20250106'),
(1, 2, 1700000.00, 'bank_transfer', '/uploads/qr/payment_2.png', 'confirmed', '2025-01-07 09:30:00', '2025-01-07 10:00:00', 'TXN002-20250107'),
(1, 3, 1275000.00, 'momo', '/uploads/qr/payment_3.png', 'confirmed', '2025-01-08 14:30:00', '2025-01-08 15:00:00', 'MOMO-20250108-001'),
(1, 4, 1275000.00, 'bank_transfer', '/uploads/qr/payment_4.png', 'confirmed', '2025-01-09 10:30:00', '2025-01-09 11:00:00', 'TXN003-20250109'),
(1, 5, 850000.00, 'vnpay', '/uploads/qr/payment_5.png', 'confirmed', '2025-01-10 16:30:00', '2025-01-10 17:00:00', 'VNPAY-20250110-001'),

-- Group 2 payments
(2, 6, 1440000.00, 'bank_transfer', '/uploads/qr/payment_6.png', 'confirmed', '2025-01-09 12:30:00', '2025-01-09 13:00:00', 'TXN004-20250109'),
(2, 7, 960000.00, 'momo', '/uploads/qr/payment_7.png', 'confirmed', '2025-01-11 08:30:00', '2025-01-11 09:00:00', 'MOMO-20250111-001'),
(2, 8, 1200000.00, 'bank_transfer', '/uploads/qr/payment_8.png', 'pending', '2025-01-23 13:30:00', NULL, 'TXN005-20250123');

-- ==========================================
-- END OF SAMPLE DATA
-- ==========================================

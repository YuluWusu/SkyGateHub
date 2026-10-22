
USE `QL_Cong_Troi_Dong_Giang`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- XÓA DỮ LIỆU CŨ THEO THỨ TỰ
truncate TABLE `CHECK_IN`;
truncate TABLE `HOAN_TIEN`;
truncate TABLE `HUY_DON`;
truncate TABLE `THANH_TOAN`;
truncate TABLE `DAT_BAN`;
truncate TABLE `QUYEN_LOI_DON`;
truncate TABLE `COMBO_THANH_PHAN`;
truncate TABLE `THANH_PHAN_DICH_VU`;
truncate TABLE `PHAN_PHONG`;
truncate TABLE `DAT_PHONG`;
truncate TABLE `VE`;
truncate TABLE `CHI_TIET_DON`;
truncate TABLE `DON_DAT`;
truncate TABLE `DICH_VU_CA_AN`;
truncate TABLE `BAN_AN`;
truncate TABLE `CA_AN`;
truncate TABLE `DICH_VU_AN_UONG`;
truncate TABLE `PHONG`;
truncate TABLE `LOAI_PHONG`;
truncate TABLE `KHU_LUU_TRU`;
truncate TABLE `NGAY_DAC_BIET`;
truncate TABLE `BANG_GIA_VE`;
truncate TABLE `KHO_VE`;
truncate TABLE `LOAI_VE`;
truncate TABLE `NGUOI_DUNG_VAI_TRO`;
truncate TABLE `VAI_TRO`;
truncate TABLE `NGUOI_DUNG`;
truncate TABLE `CHINH_SACH_COC`;
truncate TABLE `CHINH_SACH_HUY`;

-- 1. NGUOI_DUNG (25 bản ghi)
INSERT INTO `NGUOI_DUNG` (`MA_NGUOI_DUNG`, `TEN_DANG_NHAP`, `MAT_KHAU_HASH`, `HO_TEN`, `EMAIL`, `SO_DIEN_THOAI`, `TRANG_THAI`, `CAN_DOI_MAT_KHAU`) VALUES
  ('ND001', 'admin', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Nguyễn Quản Trị', 'admin@skygate.vn', '0905111001', 1, 0),
  ('ND002', 'nv_ve1', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Trần Thị Bán Vé', 've1@skygate.vn', '0905111002', 1, 0),
  ('ND003', 'nv_ve2', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Lê Văn Soát Vé', 've2@skygate.vn', '0905111003', 1, 0),
  ('ND004', 'nv_letan1', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Phạm Thu Hương', 'letan1@skygate.vn', '0905111004', 1, 0),
  ('ND005', 'nv_letan2', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Hoàng Văn Phong', 'letan2@skygate.vn', '0905111005', 1, 0),
  ('ND006', 'nv_bep1', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Nguyễn Văn Bếp', 'nhahang1@skygate.vn', '0905111006', 1, 0),
  ('ND007', 'nv_nhahang1', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Đỗ Thị Thực Khách', 'nhahang2@skygate.vn', '0905111007', 1, 0),
  ('ND008', 'khach08', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Trần Anh Tuấn', 'khach08@gmail.com', '0912345008', 1, 0),
  ('ND009', 'khach09', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Nguyễn Minh Châu', 'khach09@gmail.com', '0912345009', 1, 0),
  ('ND010', 'khach10', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Võ Văn Kiệt', 'khach10@gmail.com', '0912345010', 1, 0),
  ('ND011', 'khach11', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Đặng Thùy Tiên', 'khach11@gmail.com', '0912345011', 1, 0),
  ('ND012', 'khach12', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Bùi Đức Thịnh', 'khach12@gmail.com', '0912345012', 1, 0),
  ('ND013', 'khach13', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Hoàng Mỹ Linh', 'khach13@gmail.com', '0912345013', 1, 0),
  ('ND014', 'khach14', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Ngô Quốc Bảo', 'khach14@gmail.com', '0912345014', 1, 0),
  ('ND015', 'khach15', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Dương Cẩm Tú', 'khach15@gmail.com', '0912345015', 1, 0),
  ('ND016', 'khach16', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Trương Gia Bình', 'khach16@gmail.com', '0912345016', 1, 0),
  ('ND017', 'khach17', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Đinh Tiến Dũng', 'khach17@gmail.com', '0912345017', 1, 0),
  ('ND018', 'khach18', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Phan Thanh Hậu', 'khach18@gmail.com', '0912345018', 1, 0),
  ('ND019', 'khach19', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Vũ Hoàng Yến', 'khach19@gmail.com', '0912345019', 1, 0),
  ('ND020', 'khach20', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Lâm Trường An', 'khach20@gmail.com', '0912345020', 1, 0),
  ('ND021', 'khach21', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Đoàn Ngọc Hải', 'khach21@gmail.com', '0912345021', 1, 0),
  ('ND022', 'khach22', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Nguyễn Hữu Thọ', 'khach22@gmail.com', '0912345022', 1, 0),
  ('ND023', 'khach23', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Trịnh Công Sơn', 'khach23@gmail.com', '0912345023', 1, 0),
  ('ND024', 'khach24', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Hà Kiều Anh', 'khach24@gmail.com', '0912345024', 1, 0),
  ('ND025', 'khach25', '$2b$10$oORuNNlzGCHUCmmkfiTo5.8qA8I/578HboPUEqwDljUIGluJcaVK6', 'Lương Thế Vinh', 'khach25@gmail.com', '0912345025', 1, 0);

-- 2. VAI_TRO (4 vai trò)
INSERT INTO `VAI_TRO` (`MA_VAI_TRO`, `TEN_VAI_TRO`, `MO_TA`) VALUES
  ('VT001', 'QUAN_LY', 'Quản trị viên toàn quyền hệ thống'),
  ('VT004', 'NHAN_VIEN_VE', 'Nhân viên bán vé và check-in cổng vào'),
  ('VT005', 'NHAN_VIEN_NHA_HANG', 'Nhân viên quản lý bàn ăn và check-in nhà hàng'),
  ('VT006', 'NHAN_VIEN_LUU_TRU', 'Nhân viên lễ tân và phân phòng lưu trú');

-- 3. NGUOI_DUNG_VAI_TRO (25 bản ghi)
INSERT INTO `NGUOI_DUNG_VAI_TRO` (`MA_NGUOI_DUNG`, `MA_VAI_TRO`) VALUES
  ('ND001', 'VT001'),
  ('ND002', 'VT004'),
  ('ND003', 'VT004'),
  ('ND004', 'VT006'),
  ('ND005', 'VT006'),
  ('ND006', 'VT005'),
  ('ND007', 'VT005'),
  ('ND008', 'VT004'),
  ('ND009', 'VT004'),
  ('ND010', 'VT004'),
  ('ND011', 'VT004'),
  ('ND012', 'VT004'),
  ('ND013', 'VT004'),
  ('ND014', 'VT004'),
  ('ND015', 'VT004'),
  ('ND016', 'VT004'),
  ('ND017', 'VT004'),
  ('ND018', 'VT004'),
  ('ND019', 'VT004'),
  ('ND020', 'VT004'),
  ('ND021', 'VT004'),
  ('ND022', 'VT004'),
  ('ND023', 'VT004'),
  ('ND024', 'VT004'),
  ('ND025', 'VT004');

-- 4. LOAI_VE (25 bản ghi loại vé & combo)
INSERT INTO `LOAI_VE` (`MA_LOAI_VE`, `TEN_LOAI_VE`, `LOAI_VE`, `MO_TA`, `GIA_VE`, `TRANG_THAI`) VALUES
  ('VE01', 'Vé Tham Quan Người Lớn', 'NGUOI_LON', 'Vé vào cổng tham quan toàn khu dành cho người lớn', 350000, 1),
  ('VE02', 'Vé Tham Quan Trẻ Em', 'TRE_EM', 'Vé vào cổng cho trẻ em chiều cao 1m - 1m4', 200000, 1),
  ('VE03', 'Combo Cổng Trời 1 Ngày Người Lớn', 'TRON_GOI', 'Vé cổng + cáp treo + buffet trưa', 650000, 1),
  ('VE04', 'Combo Cổng Trời 1 Ngày Trẻ Em', 'TRON_GOI', 'Vé cổng + cáp treo + buffet trưa trẻ em', 450000, 1),
  ('VE05', 'Vé Cáp Treo Người Lớn', 'NGUOI_LON', 'Vé khứ hồi cáp treo lên đỉnh Cổng Trời', 250000, 1),
  ('VE06', 'Vé Cáp Treo Trẻ Em', 'TRE_EM', 'Vé khứ hồi cáp treo cho trẻ em', 150000, 1),
  ('VE07', 'Vé Xe Điện Nội Khu', 'KHAC', 'Vé sử dụng xe điện không giới hạn trong ngày', 50000, 1),
  ('VE08', 'Combo Trải Nghiệm Sinh Thái VIP', 'TRON_GOI', 'Vé cổng + cáp treo + hướng dẫn viên + buffet VIP', 950000, 1),
  ('VE09', 'Vé Tham Quan Suối Hoa Người Lớn', 'NGUOI_LON', 'Khu tiểu cảnh suối hoa và cổng trời', 280000, 1),
  ('VE10', 'Vé Tham Quan Suối Hoa Trẻ Em', 'TRE_EM', 'Khu tiểu cảnh suối hoa cho trẻ em', 160000, 1),
  ('VE11', 'Vé Chèo Thuyền Kayak Cây Xanh', 'KHAC', 'Vé 60 phút chèo thuyền trên sông suối', 120000, 1),
  ('VE12', 'Vé Trượt Thác Nước Tự Nhiên', 'KHAC', 'Trò chơi cảm giác mạnh trượt thác', 150000, 1),
  ('VE13', 'Combo Gia Đình 2 Lớn 1 Trẻ', 'TRON_GOI', 'Gói tham quan trọn gói cho gia đình nhỏ', 1200000, 1),
  ('VE14', 'Combo Gia Đình 2 Lớn 2 Trẻ', 'TRON_GOI', 'Gói tham quan trọn gói cho gia đình 4 người', 1500000, 1),
  ('VE15', 'Vé Tham Quan Hoàng Hôn Chiều', 'NGUOI_LON', 'Áp dụng check-in sau 15h00 hàng ngày', 220000, 1),
  ('VE16', 'Vé Tham Quan Hoàng Hôn Trẻ Em', 'TRE_EM', 'Áp dụng check-in sau 15h00 cho trẻ em', 130000, 1),
  ('VE17', 'Combo Cáp Treo Và Buffet', 'TRON_GOI', 'Cáp treo khứ hồi kèm 01 suất buffet trưa', 520000, 1),
  ('VE18', 'Vé Vào Cổng Người Cao Tuổi', 'KHAC', 'Ưu đãi cho người trên 60 tuổi', 180000, 1),
  ('VE19', 'Vé Công Đoàn Viên / Học Sinh', 'KHAC', 'Chính sách giá ưu đãi theo đoàn học sinh', 150000, 1),
  ('VE20', 'Combo Teambuilding Đoàn', 'TRON_GOI', 'Vé cổng + sân cỏ tổ chức + âm thanh teambuilding', 400000, 1),
  ('VE21', 'Vé Trải Nghiệm Làng Nghề Cổ Từ', 'KHAC', 'Tham quan và giao lưu văn hóa truyền thống', 100000, 1),
  ('VE22', 'Combo Cửa Ngõ Thiên Nhiên', 'TRON_GOI', 'Vé cổng + nước suối + xe điện 1 ngày', 390000, 1),
  ('VE23', 'Vé Cung Đường Rừng Nguyên Sinh', 'NGUOI_LON', 'Trekking có hướng dẫn viên kiểm lâm', 300000, 1),
  ('VE24', 'Vé Trải Nghiệm Hồ Bơi Vô Cực', 'KHAC', 'Vé sử dụng bể bơi nước khoáng giữa rừng', 80000, 1),
  ('VE25', 'Combo All-Inclusive Siêu VIP', 'TRON_GOI', 'Trọn gói toàn bộ dịch vụ giải trí trong ngày', 1800000, 1);

-- 5. BANG_GIA_VE (25 bản ghi)
INSERT INTO `BANG_GIA_VE` (`MA_BANG_GIA`, `MA_LOAI_VE`, `TEN_BANG_GIA`, `DIEU_KIEN_GIA`, `HIEU_LUC_TU`, `HIEU_LUC_DEN`, `DON_GIA`, `UU_TIEN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('BG01', 'VE01', 'Bảng giá tiêu chuẩn Vé Tham Quan Người Lớn', 'MAC_DINH', '2026-01-01', '2026-12-31', 350000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG02', 'VE02', 'Bảng giá tiêu chuẩn Vé Tham Quan Trẻ Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 200000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG03', 'VE03', 'Bảng giá tiêu chuẩn Combo Cổng Trời 1 Ngày Người Lớn', 'MAC_DINH', '2026-01-01', '2026-12-31', 650000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG04', 'VE04', 'Bảng giá tiêu chuẩn Combo Cổng Trời 1 Ngày Trẻ Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 450000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG05', 'VE05', 'Bảng giá tiêu chuẩn Vé Cáp Treo Người Lớn', 'MAC_DINH', '2026-01-01', '2026-12-31', 250000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG06', 'VE06', 'Bảng giá tiêu chuẩn Vé Cáp Treo Trẻ Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 150000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG07', 'VE07', 'Bảng giá tiêu chuẩn Vé Xe Điện Nội Khu', 'MAC_DINH', '2026-01-01', '2026-12-31', 50000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG08', 'VE08', 'Bảng giá tiêu chuẩn Combo Trải Nghiệm Sinh Thái VIP', 'MAC_DINH', '2026-01-01', '2026-12-31', 950000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG09', 'VE09', 'Bảng giá tiêu chuẩn Vé Tham Quan Suối Hoa Người Lớn', 'MAC_DINH', '2026-01-01', '2026-12-31', 280000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG10', 'VE10', 'Bảng giá tiêu chuẩn Vé Tham Quan Suối Hoa Trẻ Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 160000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG11', 'VE11', 'Bảng giá tiêu chuẩn Vé Chèo Thuyền Kayak Cây Xanh', 'MAC_DINH', '2026-01-01', '2026-12-31', 120000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG12', 'VE12', 'Bảng giá tiêu chuẩn Vé Trượt Thác Nước Tự Nhiên', 'MAC_DINH', '2026-01-01', '2026-12-31', 150000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG13', 'VE13', 'Bảng giá tiêu chuẩn Combo Gia Đình 2 Lớn 1 Trẻ', 'MAC_DINH', '2026-01-01', '2026-12-31', 1200000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG14', 'VE14', 'Bảng giá tiêu chuẩn Combo Gia Đình 2 Lớn 2 Trẻ', 'MAC_DINH', '2026-01-01', '2026-12-31', 1500000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG15', 'VE15', 'Bảng giá tiêu chuẩn Vé Tham Quan Hoàng Hôn Chiều', 'MAC_DINH', '2026-01-01', '2026-12-31', 220000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG16', 'VE16', 'Bảng giá tiêu chuẩn Vé Tham Quan Hoàng Hôn Trẻ Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 130000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG17', 'VE17', 'Bảng giá tiêu chuẩn Combo Cáp Treo Và Buffet', 'MAC_DINH', '2026-01-01', '2026-12-31', 520000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG18', 'VE18', 'Bảng giá tiêu chuẩn Vé Vào Cổng Người Cao Tuổi', 'MAC_DINH', '2026-01-01', '2026-12-31', 180000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG19', 'VE19', 'Bảng giá tiêu chuẩn Vé Công Đoàn Viên / Học Sinh', 'MAC_DINH', '2026-01-01', '2026-12-31', 150000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG20', 'VE20', 'Bảng giá tiêu chuẩn Combo Teambuilding Đoàn', 'MAC_DINH', '2026-01-01', '2026-12-31', 400000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG21', 'VE21', 'Bảng giá tiêu chuẩn Vé Trải Nghiệm Làng Nghề Cổ Từ', 'MAC_DINH', '2026-01-01', '2026-12-31', 100000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG22', 'VE22', 'Bảng giá tiêu chuẩn Combo Cửa Ngõ Thiên Nhiên', 'MAC_DINH', '2026-01-01', '2026-12-31', 390000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG23', 'VE23', 'Bảng giá tiêu chuẩn Vé Cung Đường Rừng Nguyên Sinh', 'MAC_DINH', '2026-01-01', '2026-12-31', 300000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG24', 'VE24', 'Bảng giá tiêu chuẩn Vé Trải Nghiệm Hồ Bơi Vô Cực', 'MAC_DINH', '2026-01-01', '2026-12-31', 80000, 10, 1, 'Giá hệ thống áp dụng năm 2026'),
  ('BG25', 'VE25', 'Bảng giá tiêu chuẩn Combo All-Inclusive Siêu VIP', 'MAC_DINH', '2026-01-01', '2026-12-31', 1800000, 10, 1, 'Giá hệ thống áp dụng năm 2026');

-- 6. NGAY_DAC_BIET (10 ngày lễ)
INSERT INTO `NGAY_DAC_BIET` (`MA_NGAY_DAC_BIET`, `NGAY_AP_DUNG`, `TEN_NGAY`, `LOAI_NGAY`, `GHI_CHU`, `TRANG_THAI`) VALUES
  ('NDB01', '2026-01-01', 'Tết Dương Lịch 2026', 'LE_TET', 'Nghỉ lễ toàn quốc', 1),
  ('NDB02', '2026-02-16', '29 Tết Bính Ngọ', 'LE_TET', 'Tết Nguyên Đán', 1),
  ('NDB03', '2026-02-17', 'Mùng 1 Tết Bính Ngọ', 'LE_TET', 'Tết Nguyên Đán', 1),
  ('NDB04', '2026-02-18', 'Mùng 2 Tết Bính Ngọ', 'LE_TET', 'Tết Nguyên Đán', 1),
  ('NDB05', '2026-04-26', 'Giỗ Tổ Hùng Vương', 'LE_TET', '10 tháng 3 Âm lịch', 1),
  ('NDB06', '2026-04-30', 'Ngày Giải Phóng Miền Nam', 'LE_TET', '30/4', 1),
  ('NDB07', '2026-05-01', 'Quốc Tế Lao Động', 'LE_TET', '1/5', 1),
  ('NDB08', '2026-09-02', 'Quốc Khánh 2/9', 'LE_TET', 'Nghỉ Quốc khánh', 1),
  ('NDB09', '2026-10-20', 'Ngày Phụ Nữ Việt Nam', 'SU_KIEN', 'Ưu đãi khách nữ', 1),
  ('NDB10', '2026-12-25', 'Lễ Giáng Sinh', 'SU_KIEN', 'Mùa lễ hội cuối năm', 1);

-- 7. KHO_VE (25 bản ghi tồn kho)
INSERT INTO `KHO_VE` (`MA_KHO_VE`, `MA_LOAI_VE`, `NGAY_THAM_QUAN`, `SO_LUONG_TOI_DA`, `SO_LUONG_DA_BAN`) VALUES
  ('KV001', 'VE02', '2026-05-02', 500, 5),
  ('KV002', 'VE03', '2026-05-03', 500, 10),
  ('KV003', 'VE04', '2026-05-04', 500, 15),
  ('KV004', 'VE05', '2026-05-05', 500, 20),
  ('KV005', 'VE06', '2026-05-06', 500, 25),
  ('KV006', 'VE07', '2026-05-07', 500, 30),
  ('KV007', 'VE08', '2026-05-08', 500, 35),
  ('KV008', 'VE09', '2026-05-09', 500, 40),
  ('KV009', 'VE10', '2026-05-10', 500, 45),
  ('KV010', 'VE11', '2026-05-11', 500, 50),
  ('KV011', 'VE12', '2026-05-12', 500, 55),
  ('KV012', 'VE13', '2026-05-13', 500, 60),
  ('KV013', 'VE14', '2026-05-14', 500, 65),
  ('KV014', 'VE15', '2026-05-15', 500, 70),
  ('KV015', 'VE16', '2026-05-16', 500, 75),
  ('KV016', 'VE17', '2026-05-17', 500, 80),
  ('KV017', 'VE18', '2026-05-18', 500, 85),
  ('KV018', 'VE19', '2026-05-19', 500, 90),
  ('KV019', 'VE20', '2026-05-20', 500, 95),
  ('KV020', 'VE21', '2026-05-21', 500, 100),
  ('KV021', 'VE22', '2026-05-22', 500, 105),
  ('KV022', 'VE23', '2026-05-23', 500, 110),
  ('KV023', 'VE24', '2026-05-24', 500, 115),
  ('KV024', 'VE25', '2026-05-25', 500, 120),
  ('KV025', 'VE01', '2026-05-26', 500, 125);

-- 8. KHU_LUU_TRU (4 khu lưu trú)
INSERT INTO `KHU_LUU_TRU` (`MA_KHU`, `TEN_KHU`, `HANG_SAO`, `DIA_CHI`, `EMAIL`, `HOTLINE`, `HINH_ANH`) VALUES
  (1, 'FIVITEL Sông Ngân', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'song_ngan.jpg'),
  (2, 'FIVITEL Cầu Kiều', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'cau_kieu.jpg'),
  (3, 'FIVITEL Ô Thước', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'o_thuoc.jpg'),
  (4, 'Nhà Moong', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'nha_moong.jpg');

-- 8.1. LOAI_PHONG (15 loại phòng từng khu, diện tích và giá từ ảnh thực tế)
INSERT INTO `LOAI_PHONG` (`MA_LOAI_PHONG`, `MA_KHU`, `TEN_LOAI_PHONG`, `DIEN_TICH`, `LOAI_GIUONG`, `SUC_CHUA_NGUOI_LON`, `SUC_CHUA_TRE_EM`, `GIA_CO_BAN`, `HINH_ANH`) VALUES
  -- 1. FIVITEL Sông Ngân (Ảnh 4: 27m2 -> 33m2)
  (1, 1, 'Superior DBL', 27, 'DBL', 2, 1, 900000.00, 'songngan_sup_dbl.jpg'),
  (2, 1, 'Superior Twin', 27, 'TWIN', 2, 1, 900000.00, 'songngan_sup_twin.jpg'),
  (3, 1, 'Deluxe DBL', 27, 'DBL', 2, 1, 1000000.00, 'songngan_dlx_dbl.jpg'),
  (4, 1, 'Deluxe Twin', 27, 'TWIN', 2, 1, 1000000.00, 'songngan_dlx_twin.jpg'),
  (5, 1, 'Suite DBL', 28, 'DBL', 2, 1, 1100000.00, 'songngan_suite_dbl.jpg'),
  (6, 1, 'Family DBL + Single', 33, 'DBL_SINGLE', 3, 1, 1200000.00, 'songngan_family.jpg'),
  -- 2. FIVITEL Cầu Kiều (Ảnh 5: 24m2, 30m2, 52m2)
  (7, 2, 'Standard DBL', 24, 'DBL', 2, 1, 800000.00, 'caukieu_std_dbl.jpg'),
  (8, 2, 'Deluxe DBL', 30, 'DBL', 2, 1, 1000000.00, 'caukieu_dlx_dbl.jpg'),
  (9, 2, 'Vip DBL', 52, 'DBL', 2, 1, 1350000.00, 'caukieu_vip_dbl.jpg'),
  -- 3. FIVITEL Ô Thước (Ảnh 1: 25m2, 30m2)
  (10, 3, 'Standard DBL', 25, 'DBL', 2, 1, 800000.00, 'othuoc_std_dbl.jpg'),
  (11, 3, 'Superior DBL', 30, 'DBL', 2, 1, 900000.00, 'othuoc_sup_dbl.jpg'),
  (12, 3, 'Superior Twin', 30, 'TWIN', 2, 1, 900000.00, 'othuoc_sup_twin.jpg'),
  (13, 3, 'Deluxe DBL', 30, 'DBL', 2, 1, 1000000.00, 'othuoc_dlx_dbl.jpg'),
  (14, 3, 'Deluxe Twin', 30, 'TWIN', 2, 1, 1000000.00, 'othuoc_dlx_twin.jpg'),
  -- 4. Nhà Moong (Ảnh 2: 25m2)
  (15, 4, 'Nhà Moong DBL/Bungalow', 25, 'BUNGALOW', 2, 1, 900000.00, 'nhamoong_dbl.jpg');

-- 9. PHONG (30 phòng vật lý phân bổ theo 15 loại phòng)
INSERT INTO `PHONG` (`MA_PHONG`, `MA_LOAI_PHONG`, `SO_PHONG`, `TRANG_THAI`) VALUES
  ('P001', 1, 'Room-101', 'SAN_SANG'),
  ('P002', 3, 'Room-102', 'SAN_SANG'),
  ('P003', 5, 'Room-103', 'SAN_SANG'),
  ('P004', 1, 'Room-104', 'SAN_SANG'),
  ('P005', 9, 'Room-105', 'SAN_SANG'),
  ('P006', 6, 'Room-106', 'SAN_SANG'),
  ('P007', 15, 'Room-107', 'SAN_SANG'),
  ('P008', 8, 'Room-108', 'SAN_SANG'),
  ('P009', 10, 'Room-109', 'SAN_SANG'),
  ('P010', 11, 'Room-110', 'SAN_SANG'),
  ('P011', 13, 'Room-111', 'SAN_SANG'),
  ('P012', 1, 'Room-112', 'SAN_SANG'),
  ('P013', 3, 'Room-113', 'SAN_SANG'),
  ('P014', 5, 'Room-114', 'SAN_SANG'),
  ('P015', 9, 'Room-115', 'SAN_SANG'),
  ('P016', 8, 'Room-116', 'SAN_SANG'),
  ('P017', 10, 'Room-117', 'SAN_SANG'),
  ('P018', 11, 'Room-118', 'SAN_SANG'),
  ('P019', 13, 'Room-119', 'SAN_SANG'),
  ('P020', 1, 'Room-120', 'SAN_SANG'),
  ('P021', 3, 'Room-121', 'SAN_SANG'),
  ('P022', 5, 'Room-122', 'SAN_SANG'),
  ('P023', 2, 'Room-123', 'SAN_SANG'),
  ('P024', 4, 'Room-124', 'SAN_SANG'),
  ('P025', 7, 'Room-125', 'DANG_SU_DUNG'),
  ('P026', 12, 'Room-126', 'DANG_SU_DUNG'),
  ('P027', 14, 'Room-127', 'DANG_SU_DUNG'),
  ('P028', 15, 'Room-128', 'DANG_SU_DUNG'),
  ('P029', 6, 'Room-129', 'BAO_TRI'),
  ('P030', 7, 'Room-130', 'BAO_TRI');

-- 10. DICH_VU_AN_UONG (25 dịch vụ nhà hàng)
INSERT INTO `DICH_VU_AN_UONG` (`MA_DICH_VU`, `TEN_DICH_VU`, `LOAI_DICH_VU`, `MO_TA`, `GIA_DICH_VU`, `TRANG_THAI`) VALUES
  ('DV01', 'Buffet Trưa Ẩm Thực Cổ Từ', 'BUFFET', 'Buffet hơn 50 món đặc sản rừng Tây Giang', 250000, 1),
  ('DV02', 'Buffet Tối Nướng BBQ Cao Nguyên', 'BUFFET', 'Tiệc nướng ngoài trời với thịt lợn bản', 350000, 1),
  ('DV03', 'Set Menu Cơm Lam Gà Nướng 4 Người', 'SET_MENU', 'Cơm lam ống tre kèm gà đồi nướng mật ong', 650000, 1),
  ('DV04', 'Set Menu Cá Niên Rau Rừng', 'SET_MENU', 'Cá niên nướng trui kèm rau dớn suối', 480000, 1),
  ('DV05', 'Set Menu Thịt Lợn Bản 7 Món', 'SET_MENU', 'Thịt heo rừng bản chế biến 7 món đặc sắc', 820000, 1),
  ('DV06', 'Lẩu Gà Lá É Tây Giang', 'A_LA_CARTE', 'Nồi lẩu gà soi lá é thơm nồng', 450000, 1),
  ('DV07', 'Heo Bản Xào Lăn', 'A_LA_CARTE', 'Đĩa thịt lợn bản thơm lừng gia vị núi rừng', 180000, 1),
  ('DV08', 'Rau Dớn Xào Tỏi', 'A_LA_CARTE', 'Rau dớn tươi hái ven suối núi', 80000, 1),
  ('DV09', 'Cá Niên Chiên Giòn', 'A_LA_CARTE', 'Cá suối nước lạnh chiên giòn chấm mắm gừng', 160000, 1),
  ('DV10', 'Xôi Nếp Nướng Ống Tre', 'A_LA_CARTE', 'Xôi nếp thơm dẻo nấu ống tre', 60000, 1),
  ('DV11', 'Rượu Cần Cổ Từ Đặc Biệt', 'DO_UONG', 'Bình rượu cần ủ men lá cây truyền thống', 220000, 1),
  ('DV12', 'Set Ăn Sáng EcoGate Breakfast', 'SET_MENU', 'Phở bò/bánh mì + cà phê sáng', 95000, 1),
  ('DV13', 'Buffet Sáng Continental', 'BUFFET', 'Buffet sáng tiêu chuẩn quốc tế', 150000, 1),
  ('DV14', 'Set Tiệc Nướng Sân Vườn VIP', 'SET_MENU', 'Tiệc BBQ riêng tại căn villa', 1500000, 1),
  ('DV15', 'Set Trà Chiều Hoàng Hôn', 'DO_UONG', 'Trà thảo mộc kèm bánh ngọt ngắm hoàng hôn', 180000, 1),
  ('DV16', 'Bò Nướng Đá Núi Lửa', 'A_LA_CARTE', 'Thịt bò nướng trên phiến đá nóng', 260000, 1),
  ('DV17', 'Gỏi Hoa Chuối Rừng Gà Xé', 'A_LA_CARTE', 'Món khai vị chua ngọt thơm ngon', 140000, 1),
  ('DV18', 'Cháo Nấm Tràm Bắp Bò', 'A_LA_CARTE', 'Tô cháo nóng bổ dưỡng đêm sương lạnh', 110000, 1),
  ('DV19', 'Canh Thụt Cá Suối Cổ Từ', 'A_LA_CARTE', 'Món canh đặc trưng nấu trong ống nứa', 190000, 1),
  ('DV20', 'Cơm Chiên Muối É Cá Niên', 'A_LA_CARTE', 'Cơm chiên giòn muối hạt é nồng ấm', 130000, 1),
  ('DV21', 'Bia Thủ Công Artisan Đông Giang', 'DO_UONG', 'Bia lên men tự nhiên độc đáo', 75000, 1),
  ('DV22', 'Nước Ép Sâm Dây Ngọc Linh', 'DO_UONG', 'Thức uống giải nhiệt bổ dưỡng', 65000, 1),
  ('DV23', 'Set Chay Thanh Tịnh Rừng Xanh', 'SET_MENU', 'Thực đơn chay 5 món thanh nhẹ', 280000, 1),
  ('DV24', 'Hải Sản Nướng Than Hồng', 'A_LA_CARTE', 'Tôm mực tươi nướng muối ớt', 320000, 1),
  ('DV25', 'Set Tiệc Lửa Trại Đêm Giao Lưu', 'SET_MENU', 'Khoai bắp nướng + thịt xiên + rượu cần', 550000, 1);

-- 11. CA_AN (3 ca ăn)
INSERT INTO `CA_AN` (`MA_CA_AN`, `TEN_CA_AN`, `GIO_BAT_DAU`, `GIO_KET_THUC`, `TRANG_THAI`) VALUES
  ('CA_SANG', 'Bữa Sáng Nắng Sớm', '06:30:00', '09:30:00', 1),
  ('CA_TRUA', 'Bữa Trưa Thung Lũng', '11:00:00', '14:00:00', 1),
  ('CA_TOI', 'Bữa Tối Lửa Trại', '17:30:00', '21:30:00', 1);

-- 12. BAN_AN (25 bàn ăn)
INSERT INTO `BAN_AN` (`MA_BAN_AN`, `SO_BAN`, `KHU_VUC`, `SUC_CHUA`, `TRANG_THAI`) VALUES
  ('BA01', 'B01', 'Khu Sân Hồ', 2, 'SAN_SANG'),
  ('BA02', 'B02', 'Khu Sân Hồ', 4, 'SAN_SANG'),
  ('BA03', 'B03', 'Khu Sân Hồ', 6, 'SAN_SANG'),
  ('BA04', 'B04', 'Khu Sân Hồ', 10, 'SAN_SANG'),
  ('BA05', 'B05', 'Khu Sân Hồ', 2, 'SAN_SANG'),
  ('BA06', 'B06', 'Khu Sân Hồ', 4, 'SAN_SANG'),
  ('BA07', 'B07', 'Khu Sân Hồ', 6, 'SAN_SANG'),
  ('BA08', 'B08', 'Khu Suối Hoa', 10, 'SAN_SANG'),
  ('BA09', 'B09', 'Khu Suối Hoa', 2, 'SAN_SANG'),
  ('BA10', 'B10', 'Khu Suối Hoa', 4, 'SAN_SANG'),
  ('BA11', 'B11', 'Khu Suối Hoa', 6, 'SAN_SANG'),
  ('BA12', 'B12', 'Khu Suối Hoa', 10, 'SAN_SANG'),
  ('BA13', 'B13', 'Khu Suối Hoa', 2, 'SAN_SANG'),
  ('BA14', 'B14', 'Khu Suối Hoa', 4, 'SAN_SANG'),
  ('BA15', 'B15', 'Khu Hoàng Hôn', 6, 'SAN_SANG'),
  ('BA16', 'B16', 'Khu Hoàng Hôn', 10, 'SAN_SANG'),
  ('BA17', 'B17', 'Khu Hoàng Hôn', 2, 'SAN_SANG'),
  ('BA18', 'B18', 'Khu Hoàng Hôn', 4, 'SAN_SANG'),
  ('BA19', 'B19', 'Khu Hoàng Hôn', 6, 'SAN_SANG'),
  ('BA20', 'B20', 'Khu Hoàng Hôn', 10, 'SAN_SANG'),
  ('BA21', 'B21', 'Khu Mây Ngàn VIP', 2, 'DANG_SU_DUNG'),
  ('BA22', 'B22', 'Khu Mây Ngàn VIP', 4, 'DANG_SU_DUNG'),
  ('BA23', 'B23', 'Khu Mây Ngàn VIP', 6, 'DANG_SU_DUNG'),
  ('BA24', 'B24', 'Khu Mây Ngàn VIP', 10, 'DANG_SU_DUNG'),
  ('BA25', 'B25', 'Khu Mây Ngàn VIP', 2, 'DANG_SU_DUNG');

-- 13. DICH_VU_CA_AN (25 bản ghi liên kết dịch vụ - ca ăn)
INSERT INTO `DICH_VU_CA_AN` (`MA_DICH_VU`, `MA_CA_AN`, `TRANG_THAI`) VALUES
  ('DV01', 'CA_TRUA', 1),
  ('DV02', 'CA_TOI', 1),
  ('DV03', 'CA_TRUA', 1),
  ('DV04', 'CA_TOI', 1),
  ('DV05', 'CA_TRUA', 1),
  ('DV06', 'CA_TOI', 1),
  ('DV07', 'CA_TRUA', 1),
  ('DV08', 'CA_TOI', 1),
  ('DV09', 'CA_TRUA', 1),
  ('DV10', 'CA_TOI', 1),
  ('DV11', 'CA_TRUA', 1),
  ('DV12', 'CA_SANG', 1),
  ('DV13', 'CA_SANG', 1),
  ('DV14', 'CA_TOI', 1),
  ('DV15', 'CA_TRUA', 1),
  ('DV16', 'CA_TOI', 1),
  ('DV17', 'CA_TRUA', 1),
  ('DV18', 'CA_TOI', 1),
  ('DV19', 'CA_TRUA', 1),
  ('DV20', 'CA_TOI', 1),
  ('DV21', 'CA_TRUA', 1),
  ('DV22', 'CA_TOI', 1),
  ('DV23', 'CA_TRUA', 1),
  ('DV24', 'CA_TOI', 1),
  ('DV25', 'CA_TRUA', 1);

-- 14. THANH_PHAN_DICH_VU (20 thành phần dịch vụ)
INSERT INTO `THANH_PHAN_DICH_VU` (`MA_THANH_PHAN`, `TEN_THANH_PHAN`, `LOAI_THANH_PHAN`, `MA_LOAI_VE`, `MA_DV_AN_UONG`, `MO_TA`, `TRANG_THAI`) VALUES
  ('TP01', 'Vé Vào Cổng Tham Quan', 'THAM_QUAN', 'VE01', NULL, 'Lượt qua cổng chính', 1),
  ('TP02', 'Cáp Treo Khứ Hồi', 'CAP_TREO', NULL, NULL, 'Tuyến cáp treo dài 2km', 1),
  ('TP03', 'Suất Buffet Trưa Tây Giang', 'AN_UONG', NULL, 'DV01', '1 suất ăn buffet trưa', 1),
  ('TP04', 'Xe Điện Nội Khu 1 Ngày', 'XE_DIEN', NULL, NULL, 'Di chuyển nội khu KDL', 1),
  ('TP05', 'Chèo Thuyền Kayak 60 Phút', 'KHAC', NULL, NULL, 'Chèo thuyền trên suối', 1),
  ('TP06', 'Vé Cổng Trẻ Em', 'THAM_QUAN', 'VE02', NULL, 'Qua cổng cho trẻ em', 1),
  ('TP07', 'Buffet Trưa Trẻ Em', 'AN_UONG', NULL, 'DV01', '1 suất buffet trưa trẻ em', 1),
  ('TP08', 'Hướng Dẫn Viên Riêng', 'KHAC', NULL, NULL, 'HDV thuyết minh toàn tuyến', 1),
  ('TP09', 'Buffet Tối BBQ Nướng', 'AN_UONG', NULL, 'DV02', 'Tiệc nướng BBQ', 1),
  ('TP10', 'Tắm Khoáng Nóng Rừng', 'KHAC', NULL, NULL, 'Ngâm khoáng nóng 45 phút', 1),
  ('TP11', 'Trượt Thác Cảm Giác Mạnh', 'KHAC', NULL, NULL, '1 lượt trượt thác nước', 1),
  ('TP12', 'Trà Chiều Bánh Ngọt', 'AN_UONG', NULL, 'DV15', 'Set trà thưởng ngoạn', 1),
  ('TP13', 'Nước Suối Đóng Chai Eco', 'KHAC', NULL, NULL, '2 chai nước suối', 1),
  ('TP14', 'Mũ Nón Lưu Niệm SkyGate', 'KHAC', NULL, NULL, 'Quà tặng lưu niệm', 1),
  ('TP15', 'Giao Lưu Lửa Trại Cổ Từ', 'KHAC', NULL, NULL, 'Giao lưu văn nghệ đêm', 1),
  ('TP16', 'Sân Cỏ Teambuilding', 'KHAC', NULL, NULL, 'Sân bãi tổ chức trò chơi', 1),
  ('TP17', 'Hồ Bơi Vô Cực Trên Núi', 'KHAC', NULL, NULL, 'Bơi không giới hạn', 1),
  ('TP18', 'Trekking Rừng Nguyên Sinh', 'THAM_QUAN', 'VE23', NULL, 'Trekking cùng HDV', 1),
  ('TP19', 'Rượu Cần Giao Lưu', 'AN_UONG', NULL, 'DV11', 'Uống rượu cần truyền thống', 1),
  ('TP20', 'Tham Quan Làng Nghề Cổ Từ', 'THAM_QUAN', 'VE21', NULL, 'Trải nghiệm dệt thổ cẩm', 1);

-- 15. COMBO_THANH_PHAN (20 bản ghi thành phần combo)
INSERT INTO `COMBO_THANH_PHAN` (`MA_LOAI_VE`, `MA_THANH_PHAN`, `SO_LUONG_MOI_COMBO`, `THEO_DOI_SU_DUNG`, `GHI_CHU`) VALUES
  ('VE03', 'TP01', 1, 1, 'Vé cổng người lớn'),
  ('VE03', 'TP02', 1, 1, 'Cáp treo khứ hồi'),
  ('VE03', 'TP03', 1, 1, 'Buffet trưa người lớn'),
  ('VE04', 'TP06', 1, 1, 'Vé cổng trẻ em'),
  ('VE04', 'TP02', 1, 1, 'Cáp treo trẻ em'),
  ('VE04', 'TP07', 1, 1, 'Buffet trưa trẻ em'),
  ('VE08', 'TP01', 1, 1, 'Vé cổng'),
  ('VE08', 'TP02', 1, 1, 'Cáp treo'),
  ('VE08', 'TP03', 1, 1, 'Buffet trưa VIP'),
  ('VE08', 'TP08', 1, 1, 'Hướng dẫn viên'),
  ('VE13', 'TP01', 2, 1, '2 vé cổng người lớn'),
  ('VE13', 'TP06', 1, 1, '1 vé cổng trẻ em'),
  ('VE13', 'TP03', 2, 1, '2 suất buffet người lớn'),
  ('VE13', 'TP07', 1, 1, '1 suất buffet trẻ em'),
  ('VE14', 'TP01', 2, 1, '2 vé người lớn'),
  ('VE14', 'TP06', 2, 1, '2 vé trẻ em'),
  ('VE17', 'TP02', 1, 1, 'Cáp treo'),
  ('VE17', 'TP03', 1, 1, 'Buffet trưa'),
  ('VE22', 'TP01', 1, 1, 'Vé cổng'),
  ('VE22', 'TP04', 1, 1, 'Xe điện 1 ngày');

-- 16. CHINH_SACH_COC & CHINH_SACH_HUY
INSERT INTO `CHINH_SACH_COC` (`MA_CHINH_SACH_COC`, `PHAM_VI_AP_DUNG`, `TY_LE_COC`, `TIEN_COC_CO_DINH`, `HIEU_LUC_TU`, `HIEU_LUC_DEN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('COC01', 'TOAN_BO', 30.00, NULL, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Đặt cọc 30% tổng giá trị đơn đặt'),
  ('COC02', 'PHONG', 50.00, NULL, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Đặt cọc 50% cho phòng vào dịp lễ tết'),
  ('COC03', 'AN_UONG', NULL, 500000.00, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Cọc cố định 500k cho bàn tiệc trên 10 người'),
  ('COC04', 'VE', 100.00, NULL, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Thanh toán 100% khi mua vé lẻ');

INSERT INTO `CHINH_SACH_HUY` (`MA_CHINH_SACH_HUY`, `TEN_CHINH_SACH`, `SO_NGAY_TU`, `SO_NGAY_DEN`, `HANH_DONG`, `TY_LE_HOAN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('HUY01', 'Hủy sớm trước 14 ngày', 14, NULL, 'HOAN_TIEN', 100.00, 1, 'Hoàn 100% tiền đã thanh toán'),
  ('HUY02', 'Hủy từ 7 đến 13 ngày', 7, 13, 'HOAN_TIEN', 70.00, 1, 'Hoàn 70% tổng tiền, giữ 30%'),
  ('HUY03', 'Hủy từ 3 đến 6 ngày', 3, 6, 'HOAN_TIEN', 50.00, 1, 'Hoàn 50% tổng tiền, giữ 50%'),
  ('HUY04', 'Hủy trong vòng 1 đến 2 ngày', 1, 2, 'MAT_COC', 0.00, 1, 'Mất toàn bộ tiền cọc / không hoàn tiền'),
  ('HUY05', 'Hủy trong ngày sử dụng', 0, 0, 'MAT_COC', 0.00, 1, 'Không được hoàn tiền trong ngày sử dụng');

-- 17. DON_DAT (25 bản ghi)
INSERT INTO `DON_DAT` (`MA_DON_DAT`, `MA_DON_HANG`, `MA_NGUOI_DUNG`, `HO_TEN_KHACH`, `SDT_KHACH`, `EMAIL_KHACH`, `GHI_CHU`, `THOI_GIAN_DAT`, `TONG_TIEN`, `TIEN_COC_YEU_CAU`, `MA_QR`, `TRANG_THAI_DON`, `TRANG_THAI_THANH_TOAN`) VALUES
  ('DD001', 'CTDG-260501-0001', 'ND008', 'Trần Anh Tuấn', '0912345008', 'khach08@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-01 08:30:00', 1900000, 570000, 'QR-CTDG-0001', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD002', 'CTDG-260501-0002', 'ND009', 'Nguyễn Minh Châu', '0912345009', 'khach09@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-01 09:15:00', 3250000, 975000, 'QR-CTDG-0002', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD003', 'CTDG-260502-0003', 'ND010', 'Võ Văn Kiệt', '0912345010', 'khach10@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-02 10:00:00', 4600000, 1380000, 'QR-CTDG-0003', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD004', 'CTDG-260502-0004', 'ND011', 'Đặng Thùy Tiên', '0912345011', 'khach11@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-02 11:20:00', 1450000, 435000, 'QR-CTDG-0004', 'CHO_XAC_NHAN', 'CHUA_THANH_TOAN'),
  ('DD005', 'CTDG-260503-0005', 'ND012', 'Bùi Đức Thịnh', '0912345012', 'khach12@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-03 14:10:00', 1900000, 570000, 'QR-CTDG-0005', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD006', 'CTDG-260503-0006', 'ND013', 'Hoàng Mỹ Linh', '0912345013', 'khach13@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-03 15:45:00', 3300000, 990000, 'QR-CTDG-0006', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD007', 'CTDG-260504-0007', 'ND014', 'Ngô Quốc Bảo', '0912345014', 'khach14@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-04 08:00:00', 0, 0, 'QR-CTDG-0007', 'DA_HUY', 'DA_HOAN_TIEN'),
  ('DD008', 'CTDG-260504-0008', 'ND015', 'Dương Cẩm Tú', '0912345015', 'khach15@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-04 10:30:00', 1200000, 360000, 'QR-CTDG-0008', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD009', 'CTDG-260505-0009', 'ND016', 'Trương Gia Bình', '0912345016', 'khach16@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-05 13:20:00', 5500000, 1650000, 'QR-CTDG-0009', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD010', 'CTDG-260505-0010', 'ND017', 'Đinh Tiến Dũng', '0912345017', 'khach17@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-05 16:00:00', 1640000, 492000, 'QR-CTDG-0010', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD011', 'CTDG-260506-0011', 'ND018', 'Phan Thanh Hậu', '0912345018', 'khach18@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-06 09:00:00', 1750000, 525000, 'QR-CTDG-0011', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD012', 'CTDG-260506-0012', 'ND019', 'Vũ Hoàng Yến', '0912345019', 'khach19@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-06 11:40:00', 2400000, 720000, 'QR-CTDG-0012', 'CHO_XAC_NHAN', 'CHUA_THANH_TOAN'),
  ('DD013', 'CTDG-260507-0013', 'ND020', 'Lâm Trường An', '0912345020', 'khach20@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-07 14:00:00', 1260000, 378000, 'QR-CTDG-0013', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD014', 'CTDG-260507-0014', 'ND021', 'Đoàn Ngọc Hải', '0912345021', 'khach21@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-07 15:30:00', 0, 0, 'QR-CTDG-0014', 'DA_HUY', 'DA_HOAN_TIEN'),
  ('DD015', 'CTDG-260508-0015', 'ND022', 'Nguyễn Hữu Thọ', '0912345022', 'khach22@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-08 08:45:00', 2100000, 630000, 'QR-CTDG-0015', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD016', 'CTDG-260508-0016', 'ND023', 'Trịnh Công Sơn', '0912345023', 'khach23@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-08 10:15:00', 5200000, 1560000, 'QR-CTDG-0016', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD017', 'CTDG-260509-0017', 'ND024', 'Hà Kiều Anh', '0912345024', 'khach24@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-09 13:00:00', 1500000, 450000, 'QR-CTDG-0017', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD018', 'CTDG-260509-0018', 'ND025', 'Lương Thế Vinh', '0912345025', 'khach25@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-09 16:20:00', 2850000, 855000, 'QR-CTDG-0018', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD019', 'CTDG-260510-0019', 'ND008', 'Trần Anh Tuấn', '0912345008', 'khach08@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-10 09:30:00', 1900000, 570000, 'QR-CTDG-0019', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD020', 'CTDG-260510-0020', 'ND009', 'Nguyễn Minh Châu', '0912345009', 'khach09@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-10 11:00:00', 1800000, 540000, 'QR-CTDG-0020', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD021', 'CTDG-260511-0021', 'ND010', 'Võ Văn Kiệt', '0912345010', 'khach10@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-11 14:00:00', 1180000, 354000, 'QR-CTDG-0021', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD022', 'CTDG-260511-0022', 'ND011', 'Đặng Thùy Tiên', '0912345011', 'khach11@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-11 15:50:00', 0, 0, 'QR-CTDG-0022', 'DA_HUY', 'DA_HOAN_TIEN'),
  ('DD023', 'CTDG-260512-0023', 'ND012', 'Bùi Đức Thịnh', '0912345012', 'khach12@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-12 08:30:00', 2600000, 780000, 'QR-CTDG-0023', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD024', 'CTDG-260512-0024', 'ND013', 'Hoàng Mỹ Linh', '0912345013', 'khach13@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-12 10:45:00', 1360000, 408000, 'QR-CTDG-0024', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD025', 'CTDG-260513-0025', 'ND014', 'Ngô Quốc Bảo', '0912345014', 'khach14@gmail.com', 'Đặt trực tuyến trên SkyGateHub portal', '2026-05-13 13:15:00', 2500000, 750000, 'QR-CTDG-0025', 'DA_HOAN_THANH', 'DA_THANH_TOAN');

-- 18. CHI_TIET_DON (40 bản ghi)
-- Chú ý: THANH_TIEN là cột tính toán GENERATED STORED -> KHÔNG đưa vào danh sách cột INSERT
INSERT INTO `CHI_TIET_DON` (`MA_CHI_TIET`, `MA_DON_DAT`, `LOAI_DICH_VU`, `MA_LOAI_VE`, `MA_LOAI_PHONG`, `MA_DV_AN_UONG`, `TEN_SAN_PHAM`, `SO_LUONG`, `DON_GIA`, `TRANG_THAI`, `NGAY_SU_DUNG`) VALUES
  ('CT001', 'DD001', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 2, 350000, 'HIEU_LUC', '2026-05-02'),
  ('CT002', 'DD001', 'PHONG', NULL, 1, NULL, 'Superior DBL (FIVITEL Sông Ngân)', 1, 1200000, 'HIEU_LUC', '2026-05-02'),
  ('CT003', 'DD002', 'VE', 'VE03', NULL, NULL, 'Combo Cổng Trời 1 Ngày Người Lớn', 4, 650000, 'HIEU_LUC', '2026-05-05'),
  ('CT004', 'DD002', 'AN_UONG', NULL, NULL, 'DV03', 'Set Menu Cơm Lam Gà Nướng 4 Người', 1, 650000, 'HIEU_LUC', '2026-05-05'),
  ('CT005', 'DD003', 'PHONG', NULL, 3, NULL, 'Deluxe DBL (FIVITEL Sông Ngân)', 2, 1600000, 'HIEU_LUC', '2026-05-10'),
  ('CT006', 'DD003', 'AN_UONG', NULL, NULL, 'DV02', 'Buffet Tối Nướng BBQ Cao Nguyên', 4, 350000, 'HIEU_LUC', '2026-05-10'),
  ('CT007', 'DD004', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 3, 350000, 'HIEU_LUC', '2026-05-12'),
  ('CT008', 'DD004', 'VE', 'VE02', NULL, NULL, 'Vé Tham Quan Trẻ Em', 2, 200000, 'HIEU_LUC', '2026-05-12'),
  ('CT009', 'DD005', 'VE', 'VE08', NULL, NULL, 'Combo Trải Nghiệm Sinh Thái VIP', 2, 950000, 'HIEU_LUC', '2026-05-08'),
  ('CT010', 'DD006', 'PHONG', NULL, 5, NULL, 'Suite DBL (FIVITEL Sông Ngân)', 1, 2800000, 'HIEU_LUC', '2026-05-15'),
  ('CT011', 'DD006', 'AN_UONG', NULL, NULL, 'DV01', 'Buffet Trưa Ẩm Thực Cổ Từ', 2, 250000, 'HIEU_LUC', '2026-05-15'),
  ('CT012', 'DD007', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 2, 350000, 'DA_HUY', '2026-05-20'),
  ('CT013', 'DD007', 'PHONG', NULL, 1, NULL, 'Superior DBL (FIVITEL Sông Ngân)', 1, 1200000, 'DA_HUY', '2026-05-20'),
  ('CT014', 'DD008', 'VE', 'VE13', NULL, NULL, 'Combo Gia Đình 2 Lớn 1 Trẻ', 1, 1200000, 'HIEU_LUC', '2026-05-09'),
  ('CT015', 'DD009', 'PHONG', NULL, 9, NULL, 'Vip DBL (FIVITEL Cầu Kiều)', 1, 4500000, 'HIEU_LUC', '2026-05-18'),
  ('CT016', 'DD009', 'VE', 'VE05', NULL, NULL, 'Vé Cáp Treo Người Lớn', 4, 250000, 'HIEU_LUC', '2026-05-18'),
  ('CT017', 'DD010', 'AN_UONG', NULL, NULL, 'DV05', 'Set Menu Thịt Lợn Bản 7 Món', 2, 820000, 'HIEU_LUC', '2026-05-16'),
  ('CT018', 'DD011', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 5, 350000, 'HIEU_LUC', '2026-05-11'),
  ('CT019', 'DD012', 'PHONG', NULL, 6, NULL, 'Family DBL + Single (FIVITEL Sông Ngân)', 1, 2400000, 'HIEU_LUC', '2026-05-22'),
  ('CT020', 'DD013', 'VE', 'VE17', NULL, NULL, 'Combo Cáp Treo Và Buffet', 2, 520000, 'HIEU_LUC', '2026-05-14'),
  ('CT021', 'DD013', 'AN_UONG', NULL, NULL, 'DV11', 'Rượu Cần Cổ Từ Đặc Biệt', 1, 220000, 'HIEU_LUC', '2026-05-14'),
  ('CT022', 'DD014', 'PHONG', NULL, 15, NULL, 'Nhà Moong DBL/Bungalow', 2, 850000, 'DA_HUY', '2026-05-25'),
  ('CT023', 'DD015', 'VE', 'VE03', NULL, NULL, 'Combo Cổng Trời 1 Ngày Người Lớn', 3, 650000, 'HIEU_LUC', '2026-05-13'),
  ('CT024', 'DD015', 'VE', 'VE07', NULL, NULL, 'Vé Xe Điện Nội Khu', 3, 50000, 'HIEU_LUC', '2026-05-13'),
  ('CT025', 'DD016', 'PHONG', NULL, 8, NULL, 'Deluxe DBL (FIVITEL Cầu Kiều)', 1, 5200000, 'HIEU_LUC', '2026-05-30'),
  ('CT026', 'DD017', 'AN_UONG', NULL, NULL, 'DV01', 'Buffet Trưa Ẩm Thực Cổ Từ', 6, 250000, 'HIEU_LUC', '2026-05-17'),
  ('CT027', 'DD018', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 4, 350000, 'HIEU_LUC', '2026-05-19'),
  ('CT028', 'DD018', 'VE', 'VE05', NULL, NULL, 'Vé Cáp Treo Người Lớn', 4, 250000, 'HIEU_LUC', '2026-05-19'),
  ('CT029', 'DD018', 'AN_UONG', NULL, NULL, 'DV06', 'Lẩu Gà Lá É Tây Giang', 1, 450000, 'HIEU_LUC', '2026-05-19'),
  ('CT030', 'DD019', 'PHONG', NULL, 10, NULL, 'Standard DBL (FIVITEL Ô Thước)', 2, 950000, 'HIEU_LUC', '2026-05-24'),
  ('CT031', 'DD020', 'VE', 'VE25', NULL, NULL, 'Combo All-Inclusive Siêu VIP', 1, 1800000, 'HIEU_LUC', '2026-05-21'),
  ('CT032', 'DD021', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 2, 350000, 'HIEU_LUC', '2026-05-23'),
  ('CT033', 'DD021', 'AN_UONG', NULL, NULL, 'DV04', 'Set Menu Cá Niên Rau Rừng', 1, 480000, 'HIEU_LUC', '2026-05-23'),
  ('CT034', 'DD022', 'VE', 'VE03', NULL, NULL, 'Combo Cổng Trời 1 Ngày Người Lớn', 2, 650000, 'DA_HUY', '2026-05-28'),
  ('CT035', 'DD023', 'PHONG', NULL, 11, NULL, 'Superior DBL (FIVITEL Ô Thước)', 1, 1100000, 'HIEU_LUC', '2026-05-26'),
  ('CT036', 'DD023', 'AN_UONG', NULL, NULL, 'DV14', 'Set Tiệc Nướng Sân Vườn VIP', 1, 1500000, 'HIEU_LUC', '2026-05-26'),
  ('CT037', 'DD024', 'VE', 'VE09', NULL, NULL, 'Vé Tham Quan Suối Hoa Người Lớn', 4, 280000, 'HIEU_LUC', '2026-05-29'),
  ('CT038', 'DD024', 'VE', 'VE11', NULL, NULL, 'Vé Chèo Thuyền Kayak Cây Xanh', 2, 120000, 'HIEU_LUC', '2026-05-29'),
  ('CT039', 'DD025', 'VE', 'VE01', NULL, NULL, 'Vé Tham Quan Người Lớn', 2, 350000, 'HIEU_LUC', '2026-05-27'),
  ('CT040', 'DD025', 'PHONG', NULL, 13, NULL, 'Deluxe DBL (FIVITEL Ô Thước)', 1, 1800000, 'HIEU_LUC', '2026-05-27');

-- 19. VE (25 vé phát hành)
INSERT INTO `VE` (`MA_VE`, `MA_DON_DAT`, `MA_CHI_TIET`, `MA_LOAI_VE`, `MA_VE_CHUOI`, `NGAY_THAM_QUAN`, `TRANG_THAI`, `THOI_GIAN_SU_DUNG`) VALUES
  ('VE_TK001', 'DD001', 'CT001', 'VE01', 'TK-2026-0001', '2026-05-02', 'DA_SU_DUNG', '2026-05-02 08:35:00'),
  ('VE_TK002', 'DD001', 'CT001', 'VE01', 'TK-2026-0002', '2026-05-02', 'DA_SU_DUNG', '2026-05-02 08:36:00'),
  ('VE_TK003', 'DD002', 'CT003', 'VE03', 'TK-2026-0003', '2026-05-05', 'CHUA_SU_DUNG', NULL),
  ('VE_TK004', 'DD002', 'CT003', 'VE03', 'TK-2026-0004', '2026-05-05', 'CHUA_SU_DUNG', NULL),
  ('VE_TK005', 'DD002', 'CT003', 'VE03', 'TK-2026-0005', '2026-05-05', 'CHUA_SU_DUNG', NULL),
  ('VE_TK006', 'DD002', 'CT003', 'VE03', 'TK-2026-0006', '2026-05-05', 'CHUA_SU_DUNG', NULL),
  ('VE_TK007', 'DD004', 'CT007', 'VE01', 'TK-2026-0007', '2026-05-12', 'CHUA_SU_DUNG', NULL),
  ('VE_TK008', 'DD004', 'CT007', 'VE01', 'TK-2026-0008', '2026-05-12', 'CHUA_SU_DUNG', NULL),
  ('VE_TK009', 'DD004', 'CT007', 'VE01', 'TK-2026-0009', '2026-05-12', 'CHUA_SU_DUNG', NULL),
  ('VE_TK010', 'DD004', 'CT008', 'VE02', 'TK-2026-0010', '2026-05-12', 'CHUA_SU_DUNG', NULL),
  ('VE_TK011', 'DD004', 'CT008', 'VE02', 'TK-2026-0011', '2026-05-12', 'CHUA_SU_DUNG', NULL),
  ('VE_TK012', 'DD005', 'CT009', 'VE08', 'TK-2026-0012', '2026-05-08', 'DA_SU_DUNG', '2026-05-08 09:10:00'),
  ('VE_TK013', 'DD005', 'CT009', 'VE08', 'TK-2026-0013', '2026-05-08', 'DA_SU_DUNG', '2026-05-08 09:11:00'),
  ('VE_TK014', 'DD007', 'CT012', 'VE01', 'TK-2026-0014', '2026-05-20', 'DA_HUY', NULL),
  ('VE_TK015', 'DD007', 'CT012', 'VE01', 'TK-2026-0015', '2026-05-20', 'DA_HUY', NULL),
  ('VE_TK016', 'DD008', 'CT014', 'VE13', 'TK-2026-0016', '2026-05-09', 'DA_SU_DUNG', '2026-05-09 08:50:00'),
  ('VE_TK017', 'DD009', 'CT016', 'VE05', 'TK-2026-0017', '2026-05-18', 'DA_SU_DUNG', '2026-05-18 10:20:00'),
  ('VE_TK018', 'DD009', 'CT016', 'VE05', 'TK-2026-0018', '2026-05-18', 'DA_SU_DUNG', '2026-05-18 10:21:00'),
  ('VE_TK019', 'DD009', 'CT016', 'VE05', 'TK-2026-0019', '2026-05-18', 'DA_SU_DUNG', '2026-05-18 10:22:00'),
  ('VE_TK020', 'DD009', 'CT016', 'VE05', 'TK-2026-0020', '2026-05-18', 'DA_SU_DUNG', '2026-05-18 10:23:00'),
  ('VE_TK021', 'DD011', 'CT018', 'VE01', 'TK-2026-0021', '2026-05-11', 'DA_SU_DUNG', '2026-05-11 09:00:00'),
  ('VE_TK022', 'DD011', 'CT018', 'VE01', 'TK-2026-0022', '2026-05-11', 'DA_SU_DUNG', '2026-05-11 09:01:00'),
  ('VE_TK023', 'DD013', 'CT020', 'VE17', 'TK-2026-0023', '2026-05-14', 'DA_SU_DUNG', '2026-05-14 09:15:00'),
  ('VE_TK024', 'DD013', 'CT020', 'VE17', 'TK-2026-0024', '2026-05-14', 'DA_SU_DUNG', '2026-05-14 09:16:00'),
  ('VE_TK025', 'DD015', 'CT023', 'VE03', 'TK-2026-0025', '2026-05-13', 'DA_SU_DUNG', '2026-05-13 08:40:00');

-- 20. DAT_PHONG (22 bản ghi)
INSERT INTO `DAT_PHONG` (`MA_DAT_PHONG`, `MA_DON_DAT`, `MA_CHI_TIET`, `MA_LOAI_PHONG`, `NGAY_NHAN_PHONG`, `NGAY_TRA_PHONG`, `SO_LUONG_PHONG`, `SO_KHACH`, `TRANG_THAI`) VALUES
  ('DP001', 'DD001', 'CT002', 1, '2026-05-02', '2026-05-03', 1, 2, 'DA_TRA_PHONG'),
  ('DP002', 'DD003', 'CT005', 3, '2026-05-10', '2026-05-12', 2, 4, 'DA_NHAN_PHONG'),
  ('DP003', 'DD006', 'CT010', 5, '2026-05-15', '2026-05-17', 1, 2, 'DA_XAC_NHAN'),
  ('DP004', 'DD007', 'CT013', 1, '2026-05-20', '2026-05-22', 1, 2, 'DA_HUY'),
  ('DP005', 'DD009', 'CT015', 9, '2026-05-18', '2026-05-20', 1, 4, 'DA_TRA_PHONG'),
  ('DP006', 'DD012', 'CT019', 6, '2026-05-22', '2026-05-24', 1, 4, 'CHO_XAC_NHAN'),
  ('DP007', 'DD014', 'CT022', 15, '2026-05-25', '2026-05-26', 2, 4, 'DA_HUY'),
  ('DP008', 'DD016', 'CT025', 8, '2026-05-30', '2026-06-01', 1, 3, 'DA_XAC_NHAN'),
  ('DP009', 'DD019', 'CT030', 10, '2026-05-24', '2026-05-26', 2, 4, 'DA_XAC_NHAN'),
  ('DP010', 'DD023', 'CT035', 11, '2026-05-26', '2026-05-27', 1, 2, 'DA_TRA_PHONG'),
  ('DP011', 'DD025', 'CT040', 13, '2026-05-27', '2026-05-29', 1, 4, 'DA_TRA_PHONG'),
  ('DP012', 'DD001', 'CT002', 1, '2026-06-01', '2026-06-03', 1, 2, 'DA_XAC_NHAN'),
  ('DP013', 'DD003', 'CT005', 3, '2026-06-05', '2026-06-07', 1, 2, 'DA_XAC_NHAN'),
  ('DP014', 'DD006', 'CT010', 5, '2026-06-08', '2026-06-10', 1, 2, 'DA_XAC_NHAN'),
  ('DP015', 'DD009', 'CT015', 9, '2026-06-11', '2026-06-13', 1, 4, 'DA_XAC_NHAN'),
  ('DP016', 'DD016', 'CT025', 8, '2026-06-15', '2026-06-17', 1, 2, 'DA_XAC_NHAN'),
  ('DP017', 'DD019', 'CT030', 10, '2026-06-18', '2026-06-20', 1, 2, 'DA_XAC_NHAN'),
  ('DP018', 'DD023', 'CT035', 11, '2026-06-21', '2026-06-22', 1, 2, 'DA_XAC_NHAN'),
  ('DP019', 'DD025', 'CT040', 13, '2026-06-25', '2026-06-27', 1, 4, 'DA_XAC_NHAN'),
  ('DP020', 'DD001', 'CT002', 1, '2026-07-01', '2026-07-03', 1, 2, 'DA_XAC_NHAN'),
  ('DP021', 'DD003', 'CT005', 3, '2026-07-05', '2026-07-07', 1, 2, 'DA_XAC_NHAN'),
  ('DP022', 'DD006', 'CT010', 5, '2026-07-10', '2026-07-12', 1, 2, 'DA_XAC_NHAN');

-- 21. PHAN_PHONG (22 bản ghi phân bổ phòng)
INSERT INTO `PHAN_PHONG` (`MA_PHAN_PHONG`, `MA_DAT_PHONG`, `MA_LOAI_PHONG`, `MA_PHONG`, `MA_NHAN_VIEN`, `THOI_GIAN_PHAN`, `TRANG_THAI`) VALUES
  ('PP001', 'DP001', 1, 'P001', 'ND004', '2026-05-01 10:00:00', 'DA_TRA_PHONG'),
  ('PP002', 'DP002', 3, 'P002', 'ND004', '2026-05-01 10:00:00', 'DA_NHAN_PHONG'),
  ('PP003', 'DP003', 5, 'P003', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP004', 'DP004', 1, 'P004', 'ND004', '2026-05-01 10:00:00', 'DA_HUY'),
  ('PP005', 'DP005', 9, 'P005', 'ND004', '2026-05-01 10:00:00', 'DA_TRA_PHONG'),
  ('PP006', 'DP006', 6, 'P006', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP007', 'DP007', 15, 'P007', 'ND004', '2026-05-01 10:00:00', 'DA_HUY'),
  ('PP008', 'DP008', 8, 'P008', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP009', 'DP009', 10, 'P009', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP010', 'DP010', 11, 'P010', 'ND004', '2026-05-01 10:00:00', 'DA_TRA_PHONG'),
  ('PP011', 'DP011', 13, 'P011', 'ND004', '2026-05-01 10:00:00', 'DA_TRA_PHONG'),
  ('PP012', 'DP012', 1, 'P012', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP013', 'DP013', 3, 'P013', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP014', 'DP014', 5, 'P014', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP015', 'DP015', 9, 'P015', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP016', 'DP016', 8, 'P016', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP017', 'DP017', 10, 'P017', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP018', 'DP018', 11, 'P018', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP019', 'DP019', 13, 'P019', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP020', 'DP020', 1, 'P020', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP021', 'DP021', 3, 'P021', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN'),
  ('PP022', 'DP022', 5, 'P022', 'ND004', '2026-05-01 10:00:00', 'DANG_PHAN');

-- 22. QUYEN_LOI_DON (25 bản ghi chụp quyền lợi combo)
INSERT INTO `QUYEN_LOI_DON` (`MA_QUYEN_LOI`, `MA_DON_DAT`, `MA_CHI_TIET`, `MA_THANH_PHAN`, `TEN_THANH_PHAN_CHUP`, `LOAI_THANH_PHAN_CHUP`, `SO_LUONG_TOI_DA`, `SO_LUONG_DA_DUNG`, `THEO_DOI_SU_DUNG`, `TRANG_THAI`) VALUES
  ('QL001', 'DD002', 'CT003', 'TP01', 'Vé Vào Cổng Tham Quan', 'THAM_QUAN', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL002', 'DD002', 'CT003', 'TP02', 'Cáp Treo Khứ Hồi', 'CAP_TREO', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL003', 'DD002', 'CT003', 'TP03', 'Suất Buffet Trưa Tây Giang', 'AN_UONG', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL004', 'DD005', 'CT009', 'TP01', 'Vé Vào Cổng Tham Quan', 'THAM_QUAN', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL005', 'DD005', 'CT009', 'TP02', 'Cáp Treo Khứ Hồi', 'CAP_TREO', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL006', 'DD005', 'CT009', 'TP03', 'Suất Buffet Trưa VIP', 'AN_UONG', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL007', 'DD005', 'CT009', 'TP08', 'Hướng Dẫn Viên Riêng', 'KHAC', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL008', 'DD008', 'CT014', 'TP01', 'Vé Vào Cổng 2 Người Lớn', 'THAM_QUAN', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL009', 'DD008', 'CT014', 'TP06', 'Vé Cổng Trẻ Em', 'THAM_QUAN', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL010', 'DD008', 'CT014', 'TP03', 'Buffet Trưa Người Lớn', 'AN_UONG', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL011', 'DD008', 'CT014', 'TP07', 'Buffet Trưa Trẻ Em', 'AN_UONG', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL012', 'DD013', 'CT020', 'TP02', 'Cáp Treo Khứ Hồi', 'CAP_TREO', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL013', 'DD013', 'CT020', 'TP03', 'Buffet Trưa', 'AN_UONG', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL014', 'DD015', 'CT023', 'TP01', 'Vé Cổng Người Lớn', 'THAM_QUAN', 3, 3, 1, 'DA_DUNG_HET'),
  ('QL015', 'DD015', 'CT023', 'TP02', 'Cáp Treo Khứ Hồi', 'CAP_TREO', 3, 3, 1, 'DA_DUNG_HET'),
  ('QL016', 'DD015', 'CT023', 'TP03', 'Buffet Trưa', 'AN_UONG', 3, 3, 1, 'DA_DUNG_HET'),
  ('QL017', 'DD020', 'CT031', 'TP01', 'Vé Cổng VIP', 'THAM_QUAN', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL018', 'DD020', 'CT031', 'TP02', 'Cáp Treo VIP', 'CAP_TREO', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL019', 'DD020', 'CT031', 'TP03', 'Buffet Trưa VIP', 'AN_UONG', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL020', 'DD020', 'CT031', 'TP04', 'Xe Điện Không Giới Hạn', 'XE_DIEN', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL021', 'DD022', 'CT034', 'TP01', 'Vé Cổng Người Lớn', 'THAM_QUAN', 2, 0, 1, 'DA_HUY'),
  ('QL022', 'DD022', 'CT034', 'TP02', 'Cáp Treo Khứ Hồi', 'CAP_TREO', 2, 0, 1, 'DA_HUY'),
  ('QL023', 'DD022', 'CT034', 'TP03', 'Buffet Trưa', 'AN_UONG', 2, 0, 1, 'DA_HUY'),
  ('QL024', 'DD002', 'CT003', 'TP04', 'Xe Điện Nội Khu', 'XE_DIEN', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL025', 'DD015', 'CT024', 'TP04', 'Xe Điện Nội Khu', 'XE_DIEN', 3, 3, 1, 'DA_DUNG_HET');

-- 23. DAT_BAN (25 bản ghi đặt bàn)
INSERT INTO `DAT_BAN` (`MA_DAT_BAN`, `MA_DON_DAT`, `MA_DICH_VU`, `MA_CA_AN`, `NGAY_AN`, `SO_KHACH`, `MA_BAN_AN`, `MA_CHI_TIET`, `MA_QUYEN_LOI`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('DB001', 'DD002', 'DV03', 'CA_TRUA', '2026-05-05', 4, 'BA01', 'CT004', NULL, 'DA_HOAN_THANH', 'Bàn cạnh cửa sổ'),
  ('DB002', 'DD003', 'DV02', 'CA_TOI', '2026-05-10', 4, 'BA02', 'CT006', NULL, 'DA_HOAN_THANH', 'Tiệc nướng ngoài trời'),
  ('DB003', 'DD006', 'DV01', 'CA_TRUA', '2026-05-15', 2, 'BA03', 'CT011', NULL, 'DA_XAC_NHAN', 'Bàn 2 người ấm cúng'),
  ('DB004', 'DD010', 'DV05', 'CA_TRUA', '2026-05-16', 8, 'BA04', 'CT017', NULL, 'DA_XAC_NHAN', 'Đoàn 8 người'),
  ('DB005', 'DD013', 'DV11', 'CA_TOI', '2026-05-14', 2, 'BA05', 'CT021', NULL, 'DA_HOAN_THANH', 'Thưởng thức rượu cần'),
  ('DB006', 'DD017', 'DV01', 'CA_TRUA', '2026-05-17', 6, 'BA06', 'CT026', NULL, 'DA_HOAN_THANH', 'Buffet gia đình'),
  ('DB007', 'DD018', 'DV06', 'CA_TOI', '2026-05-19', 4, 'BA07', 'CT029', NULL, 'DA_HOAN_THANH', 'Lẩu gà nóng'),
  ('DB008', 'DD021', 'DV04', 'CA_TRUA', '2026-05-23', 2, 'BA08', 'CT033', NULL, 'DA_HOAN_THANH', 'Cá niên nướng'),
  ('DB009', 'DD023', 'DV14', 'CA_TOI', '2026-05-26', 2, 'BA09', 'CT036', NULL, 'DA_HOAN_THANH', 'BBQ sân vườn'),
  ('DB10', 'DD002', 'DV01', 'CA_TRUA', '2026-05-05', 4, 'BA10', NULL, 'QL003', 'DA_HOAN_THANH', 'Buffet từ combo QL003'),
  ('DB11', 'DD005', 'DV01', 'CA_TRUA', '2026-05-08', 2, 'BA11', NULL, 'QL006', 'DA_HOAN_THANH', 'Buffet VIP từ combo'),
  ('DB12', 'DD008', 'DV01', 'CA_TRUA', '2026-05-09', 3, 'BA12', NULL, 'QL010', 'DA_HOAN_THANH', 'Buffet gia đình từ combo'),
  ('DB13', 'DD013', 'DV01', 'CA_TRUA', '2026-05-14', 2, 'BA13', NULL, 'QL013', 'DA_HOAN_THANH', 'Buffet từ combo'),
  ('DB14', 'DD015', 'DV01', 'CA_TRUA', '2026-05-13', 3, 'BA14', NULL, 'QL016', 'DA_HOAN_THANH', 'Buffet từ combo'),
  ('DB15', 'DD020', 'DV01', 'CA_TRUA', '2026-05-21', 1, 'BA15', NULL, 'QL019', 'DA_HOAN_THANH', 'Buffet All-in'),
  ('DB16', 'DD002', 'DV03', 'CA_TRUA', '2026-06-05', 4, 'BA16', 'CT004', NULL, 'DA_XAC_NHAN', 'Bàn đặt trước tháng 6'),
  ('DB17', 'DD003', 'DV02', 'CA_TOI', '2026-06-10', 4, 'BA17', 'CT006', NULL, 'DA_XAC_NHAN', 'Bàn tiệc nướng'),
  ('DB18', 'DD006', 'DV01', 'CA_TRUA', '2026-06-15', 2, 'BA18', 'CT011', NULL, 'DA_XAC_NHAN', 'Đặt trước trưa'),
  ('DB19', 'DD010', 'DV05', 'CA_TRUA', '2026-06-16', 4, 'BA19', 'CT017', NULL, 'DA_XAC_NHAN', 'Đặt trước lớn bàn'),
  ('DB20', 'DD013', 'DV11', 'CA_TOI', '2026-06-14', 2, 'BA20', 'CT021', NULL, 'DA_XAC_NHAN', 'Rượu cần tối'),
  ('DB21', 'DD017', 'DV01', 'CA_TRUA', '2026-06-17', 6, 'BA21', 'CT026', NULL, 'DA_XAC_NHAN', 'Đoàn 6 người'),
  ('DB22', 'DD018', 'DV06', 'CA_TOI', '2026-06-19', 4, 'BA22', 'CT029', NULL, 'DA_XAC_NHAN', 'Lẩu gà lá é'),
  ('DB23', 'DD021', 'DV04', 'CA_TRUA', '2026-06-23', 2, 'BA23', 'CT033', NULL, 'DA_XAC_NHAN', 'Cá niên bữa trưa'),
  ('DB24', 'DD023', 'DV14', 'CA_TOI', '2026-06-26', 2, 'BA24', 'CT036', NULL, 'DA_XAC_NHAN', 'BBQ ngoài trời'),
  ('DB25', 'DD007', 'DV01', 'CA_TRUA', '2026-05-20', 2, NULL, 'CT011', NULL, 'DA_HUY', 'Đặt bàn đã hủy');

-- 24. THANH_TOAN (30 bản ghi giao dịch thanh toán)
INSERT INTO `THANH_TOAN` (`MA_THANH_TOAN`, `MA_DON_DAT`, `LOAI_GIAO_DICH`, `PHUONG_THUC_THANH_TOAN`, `SO_TIEN`, `MA_GIAO_DICH`, `TRANG_THAI`, `THOI_GIAN_TAO`, `THOI_GIAN_THANH_TOAN`) VALUES
  ('TT001', 'DD001', 'THANH_TOAN_DU', 'VNPAY', 1900000, 'VNP-20260501-001', 'DA_THANH_TOAN', '2026-05-01 08:31:00', '2026-05-01 08:32:00'),
  ('TT002', 'DD002', 'DAT_COC', 'MOMO', 975000, 'MOM-20260501-002', 'DA_THANH_TOAN', '2026-05-01 09:16:00', '2026-05-01 09:17:00'),
  ('TT003', 'DD003', 'THANH_TOAN_DU', 'CHUYEN_KHOAN', 4600000, 'MB-20260502-003', 'DA_THANH_TOAN', '2026-05-02 10:02:00', '2026-05-02 10:05:00'),
  ('TT004', 'DD005', 'THANH_TOAN_DU', 'VNPAY', 1900000, 'VNP-20260503-004', 'DA_THANH_TOAN', '2026-05-03 14:12:00', '2026-05-03 14:13:00'),
  ('TT005', 'DD006', 'DAT_COC', 'VNPAY', 990000, 'VNP-20260503-005', 'DA_THANH_TOAN', '2026-05-03 15:47:00', '2026-05-03 15:48:00'),
  ('TT006', 'DD007', 'THANH_TOAN_DU', 'VNPAY', 1900000, 'VNP-20260504-006', 'DA_THANH_TOAN', '2026-05-04 08:05:00', '2026-05-04 08:06:00'),
  ('TT007', 'DD008', 'THANH_TOAN_DU', 'MOMO', 1200000, 'MOM-20260504-007', 'DA_THANH_TOAN', '2026-05-04 10:32:00', '2026-05-04 10:33:00'),
  ('TT008', 'DD009', 'THANH_TOAN_DU', 'CHUYEN_KHOAN', 5500000, 'VCB-20260505-008', 'DA_THANH_TOAN', '2026-05-05 13:22:00', '2026-05-05 13:25:00'),
  ('TT009', 'DD010', 'DAT_COC', 'TIEN_MAT', 492000, 'TM-20260505-009', 'DA_THANH_TOAN', '2026-05-05 16:05:00', '2026-05-05 16:05:00'),
  ('TT010', 'DD011', 'THANH_TOAN_DU', 'VNPAY', 1750000, 'VNP-20260506-010', 'DA_THANH_TOAN', '2026-05-06 09:02:00', '2026-05-06 09:03:00'),
  ('TT011', 'DD013', 'THANH_TOAN_DU', 'MOMO', 1260000, 'MOM-20260507-011', 'DA_THANH_TOAN', '2026-05-07 14:02:00', '2026-05-07 14:03:00'),
  ('TT012', 'DD014', 'THANH_TOAN_DU', 'VNPAY', 1700000, 'VNP-20260507-012', 'DA_THANH_TOAN', '2026-05-07 15:32:00', '2026-05-07 15:33:00'),
  ('TT013', 'DD015', 'THANH_TOAN_DU', 'VNPAY', 2100000, 'VNP-20260508-013', 'DA_THANH_TOAN', '2026-05-08 08:47:00', '2026-05-08 08:48:00'),
  ('TT014', 'DD016', 'DAT_COC', 'CHUYEN_KHOAN', 1560000, 'TCB-20260508-014', 'DA_THANH_TOAN', '2026-05-08 10:18:00', '2026-05-08 10:20:00'),
  ('TT015', 'DD017', 'THANH_TOAN_DU', 'TIEN_MAT', 1500000, 'TM-20260509-015', 'DA_THANH_TOAN', '2026-05-09 13:05:00', '2026-05-09 13:05:00'),
  ('TT016', 'DD018', 'THANH_TOAN_DU', 'VNPAY', 2850000, 'VNP-20260509-016', 'DA_THANH_TOAN', '2026-05-09 16:22:00', '2026-05-09 16:23:00'),
  ('TT017', 'DD019', 'DAT_COC', 'MOMO', 570000, 'MOM-20260510-017', 'DA_THANH_TOAN', '2026-05-10 09:32:00', '2026-05-10 09:33:00'),
  ('TT018', 'DD020', 'THANH_TOAN_DU', 'VNPAY', 1800000, 'VNP-20260510-018', 'DA_THANH_TOAN', '2026-05-10 11:02:00', '2026-05-10 11:03:00'),
  ('TT019', 'DD021', 'THANH_TOAN_DU', 'CHUYEN_KHOAN', 1180000, 'BIDV-20260511-019', 'DA_THANH_TOAN', '2026-05-11 14:02:00', '2026-05-11 14:04:00'),
  ('TT020', 'DD022', 'THANH_TOAN_DU', 'VNPAY', 1300000, 'VNP-20260511-020', 'DA_THANH_TOAN', '2026-05-11 15:52:00', '2026-05-11 15:53:00'),
  ('TT021', 'DD023', 'THANH_TOAN_DU', 'VNPAY', 2600000, 'VNP-20260512-021', 'DA_THANH_TOAN', '2026-05-12 08:32:00', '2026-05-12 08:33:00'),
  ('TT022', 'DD024', 'DAT_COC', 'MOMO', 408000, 'MOM-20260512-022', 'DA_THANH_TOAN', '2026-05-12 10:47:00', '2026-05-12 10:48:00'),
  ('DD025', 'DD025', 'THANH_TOAN_DU', 'VNPAY', 2500000, 'VNP-20260513-023', 'DA_THANH_TOAN', '2026-05-13 13:17:00', '2026-05-13 13:18:00'),
  ('TT024', 'DD002', 'THANH_TOAN_CON_LAI', 'TIEN_MAT', 2275000, 'TM-20260505-024', 'DA_THANH_TOAN', '2026-05-05 08:00:00', '2026-05-05 08:00:00'),
  ('TT025', 'DD006', 'THANH_TOAN_CON_LAI', 'CHUYEN_KHOAN', 2310000, 'VCB-20260515-025', 'DA_THANH_TOAN', '2026-05-15 14:00:00', '2026-05-15 14:05:00'),
  ('TT026', 'DD010', 'THANH_TOAN_CON_LAI', 'TIEN_MAT', 1148000, 'TM-20260516-026', 'DA_THANH_TOAN', '2026-05-16 11:30:00', '2026-05-16 11:30:00'),
  ('TT027', 'DD016', 'THANH_TOAN_CON_LAI', 'VNPAY', 3640000, 'VNP-20260530-027', 'DA_THANH_TOAN', '2026-05-30 14:00:00', '2026-05-30 14:02:00'),
  ('TT028', 'DD019', 'THANH_TOAN_CON_LAI', 'TIEN_MAT', 1330000, 'TM-20260524-028', 'DA_THANH_TOAN', '2026-05-24 14:00:00', '2026-05-24 14:00:00'),
  ('TT029', 'DD024', 'THANH_TOAN_CON_LAI', 'MOMO', 952000, 'MOM-20260529-029', 'DA_THANH_TOAN', '2026-05-29 08:00:00', '2026-05-29 08:02:00'),
  ('TT030', 'DD004', 'THANH_TOAN', 'VNPAY', 1450000, 'VNP-20260502-030', 'CHO_THANH_TOAN', '2026-05-02 11:21:00', NULL);

-- 25. HUY_DON (22 bản ghi yêu cầu hủy)
INSERT INTO `HUY_DON` (`MA_HUY`, `MA_NHOM_HUY`, `MA_CHI_TIET`, `MA_DON_DAT`, `MA_CHINH_SACH_HUY`, `SO_NGAY_TRUOC`, `THOI_GIAN_YEU_CAU`, `LY_DO`, `TIEN_HOAN`, `TIEN_GIU_LAI`, `TRANG_THAI_XU_LY`, `MA_NGUOI_XU_LY`) VALUES
  ('HD001', 'GRP-HUY-001', 'CT012', 'DD007', 'HUY01', 16, '2026-05-04 10:00:00', 'Khách thay đổi lịch trình công tác', 700000, 0, 'DA_XU_LY', 'ND001'),
  ('HD002', 'GRP-HUY-001', 'CT013', 'DD007', 'HUY01', 16, '2026-05-04 10:00:00', 'Khách thay đổi lịch trình công tác', 1200000, 0, 'DA_XU_LY', 'ND001'),
  ('HD003', 'GRP-HUY-002', 'CT022', 'DD014', 'HUY01', 18, '2026-05-07 16:00:00', 'Thời tiết mưa bão', 1700000, 0, 'DA_XU_LY', 'ND001'),
  ('HD004', 'GRP-HUY-003', 'CT034', 'DD022', 'HUY01', 17, '2026-05-11 16:30:00', 'Gia đình có việc đột xuất', 1300000, 0, 'DA_XU_LY', 'ND001'),
  ('HD005', 'GRP-HUY-004', 'CT001', 'DD001', 'HUY02', 10, '2026-05-01 12:00:00', 'Yêu cầu hủy thử nghiệm', 490000, 210000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD006', 'GRP-HUY-005', 'CT002', 'DD001', 'HUY02', 10, '2026-05-01 12:05:00', 'Yêu cầu hủy thử nghiệm phòng', 840000, 360000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD007', 'GRP-HUY-006', 'CT003', 'DD002', 'HUY03', 5, '2026-05-01 14:00:00', 'Hủy 1 phần combo', 1300000, 1300000, 'DA_DUYET', 'ND001'),
  ('HD008', 'GRP-HUY-007', 'CT005', 'DD003', 'HUY01', 15, '2026-05-02 12:00:00', 'Yêu cầu hủy trước hạn', 3200000, 0, 'DA_DUYET', 'ND001'),
  ('HD009', 'GRP-HUY-008', 'CT007', 'DD004', 'HUY04', 2, '2026-05-02 14:00:00', 'Hủy sát ngày', 0, 1050000, 'DA_DUYET', 'ND001'),
  ('HD010', 'GRP-HUY-009', 'CT009', 'DD005', 'HUY01', 20, '2026-05-03 16:00:00', 'Đổi sang dịp khác', 1900000, 0, 'CAN_XAC_NHAN', 'ND001'),
  ('HD011', 'GRP-HUY-010', 'CT010', 'DD006', 'HUY02', 9, '2026-05-03 17:00:00', 'Giảm số lượng phòng', 1960000, 840000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD012', 'GRP-HUY-011', 'CT014', 'DD008', 'HUY01', 14, '2026-05-04 11:00:00', 'Hủy vé gia đình', 1200000, 0, 'DA_DUYET', 'ND001'),
  ('HD013', 'GRP-HUY-012', 'CT015', 'DD009', 'HUY03', 4, '2026-05-05 14:00:00', 'Hủy phòng villa', 2250000, 2250000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD014', 'GRP-HUY-013', 'CT017', 'DD010', 'HUY01', 15, '2026-05-05 17:00:00', 'Hủy bàn tiệc', 1640000, 0, 'DA_DUYET', 'ND001'),
  ('HD015', 'GRP-HUY-014', 'CT018', 'DD011', 'HUY02', 8, '2026-05-06 10:00:00', 'Hủy vé tham quan', 1225000, 525000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD016', 'GRP-HUY-015', 'CT019', 'DD012', 'HUY01', 16, '2026-05-06 12:00:00', 'Hủy đơn chờ', 2400000, 0, 'DA_DUYET', 'ND001'),
  ('HD017', 'GRP-HUY-016', 'CT020', 'DD013', 'HUY04', 1, '2026-05-07 16:00:00', 'Hủy combo sát ngày', 0, 1040000, 'DA_XU_LY', 'ND001'),
  ('HD018', 'GRP-HUY-017', 'CT023', 'DD015', 'HUY01', 14, '2026-05-08 11:00:00', 'Hủy trước 14 ngày', 1950000, 0, 'DA_DUYET', 'ND001'),
  ('HD019', 'GRP-HUY-018', 'CT025', 'DD016', 'HUY02', 11, '2026-05-08 12:00:00', 'Hủy villa hoàng hôn', 3640000, 1560000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD020', 'GRP-HUY-019', 'CT026', 'DD017', 'HUY01', 15, '2026-05-09 14:00:00', 'Hủy buffet', 1500000, 0, 'DA_DUYET', 'ND001'),
  ('HD021', 'GRP-HUY-020', 'CT027', 'DD018', 'HUY03', 5, '2026-05-09 17:00:00', 'Hủy vé người lớn', 700000, 700000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD022', 'GRP-HUY-021', 'CT030', 'DD019', 'HUY01', 16, '2026-05-10 10:00:00', 'Hủy homestay bên suối', 1900000, 0, 'DA_DUYET', 'ND001');

-- 26. HOAN_TIEN (15 bản ghi hoàn tiền trỏ ngược THANH_TOAN)
INSERT INTO `HOAN_TIEN` (`MA_HOAN_TIEN`, `MA_HUY`, `MA_THANH_TOAN`, `SO_TIEN`, `TRANG_THAI`, `MA_GIAO_DICH_HOAN`, `THOI_GIAN_TAO`, `THOI_GIAN_HOAN`) VALUES
  ('HT001', 'HD001', 'TT006', 700000, 'DA_HOAN', 'REF-VNP-001', '2026-05-04 11:00:00', '2026-05-04 11:10:00'),
  ('HT002', 'HD002', 'TT006', 1200000, 'DA_HOAN', 'REF-VNP-002', '2026-05-04 11:00:00', '2026-05-04 11:12:00'),
  ('HT003', 'HD003', 'TT012', 1700000, 'DA_HOAN', 'REF-VNP-003', '2026-05-07 17:00:00', '2026-05-07 17:15:00'),
  ('HT004', 'HD004', 'TT020', 1300000, 'DA_HOAN', 'REF-VNP-004', '2026-05-11 17:00:00', '2026-05-11 17:08:00'),
  ('HT005', 'HD007', 'TT002', 975000, 'DA_HOAN', 'REF-MOM-005', '2026-05-02 09:00:00', '2026-05-02 09:05:00'),
  ('HT006', 'HD008', 'TT003', 3200000, 'DA_HOAN', 'REF-MB-006', '2026-05-03 10:00:00', '2026-05-03 10:30:00'),
  ('HT007', 'HD012', 'TT007', 1200000, 'DA_HOAN', 'REF-MOM-007', '2026-05-05 10:00:00', '2026-05-05 10:15:00'),
  ('HT008', 'HD014', 'TT009', 492000, 'DA_HOAN', 'REF-TM-008', '2026-05-06 14:00:00', '2026-05-06 14:00:00'),
  ('HT009', 'HD016', 'TT030', 1450000, 'CHO_HOAN', 'REF-VNP-009', '2026-05-07 10:00:00', NULL),
  ('HT010', 'HD018', 'TT013', 1950000, 'DA_HOAN', 'REF-VNP-010', '2026-05-09 09:00:00', '2026-05-09 09:20:00'),
  ('HT011', 'HD020', 'TT015', 1500000, 'DA_HOAN', 'REF-TM-011', '2026-05-10 11:00:00', '2026-05-10 11:00:00'),
  ('HT012', 'HD022', 'TT017', 570000, 'DA_HOAN', 'REF-MOM-012', '2026-05-11 11:00:00', '2026-05-11 11:05:00'),
  ('HT013', 'HD005', 'TT001', 490000, 'CHO_HOAN', 'REF-VNP-013', '2026-05-01 13:00:00', NULL),
  ('HT014', 'HD006', 'TT001', 840000, 'CHO_HOAN', 'REF-VNP-014', '2026-05-01 13:00:00', NULL),
  ('HT015', 'HD010', 'TT004', 1900000, 'CHO_HOAN', 'REF-VNP-015', '2026-05-03 17:00:00', NULL);

-- 27. CHECK_IN (25 bản ghi check-in qua Cổng/Quầy/Nhà hàng)
INSERT INTO `CHECK_IN` (`MA_CHECK_IN`, `MA_DON_DAT`, `LOAI_CHECK_IN`, `MA_VE`, `MA_DAT_PHONG`, `MA_DAT_BAN`, `MA_QUYEN_LOI`, `MA_NHAN_VIEN`, `SO_LUONG`, `THOI_GIAN_CHECK_IN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('CI001', 'DD001', 'VE', 'VE_TK001', NULL, NULL, NULL, 'ND003', 1, '2026-05-02 08:35:00', 'DA_CHECK_IN', 'Qua cổng chính Trạm 1'),
  ('CI002', 'DD001', 'VE', 'VE_TK002', NULL, NULL, NULL, 'ND003', 1, '2026-05-02 08:36:00', 'DA_CHECK_IN', 'Qua cổng chính Trạm 1'),
  ('CI003', 'DD001', 'PHONG', NULL, 'DP001', NULL, NULL, 'ND004', 1, '2026-05-02 14:00:00', 'DA_CHECK_IN', 'Nhận phòng tại quầy Lễ Tân Trạm 2'),
  ('CI004', 'DD005', 'VE', 'VE_TK012', NULL, NULL, NULL, 'ND003', 1, '2026-05-08 09:10:00', 'DA_CHECK_IN', 'Cổng VIP Trạm 1'),
  ('CI005', 'DD005', 'VE', 'VE_TK013', NULL, NULL, NULL, 'ND003', 1, '2026-05-08 09:11:00', 'DA_CHECK_IN', 'Cổng VIP Trạm 1'),
  ('CI006', 'DD008', 'VE', 'VE_TK016', NULL, NULL, NULL, 'ND003', 1, '2026-05-09 08:50:00', 'DA_CHECK_IN', 'Cổng gia đình'),
  ('CI007', 'DD009', 'VE', 'VE_TK017', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:20:00', 'DA_CHECK_IN', 'Qua trạm cáp treo'),
  ('CI008', 'DD009', 'VE', 'VE_TK018', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:21:00', 'DA_CHECK_IN', 'Qua trạm cáp treo'),
  ('CI009', 'DD009', 'VE', 'VE_TK019', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:22:00', 'DA_CHECK_IN', 'Qua trạm cáp treo'),
  ('CI010', 'DD009', 'VE', 'VE_TK020', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:23:00', 'DA_CHECK_IN', 'Qua trạm cáp treo'),
  ('CI011', 'DD009', 'PHONG', NULL, 'DP005', NULL, NULL, 'ND004', 1, '2026-05-18 14:15:00', 'DA_CHECK_IN', 'Nhận villa 2PN'),
  ('CI012', 'DD011', 'VE', 'VE_TK021', NULL, NULL, NULL, 'ND003', 1, '2026-05-11 09:00:00', 'DA_CHECK_IN', 'Cổng 1'),
  ('CI013', 'DD011', 'VE', 'VE_TK022', NULL, NULL, NULL, 'ND003', 1, '2026-05-11 09:01:00', 'DA_CHECK_IN', 'Cổng 1'),
  ('CI014', 'DD013', 'VE', 'VE_TK023', NULL, NULL, NULL, 'ND003', 1, '2026-05-14 09:15:00', 'DA_CHECK_IN', 'Cáp treo Trạm 1'),
  ('CI015', 'DD013', 'VE', 'VE_TK024', NULL, NULL, NULL, 'ND003', 1, '2026-05-14 09:16:00', 'DA_CHECK_IN', 'Cáp treo Trạm 1'),
  ('CI016', 'DD015', 'VE', 'VE_TK025', NULL, NULL, NULL, 'ND003', 1, '2026-05-13 08:40:00', 'DA_CHECK_IN', 'Cổng chính'),
  ('CI017', 'DD003', 'PHONG', NULL, 'DP002', NULL, NULL, 'ND004', 2, '2026-05-10 14:30:00', 'DA_CHECK_IN', 'Nhận 2 phòng deluxe'),
  ('CI018', 'DD023', 'PHONG', NULL, 'DP010', NULL, NULL, 'ND004', 1, '2026-05-26 14:00:00', 'DA_CHECK_IN', 'Nhận glamping'),
  ('CI019', 'DD025', 'PHONG', NULL, 'DP011', NULL, NULL, 'ND004', 1, '2026-05-27 14:00:00', 'DA_CHECK_IN', 'Nhận glamping gia đình'),
  ('CI020', 'DD002', 'AN_UONG', NULL, NULL, 'DB001', NULL, 'ND007', 4, '2026-05-05 11:30:00', 'DA_CHECK_IN', 'Vào bàn ăn cơm lam gà nướng'),
  ('CI021', 'DD003', 'AN_UONG', NULL, NULL, 'DB002', NULL, 'ND007', 4, '2026-05-10 18:00:00', 'DA_CHECK_IN', 'Vào tiệc BBQ tối'),
  ('CI022', 'DD013', 'AN_UONG', NULL, NULL, 'DB005', NULL, 'ND007', 2, '2026-05-14 18:30:00', 'DA_CHECK_IN', 'Uống rượu cần'),
  ('CI023', 'DD017', 'AN_UONG', NULL, NULL, 'DB006', NULL, 'ND007', 6, '2026-05-17 11:45:00', 'DA_CHECK_IN', 'Buffet trưa nhà hàng'),
  ('CI024', 'DD018', 'AN_UONG', NULL, NULL, 'DB007', NULL, 'ND007', 4, '2026-05-19 18:00:00', 'DA_CHECK_IN', 'Dùng lẩu gà lá é'),
  ('CI025', 'DD021', 'AN_UONG', NULL, NULL, 'DB008', NULL, 'ND007', 2, '2026-05-23 12:00:00', 'DA_CHECK_IN', 'Cá niên nướng trưa');

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Kiểm tra tổng tiền DON_DAT khớp với CHI_TIET_DON có HIEU_LUC:' AS `MUC_KIEM_TRA`;
SELECT
    d.MA_DON_DAT,
    d.MA_DON_HANG,
    d.TONG_TIEN AS TONG_DON_DAT,
    COALESCE(SUM(c.SO_LUONG * c.DON_GIA), 0) AS TONG_CHI_TIET_TINH,
    CASE WHEN d.TONG_TIEN = COALESCE(SUM(c.SO_LUONG * c.DON_GIA), 0) THEN 'KHOP_100%' ELSE 'LECH_TIEN' END AS TRANG_THAI_KHOP
FROM DON_DAT d
LEFT JOIN CHI_TIET_DON c ON c.MA_DON_DAT = d.MA_DON_DAT AND c.TRANG_THAI = 'HIEU_LUC'
GROUP BY d.MA_DON_DAT, d.MA_DON_HANG, d.TONG_TIEN
ORDER BY d.MA_DON_DAT;

SELECT 'Số lượng bản ghi trong các bảng chính (Tiêu chí 4.1 >= 20):' AS `MUC_KIEM_TRA`;
SELECT 'NGUOI_DUNG' AS BANG, COUNT(*) AS SO_BAN_GHI FROM NGUOI_DUNG
UNION ALL SELECT 'LOAI_VE', COUNT(*) FROM LOAI_VE
UNION ALL SELECT 'BANG_GIA_VE', COUNT(*) FROM BANG_GIA_VE
UNION ALL SELECT 'KHU_LUU_TRU', COUNT(*) FROM KHU_LUU_TRU
UNION ALL SELECT 'LOAI_PHONG', COUNT(*) FROM LOAI_PHONG
UNION ALL SELECT 'PHONG', COUNT(*) FROM PHONG
UNION ALL SELECT 'DICH_VU_AN_UONG', COUNT(*) FROM DICH_VU_AN_UONG
UNION ALL SELECT 'BAN_AN', COUNT(*) FROM BAN_AN
UNION ALL SELECT 'DON_DAT', COUNT(*) FROM DON_DAT
UNION ALL SELECT 'CHI_TIET_DON', COUNT(*) FROM CHI_TIET_DON
UNION ALL SELECT 'VE', COUNT(*) FROM VE
UNION ALL SELECT 'DAT_PHONG', COUNT(*) FROM DAT_PHONG
UNION ALL SELECT 'PHAN_PHONG', COUNT(*) FROM PHAN_PHONG
UNION ALL SELECT 'DAT_BAN', COUNT(*) FROM DAT_BAN
UNION ALL SELECT 'QUYEN_LOI_DON', COUNT(*) FROM QUYEN_LOI_DON
UNION ALL SELECT 'THANH_TOAN', COUNT(*) FROM THANH_TOAN
UNION ALL SELECT 'HUY_DON', COUNT(*) FROM HUY_DON
UNION ALL SELECT 'CHECK_IN', COUNT(*) FROM CHECK_IN;
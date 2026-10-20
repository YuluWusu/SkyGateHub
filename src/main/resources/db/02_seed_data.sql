-- =============================================================================
--  SkyGateHub - DU LIEU MAU KDL CONG TROI DONG GIANG (MySQL 8.x)
--  File: 02_seed_data.sql
--  Thoa man Tieu chi 4.1: Toi thieu 20 ban ghi cho tat ca cac bang chinh
--  Mat khau mac dinh cho tat ca tai khoan: 123456 (da bam BCrypt)
--  Tong tien DON_DAT khop 100% voi tong THANH_TIEN cua CHI_TIET_DON co HIEU_LUC
-- =============================================================================
USE `QL_Cong_Troi_Dong_Giang`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- XOA DU LIEU CU THEO THU TU
DELETE FROM `CHECK_IN`;
DELETE FROM `HOAN_TIEN`;
DELETE FROM `HUY_DON`;
DELETE FROM `THANH_TOAN`;
DELETE FROM `DAT_BAN`;
DELETE FROM `QUYEN_LOI_DON`;
DELETE FROM `COMBO_THANH_PHAN`;
DELETE FROM `THANH_PHAN_DICH_VU`;
DELETE FROM `PHAN_PHONG`;
DELETE FROM `DAT_PHONG`;
DELETE FROM `VE`;
DELETE FROM `CHI_TIET_DON`;
DELETE FROM `DON_DAT`;
DELETE FROM `DICH_VU_CA_AN`;
DELETE FROM `BAN_AN`;
DELETE FROM `CA_AN`;
DELETE FROM `DICH_VU_AN_UONG`;
DELETE FROM `PHONG`;
DELETE FROM `LOAI_PHONG`;
DELETE FROM `KHU_LUU_TRU`;
DELETE FROM `NGAY_DAC_BIET`;
DELETE FROM `BANG_GIA_VE`;
DELETE FROM `KHO_VE`;
DELETE FROM `LOAI_VE`;
DELETE FROM `NGUOI_DUNG_VAI_TRO`;
DELETE FROM `VAI_TRO`;
DELETE FROM `NGUOI_DUNG`;
DELETE FROM `CHINH_SACH_COC`;
DELETE FROM `CHINH_SACH_HUY`;

-- 1. NGUOI_DUNG (25 ban ghi)
INSERT INTO `NGUOI_DUNG` (`MA_NGUOI_DUNG`, `TEN_DANG_NHAP`, `MAT_KHAU_HASH`, `HO_TEN`, `EMAIL`, `SO_DIEN_THOAI`, `TRANG_THAI`, `CAN_DOI_MAT_KHAU`) VALUES
  ('ND001', 'admin', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Nguyen Quan Tri', 'admin@skygate.vn', '0905111001', 1, 0),
  ('ND002', 'nv_ve1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Tran Thi Ban Ve', 've1@skygate.vn', '0905111002', 1, 0),
  ('ND003', 'nv_ve2', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Le Van Soat Ve', 've2@skygate.vn', '0905111003', 1, 0),
  ('ND004', 'nv_letan1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Pham Thu Huong', 'letan1@skygate.vn', '0905111004', 1, 0),
  ('ND005', 'nv_letan2', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Hoang Van Phong', 'letan2@skygate.vn', '0905111005', 1, 0),
  ('ND006', 'nv_bep1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Nguyen Van Bep', 'nhahang1@skygate.vn', '0905111006', 1, 0),
  ('ND007', 'nv_nhahang1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Do Thi Thuc Khach', 'nhahang2@skygate.vn', '0905111007', 1, 0),
  ('ND008', 'khach08', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Tran Anh Tuan', 'khach08@gmail.com', '0912345008', 1, 0),
  ('ND009', 'khach09', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Nguyen Minh Chau', 'khach09@gmail.com', '0912345009', 1, 0),
  ('ND010', 'khach10', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Vo Van Kiet', 'khach10@gmail.com', '0912345010', 1, 0),
  ('ND011', 'khach11', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Dang Thuy Tien', 'khach11@gmail.com', '0912345011', 1, 0),
  ('ND012', 'khach12', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Bui Duc Thinh', 'khach12@gmail.com', '0912345012', 1, 0),
  ('ND013', 'khach13', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Hoang My Linh', 'khach13@gmail.com', '0912345013', 1, 0),
  ('ND014', 'khach14', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Ngo Quoc Bao', 'khach14@gmail.com', '0912345014', 1, 0),
  ('ND015', 'khach15', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Duong Cam Tu', 'khach15@gmail.com', '0912345015', 1, 0),
  ('ND016', 'khach16', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Truong Gia Binh', 'khach16@gmail.com', '0912345016', 1, 0),
  ('ND017', 'khach17', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Dinh Tien Dung', 'khach17@gmail.com', '0912345017', 1, 0),
  ('ND018', 'khach18', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Phan Thanh Hau', 'khach18@gmail.com', '0912345018', 1, 0),
  ('ND019', 'khach19', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Vu Hoang Yen', 'khach19@gmail.com', '0912345019', 1, 0),
  ('ND020', 'khach20', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Lam Truong An', 'khach20@gmail.com', '0912345020', 1, 0),
  ('ND021', 'khach21', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Doan Ngoc Hai', 'khach21@gmail.com', '0912345021', 1, 0),
  ('ND022', 'khach22', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Nguyen Huu Tho', 'khach22@gmail.com', '0912345022', 1, 0),
  ('ND023', 'khach23', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Trinh Cong Son', 'khach23@gmail.com', '0912345023', 1, 0),
  ('ND024', 'khach24', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Ha Kieu Anh', 'khach24@gmail.com', '0912345024', 1, 0),
  ('ND025', 'khach25', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjAkq0Mfv88mqLcKPu85P66be8DCsj2', 'Luong The Vinh', 'khach25@gmail.com', '0912345025', 1, 0);

-- 2. VAI_TRO (4 vai tro)
INSERT INTO `VAI_TRO` (`MA_VAI_TRO`, `TEN_VAI_TRO`, `MO_TA`) VALUES
  ('VT001', 'QUAN_LY', 'Quan tri vien toan quyen he thong'),
  ('VT004', 'NHAN_VIEN_VE', 'Nhan vien ban ve va check-in cong vao'),
  ('VT005', 'NHAN_VIEN_NHA_HANG', 'Nhan vien quan ly ban an va check-in nha hang'),
  ('VT006', 'NHAN_VIEN_LUU_TRU', 'Nhan vien le tan va phan phong luu tru');

-- 3. NGUOI_DUNG_VAI_TRO (25 ban ghi)
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

-- 4. LOAI_VE (25 ban ghi loai ve & combo)
INSERT INTO `LOAI_VE` (`MA_LOAI_VE`, `TEN_LOAI_VE`, `LOAI_VE`, `MO_TA`, `GIA_VE`, `TRANG_THAI`) VALUES
  ('VE01', 'Ve Tham Quan Nguoi Lon', 'NGUOI_LON', 'Ve vao cong tham quan toan khu danh cho nguoi lon', 350000, 1),
  ('VE02', 'Ve Tham Quan Tre Em', 'TRE_EM', 'Ve vao cong cho tre em chieu cao 1m - 1m4', 200000, 1),
  ('VE03', 'Combo Cong Troi 1 Ngay Nguoi Lon', 'TRON_GOI', 'Ve cong + cap treo + buffet trua', 650000, 1),
  ('VE04', 'Combo Cong Troi 1 Ngay Tre Em', 'TRON_GOI', 'Ve cong + cap treo + buffet trua tre em', 450000, 1),
  ('VE05', 'Ve Cap Treo Nguoi Lon', 'NGUOI_LON', 'Ve khuu hoi cap treo len dinh Cong Troi', 250000, 1),
  ('VE06', 'Ve Cap Treo Tre Em', 'TRE_EM', 'Ve khuu hoi cap treo cho tre em', 150000, 1),
  ('VE07', 'Ve Xe Dien Noi Khu', 'KHAC', 'Ve su dung xe dien khong gioi han trong ngay', 50000, 1),
  ('VE08', 'Combo Trai Nghiem Sinh Thai VIP', 'TRON_GOI', 'Ve cong + cap treo + huong dan vien + buffet VIP', 950000, 1),
  ('VE09', 'Ve Tham Quan Suoi Hoa Nguoi Lon', 'NGUOI_LON', 'Khu tieu canh suoi hoa va cong troi', 280000, 1),
  ('VE10', 'Ve Tham Quan Suoi Hoa Tre Em', 'TRE_EM', 'Khu tieu canh suoi hoa cho tre em', 160000, 1),
  ('VE11', 'Ve Cheo Thuyen Kayak Cay Xanh', 'KHAC', 'Ve 60 phut cheo thuyen tren song suoi', 120000, 1),
  ('VE12', 'Ve Truot Thac Nuoc Tu Nhien', 'KHAC', 'Tro choi cam giac manh truot thac', 150000, 1),
  ('VE13', 'Combo Gia Dinh 2 Lon 1 Tre', 'TRON_GOI', 'Goi tham quan tron goi cho gia dinh nho', 1200000, 1),
  ('VE14', 'Combo Gia Dinh 2 Lon 2 Tre', 'TRON_GOI', 'Goi tham quan tron goi cho gia dinh 4 nguoi', 1500000, 1),
  ('VE15', 'Ve Tham Quan Hoang Hon Chieu', 'NGUOI_LON', 'Ap dung check-in sau 15h00 hang ngay', 220000, 1),
  ('VE16', 'Ve Tham Quan Hoang Hon Tre Em', 'TRE_EM', 'Ap dung check-in sau 15h00 cho tre em', 130000, 1),
  ('VE17', 'Combo Cap Treo Va Buffet', 'TRON_GOI', 'Cap treo khuu hoi kem 01 suat buffet trua', 520000, 1),
  ('VE18', 'Ve Vao Cong Nguoi Cao Tuoi', 'KHAC', 'Uu dai cho nguoi tren 60 tuoi', 180000, 1),
  ('VE19', 'Ve Cong Doan Vien / Hoc Sinh', 'KHAC', 'Chinh sach gia uu dai theo doan hoc sinh', 150000, 1),
  ('VE20', 'Combo Teambuilding Doan', 'TRON_GOI', 'Ve cong + san co to chuc + am thanh teambuilding', 400000, 1),
  ('VE21', 'Ve Trai Nghiem Lang Nghe Co Tu', 'KHAC', 'Tham quan va giao luu van hoa truyen thong', 100000, 1),
  ('VE22', 'Combo Cua Ngo Thien Nhien', 'TRON_GOI', 'Ve cong + nuoc suoi + xe dien 1 ngay', 390000, 1),
  ('VE23', 'Ve Cung Duong Rung Nguyen Sinh', 'NGUOI_LON', 'Trekking co huong dan vien kiem lam', 300000, 1),
  ('VE24', 'Ve Trai Nghiem Ho Boi Vo Cuc', 'KHAC', 'Ve su dung be boi nuoc khoang giua rung', 80000, 1),
  ('VE25', 'Combo All-Inclusive Sieu VIP', 'TRON_GOI', 'Tron goi toan bo dich vu giai tri trong ngay', 1800000, 1);

-- 5. BANG_GIA_VE (25 ban ghi)
INSERT INTO `BANG_GIA_VE` (`MA_BANG_GIA`, `MA_LOAI_VE`, `TEN_BANG_GIA`, `DIEU_KIEN_GIA`, `HIEU_LUC_TU`, `HIEU_LUC_DEN`, `DON_GIA`, `UU_TIEN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('BG01', 'VE01', 'Bang gia tieu chuan Ve Tham Quan Nguoi Lon', 'MAC_DINH', '2026-01-01', '2026-12-31', 350000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG02', 'VE02', 'Bang gia tieu chuan Ve Tham Quan Tre Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 200000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG03', 'VE03', 'Bang gia tieu chuan Combo Cong Troi 1 Ngay Nguoi Lon', 'MAC_DINH', '2026-01-01', '2026-12-31', 650000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG04', 'VE04', 'Bang gia tieu chuan Combo Cong Troi 1 Ngay Tre Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 450000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG05', 'VE05', 'Bang gia tieu chuan Ve Cap Treo Nguoi Lon', 'MAC_DINH', '2026-01-01', '2026-12-31', 250000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG06', 'VE06', 'Bang gia tieu chuan Ve Cap Treo Tre Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 150000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG07', 'VE07', 'Bang gia tieu chuan Ve Xe Dien Noi Khu', 'MAC_DINH', '2026-01-01', '2026-12-31', 50000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG08', 'VE08', 'Bang gia tieu chuan Combo Trai Nghiem Sinh Thai VIP', 'MAC_DINH', '2026-01-01', '2026-12-31', 950000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG09', 'VE09', 'Bang gia tieu chuan Ve Tham Quan Suoi Hoa Nguoi Lon', 'MAC_DINH', '2026-01-01', '2026-12-31', 280000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG10', 'VE10', 'Bang gia tieu chuan Ve Tham Quan Suoi Hoa Tre Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 160000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG11', 'VE11', 'Bang gia tieu chuan Ve Cheo Thuyen Kayak Cay Xanh', 'MAC_DINH', '2026-01-01', '2026-12-31', 120000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG12', 'VE12', 'Bang gia tieu chuan Ve Truot Thac Nuoc Tu Nhien', 'MAC_DINH', '2026-01-01', '2026-12-31', 150000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG13', 'VE13', 'Bang gia tieu chuan Combo Gia Dinh 2 Lon 1 Tre', 'MAC_DINH', '2026-01-01', '2026-12-31', 1200000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG14', 'VE14', 'Bang gia tieu chuan Combo Gia Dinh 2 Lon 2 Tre', 'MAC_DINH', '2026-01-01', '2026-12-31', 1500000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG15', 'VE15', 'Bang gia tieu chuan Ve Tham Quan Hoang Hon Chieu', 'MAC_DINH', '2026-01-01', '2026-12-31', 220000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG16', 'VE16', 'Bang gia tieu chuan Ve Tham Quan Hoang Hon Tre Em', 'MAC_DINH', '2026-01-01', '2026-12-31', 130000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG17', 'VE17', 'Bang gia tieu chuan Combo Cap Treo Va Buffet', 'MAC_DINH', '2026-01-01', '2026-12-31', 520000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG18', 'VE18', 'Bang gia tieu chuan Ve Vao Cong Nguoi Cao Tuoi', 'MAC_DINH', '2026-01-01', '2026-12-31', 180000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG19', 'VE19', 'Bang gia tieu chuan Ve Cong Doan Vien / Hoc Sinh', 'MAC_DINH', '2026-01-01', '2026-12-31', 150000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG20', 'VE20', 'Bang gia tieu chuan Combo Teambuilding Doan', 'MAC_DINH', '2026-01-01', '2026-12-31', 400000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG21', 'VE21', 'Bang gia tieu chuan Ve Trai Nghiem Lang Nghe Co Tu', 'MAC_DINH', '2026-01-01', '2026-12-31', 100000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG22', 'VE22', 'Bang gia tieu chuan Combo Cua Ngo Thien Nhien', 'MAC_DINH', '2026-01-01', '2026-12-31', 390000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG23', 'VE23', 'Bang gia tieu chuan Ve Cung Duong Rung Nguyen Sinh', 'MAC_DINH', '2026-01-01', '2026-12-31', 300000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG24', 'VE24', 'Bang gia tieu chuan Ve Trai Nghiem Ho Boi Vo Cuc', 'MAC_DINH', '2026-01-01', '2026-12-31', 80000, 10, 1, 'Gia he thong ap dung nam 2026'),
  ('BG25', 'VE25', 'Bang gia tieu chuan Combo All-Inclusive Sieu VIP', 'MAC_DINH', '2026-01-01', '2026-12-31', 1800000, 10, 1, 'Gia he thong ap dung nam 2026');

-- 6. NGAY_DAC_BIET (10 ngay le)
INSERT INTO `NGAY_DAC_BIET` (`MA_NGAY_DAC_BIET`, `NGAY_AP_DUNG`, `TEN_NGAY`, `LOAI_NGAY`, `GHI_CHU`, `TRANG_THAI`) VALUES
  ('NDB01', '2026-01-01', 'Tet Duong Lich 2026', 'LE_TET', 'Nghi le toan quoc', 1),
  ('NDB02', '2026-02-16', '29 Tet Binh Ngo', 'LE_TET', 'Tet Nguyen Dan', 1),
  ('NDB03', '2026-02-17', 'Mung 1 Tet Binh Ngo', 'LE_TET', 'Tet Nguyen Dan', 1),
  ('NDB04', '2026-02-18', 'Mung 2 Tet Binh Ngo', 'LE_TET', 'Tet Nguyen Dan', 1),
  ('NDB05', '2026-04-26', 'Gio To Hung Vuong', 'LE_TET', '10 thang 3 Am lich', 1),
  ('NDB06', '2026-04-30', 'Ngay Giai Phong Mien Nam', 'LE_TET', '30/4', 1),
  ('NDB07', '2026-05-01', 'Quoc Te Lao Dong', 'LE_TET', '1/5', 1),
  ('NDB08', '2026-09-02', 'Quoc Khanh 2/9', 'LE_TET', 'Nghi Quoc khanh', 1),
  ('NDB09', '2026-10-20', 'Ngay Phu Nu Viet Nam', 'SU_KIEN', 'Uu dai khach nu', 1),
  ('NDB10', '2026-12-25', 'Le Giang Sinh', 'SU_KIEN', 'Mua le hoi cuoi nam', 1);

-- 7. KHO_VE (25 ban ghi ton kho)
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

-- 8. KHU_LUU_TRU (4 khu luu tru)
INSERT INTO `KHU_LUU_TRU` (`MA_KHU`, `TEN_KHU`, `HANG_SAO`, `DIA_CHI`, `EMAIL`, `HOTLINE`, `HINH_ANH`) VALUES
  (1, 'FIVITEL Sông Ngân', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'song_ngan.jpg'),
  (2, 'FIVITEL Cầu Kiều', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'cau_kieu.jpg'),
  (3, 'FIVITEL Ô Thước', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'o_thuoc.jpg'),
  (4, 'Nhà Moong', 5, 'Thôn A Sờ, Xã Bến Hiên, Thành Phố Đà Nẵng', 'booking@congtroidonggiang.com', '1900 866644', 'nha_moong.jpg');

-- 8.1. LOAI_PHONG (15 loai phong dung tung khu, dien tich va gia tu anh thuc te)
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

-- 9. PHONG (30 phong vat ly phan bo theo 15 loai phong)
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

-- 10. DICH_VU_AN_UONG (25 dich vu nha hang)
INSERT INTO `DICH_VU_AN_UONG` (`MA_DICH_VU`, `TEN_DICH_VU`, `LOAI_DICH_VU`, `MO_TA`, `GIA_DICH_VU`, `TRANG_THAI`) VALUES
  ('DV01', 'Buffet Trua Am Thuc Co Tu', 'BUFFET', 'Buffet hon 50 mon dac san rung Tay Giang', 250000, 1),
  ('DV02', 'Buffet Toi Nuong BBQ Cao Nguyen', 'BUFFET', 'Tiec nuong ngoai troi voi thit lon ban', 350000, 1),
  ('DV03', 'Set Menu Com Lam Ga Nuong 4 Nguoi', 'SET_MENU', 'Com lam ong tre kem ga doi nuong mat ong', 650000, 1),
  ('DV04', 'Set Menu Ca Nien Rau Rung', 'SET_MENU', 'Ca nien nuong trui kem rau don suoi', 480000, 1),
  ('DV05', 'Set Menu Thit Lon Ban 7 Mon', 'SET_MENU', 'Thit heo rung ban che bien 7 mon dac sac', 820000, 1),
  ('DV06', 'Lau Ga La E Tay Giang', 'A_LA_CARTE', 'Noi lau ga soi la e thom nong', 450000, 1),
  ('DV07', 'Heo Ban Xao Lan', 'A_LA_CARTE', 'Dia thit lon ban thom lung gia vi nui rung', 180000, 1),
  ('DV08', 'Rau Don Xao Toi', 'A_LA_CARTE', 'Rau don tuoi hai ven suoi nui', 80000, 1),
  ('DV09', 'Ca Nien Chien Gion', 'A_LA_CARTE', 'Ca suoi nuoc lanh chien gion cham mam gung', 160000, 1),
  ('DV10', 'Xoi Nep Nuong Ong Tre', 'A_LA_CARTE', 'Xoi nep thom deo nau ong tre', 60000, 1),
  ('DV11', 'Ruou Can Co Tu Dac Biet', 'DO_UONG', 'Binh ruou can u men la cay truyen thong', 220000, 1),
  ('DV12', 'Set An Sang EcoGate Breakfast', 'SET_MENU', 'Pho bo/banh mi + ca phe sang', 95000, 1),
  ('DV13', 'Buffet Sang Continental', 'BUFFET', 'Buffet sang tieu chuan quoc te', 150000, 1),
  ('DV14', 'Set Tiec Nuong San Vuon VIP', 'SET_MENU', 'Tiec BBQ rieng tai can villa', 1500000, 1),
  ('DV15', 'Set Tra Chieu Hoang Hon', 'DO_UONG', 'Tra thao moc kem banh ngot ngam hoang hon', 180000, 1),
  ('DV16', 'Bo Nuong Da Nui Lua', 'A_LA_CARTE', 'Thit bo nuong tren phien da nong', 260000, 1),
  ('DV17', 'Goi Hoa Chuoi Rung Ga Xe', 'A_LA_CARTE', 'Mon khai vi chua ngot thom ngon', 140000, 1),
  ('DV18', 'Chao Nam Tram Bap Bo', 'A_LA_CARTE', 'To chao nong bo duong dem suong lanh', 110000, 1),
  ('DV19', 'Canh Thut Ca Suoi Co Tu', 'A_LA_CARTE', 'Mon canh dac trung nau trong ong nua', 190000, 1),
  ('DV20', 'Com Chien Muoi E Ca Nien', 'A_LA_CARTE', 'Com chien gion muoi hat e nong am', 130000, 1),
  ('DV21', 'Bia Thu Cong Artisan Dong Giang', 'DO_UONG', 'Bia len men tu nhien doc dao', 75000, 1),
  ('DV22', 'Nuoc Ep Sam Day Ngoc Linh', 'DO_UONG', 'Thuc uong giai nhiet bo duong', 65000, 1),
  ('DV23', 'Set Chay Thanh Tinh Rung Xanh', 'SET_MENU', 'Thuc don chay 5 mon thanh nhe', 280000, 1),
  ('DV24', 'Hai San Nuong Than Hong', 'A_LA_CARTE', 'Tom muc tuoi nuong muoi ot', 320000, 1),
  ('DV25', 'Set Tiec Lua Trai Dem Giao Luu', 'SET_MENU', 'Khoai bap nuong + thit xien + ruou can', 550000, 1);

-- 11. CA_AN (3 ca an)
INSERT INTO `CA_AN` (`MA_CA_AN`, `TEN_CA_AN`, `GIO_BAT_DAU`, `GIO_KET_THUC`, `TRANG_THAI`) VALUES
  ('CA_SANG', 'Bua Sang Nang Som', '06:30:00', '09:30:00', 1),
  ('CA_TRUA', 'Bua Trua Thung Lung', '11:00:00', '14:00:00', 1),
  ('CA_TOI', 'Bua Toi Lua Trai', '17:30:00', '21:30:00', 1);

-- 12. BAN_AN (25 ban an)
INSERT INTO `BAN_AN` (`MA_BAN_AN`, `SO_BAN`, `KHU_VUC`, `SUC_CHUA`, `TRANG_THAI`) VALUES
  ('BA01', 'B01', 'Khu San Ho', 2, 'SAN_SANG'),
  ('BA02', 'B02', 'Khu San Ho', 4, 'SAN_SANG'),
  ('BA03', 'B03', 'Khu San Ho', 6, 'SAN_SANG'),
  ('BA04', 'B04', 'Khu San Ho', 10, 'SAN_SANG'),
  ('BA05', 'B05', 'Khu San Ho', 2, 'SAN_SANG'),
  ('BA06', 'B06', 'Khu San Ho', 4, 'SAN_SANG'),
  ('BA07', 'B07', 'Khu San Ho', 6, 'SAN_SANG'),
  ('BA08', 'B08', 'Khu Suoi Hoa', 10, 'SAN_SANG'),
  ('BA09', 'B09', 'Khu Suoi Hoa', 2, 'SAN_SANG'),
  ('BA10', 'B10', 'Khu Suoi Hoa', 4, 'SAN_SANG'),
  ('BA11', 'B11', 'Khu Suoi Hoa', 6, 'SAN_SANG'),
  ('BA12', 'B12', 'Khu Suoi Hoa', 10, 'SAN_SANG'),
  ('BA13', 'B13', 'Khu Suoi Hoa', 2, 'SAN_SANG'),
  ('BA14', 'B14', 'Khu Suoi Hoa', 4, 'SAN_SANG'),
  ('BA15', 'B15', 'Khu Hoang Hon', 6, 'SAN_SANG'),
  ('BA16', 'B16', 'Khu Hoang Hon', 10, 'SAN_SANG'),
  ('BA17', 'B17', 'Khu Hoang Hon', 2, 'SAN_SANG'),
  ('BA18', 'B18', 'Khu Hoang Hon', 4, 'SAN_SANG'),
  ('BA19', 'B19', 'Khu Hoang Hon', 6, 'SAN_SANG'),
  ('BA20', 'B20', 'Khu Hoang Hon', 10, 'SAN_SANG'),
  ('BA21', 'B21', 'Khu May Ngan VIP', 2, 'DANG_SU_DUNG'),
  ('BA22', 'B22', 'Khu May Ngan VIP', 4, 'DANG_SU_DUNG'),
  ('BA23', 'B23', 'Khu May Ngan VIP', 6, 'DANG_SU_DUNG'),
  ('BA24', 'B24', 'Khu May Ngan VIP', 10, 'DANG_SU_DUNG'),
  ('BA25', 'B25', 'Khu May Ngan VIP', 2, 'DANG_SU_DUNG');

-- 13. DICH_VU_CA_AN (25 ban ghi lien ket dich vu - ca an)
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

-- 14. THANH_PHAN_DICH_VU (20 thanh phan dich vu)
INSERT INTO `THANH_PHAN_DICH_VU` (`MA_THANH_PHAN`, `TEN_THANH_PHAN`, `LOAI_THANH_PHAN`, `MA_LOAI_VE`, `MA_DV_AN_UONG`, `MO_TA`, `TRANG_THAI`) VALUES
  ('TP01', 'Ve Vao Cong Tham Quan', 'THAM_QUAN', 'VE01', NULL, 'Luot qua cong chinh', 1),
  ('TP02', 'Cap Treo Khuu Hoi', 'CAP_TREO', NULL, NULL, 'Tuyen cap treo dai 2km', 1),
  ('TP03', 'Suat Buffet Trua Tay Giang', 'AN_UONG', NULL, 'DV01', '1 suat an buffet trua', 1),
  ('TP04', 'Xe Dien Noi Khu 1 Ngay', 'XE_DIEN', NULL, NULL, 'Di chuyen noi khu KDL', 1),
  ('TP05', 'Cheo Thuyen Kayak 60 Phut', 'KHAC', NULL, NULL, 'Cheo thuyen tren suoi', 1),
  ('TP06', 'Ve Cong Tre Em', 'THAM_QUAN', 'VE02', NULL, 'Qua cong cho tre em', 1),
  ('TP07', 'Buffet Trua Tre Em', 'AN_UONG', NULL, 'DV01', '1 suat buffet trua tre em', 1),
  ('TP08', 'Huong Dan Vien Rieng', 'KHAC', NULL, NULL, 'HDV thuyet minh toan tuyen', 1),
  ('TP09', 'Buffet Toi BBQ Nuong', 'AN_UONG', NULL, 'DV02', 'Tiec nuong BBQ', 1),
  ('TP10', 'Tam Khoang Nong Rung', 'KHAC', NULL, NULL, 'Ngam khoang nong 45 phut', 1),
  ('TP11', 'Truot Thac Cam Giac Manh', 'KHAC', NULL, NULL, '1 luot truot thac nuoc', 1),
  ('TP12', 'Tra Chieu Banh Ngot', 'AN_UONG', NULL, 'DV15', 'Set tra thuong ngoan', 1),
  ('TP13', 'Nuoc Suoi Dong Chai Eco', 'KHAC', NULL, NULL, '2 chai nuoc suoi', 1),
  ('TP14', 'Mu Non Luu Niem SkyGate', 'KHAC', NULL, NULL, 'Qua tang luu niem', 1),
  ('TP15', 'Giao Luu Lua Trai Co Tu', 'KHAC', NULL, NULL, 'Giao luu van nghe dem', 1),
  ('TP16', 'San Co Teambuilding', 'KHAC', NULL, NULL, 'San bai to chuc tro choi', 1),
  ('TP17', 'Ho Boi Vo Cuc Tren Nui', 'KHAC', NULL, NULL, 'Boi khong gioi han', 1),
  ('TP18', 'Trekking Rung Nguyen Sinh', 'THAM_QUAN', 'VE23', NULL, 'Trekking cung HDV', 1),
  ('TP19', 'Ruou Can Giao Luu', 'AN_UONG', NULL, 'DV11', 'Uong ruou can truyen thong', 1),
  ('TP20', 'Tham Quan Lang Nghe Co Tu', 'THAM_QUAN', 'VE21', NULL, 'Trai nghiem det tho cam', 1);

-- 15. COMBO_THANH_PHAN (20 ban ghi thanh phan combo)
INSERT INTO `COMBO_THANH_PHAN` (`MA_LOAI_VE`, `MA_THANH_PHAN`, `SO_LUONG_MOI_COMBO`, `THEO_DOI_SU_DUNG`, `GHI_CHU`) VALUES
  ('VE03', 'TP01', 1, 1, 'Ve cong nguoi lon'),
  ('VE03', 'TP02', 1, 1, 'Cap treo khuu hoi'),
  ('VE03', 'TP03', 1, 1, 'Buffet trua nguoi lon'),
  ('VE04', 'TP06', 1, 1, 'Ve cong tre em'),
  ('VE04', 'TP02', 1, 1, 'Cap treo tre em'),
  ('VE04', 'TP07', 1, 1, 'Buffet trua tre em'),
  ('VE08', 'TP01', 1, 1, 'Ve cong'),
  ('VE08', 'TP02', 1, 1, 'Cap treo'),
  ('VE08', 'TP03', 1, 1, 'Buffet trua VIP'),
  ('VE08', 'TP08', 1, 1, 'Huong dan vien'),
  ('VE13', 'TP01', 2, 1, '2 ve cong nguoi lon'),
  ('VE13', 'TP06', 1, 1, '1 ve cong tre em'),
  ('VE13', 'TP03', 2, 1, '2 suat buffet nguoi lon'),
  ('VE13', 'TP07', 1, 1, '1 suat buffet tre em'),
  ('VE14', 'TP01', 2, 1, '2 ve nguoi lon'),
  ('VE14', 'TP06', 2, 1, '2 ve tre em'),
  ('VE17', 'TP02', 1, 1, 'Cap treo'),
  ('VE17', 'TP03', 1, 1, 'Buffet trua'),
  ('VE22', 'TP01', 1, 1, 'Ve cong'),
  ('VE22', 'TP04', 1, 1, 'Xe dien 1 ngay');

-- 16. CHINH_SACH_COC & CHINH_SACH_HUY
INSERT INTO `CHINH_SACH_COC` (`MA_CHINH_SACH_COC`, `PHAM_VI_AP_DUNG`, `TY_LE_COC`, `TIEN_COC_CO_DINH`, `HIEU_LUC_TU`, `HIEU_LUC_DEN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('COC01', 'TOAN_BO', 30.00, NULL, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Dat coc 30% tong gia tri don dat'),
  ('COC02', 'PHONG', 50.00, NULL, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Dat coc 50% cho phong vao dip le tet'),
  ('COC03', 'AN_UONG', NULL, 500000.00, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Coc co dinh 500k cho ban tiec tren 10 nguoi'),
  ('COC04', 'VE', 100.00, NULL, '2026-01-01', '2026-12-31', 'DANG_AP_DUNG', 'Thanh toan 100% khi mua ve le');

INSERT INTO `CHINH_SACH_HUY` (`MA_CHINH_SACH_HUY`, `TEN_CHINH_SACH`, `SO_NGAY_TU`, `SO_NGAY_DEN`, `HANH_DONG`, `TY_LE_HOAN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('HUY01', 'Huy som truoc 14 ngay', 14, NULL, 'HOAN_TIEN', 100.00, 1, 'Hoan 100% tien da thanh toan'),
  ('HUY02', 'Huy tu 7 den 13 ngay', 7, 13, 'HOAN_TIEN', 70.00, 1, 'Hoan 70% tong tien, giu 30%'),
  ('HUY03', 'Huy tu 3 den 6 ngay', 3, 6, 'HOAN_TIEN', 50.00, 1, 'Hoan 50% tong tien, giu 50%'),
  ('HUY04', 'Huy trong vong 1 den 2 ngay', 1, 2, 'MAT_COC', 0.00, 1, 'Mat toan bo tien coc / khong hoan tien'),
  ('HUY05', 'Huy trong ngay su dung', 0, 0, 'MAT_COC', 0.00, 1, 'Khong duoc hoan tien trong ngay su dung');

-- 17. DON_DAT (25 ban ghi)
INSERT INTO `DON_DAT` (`MA_DON_DAT`, `MA_DON_HANG`, `MA_NGUOI_DUNG`, `HO_TEN_KHACH`, `SDT_KHACH`, `EMAIL_KHACH`, `GHI_CHU`, `THOI_GIAN_DAT`, `TONG_TIEN`, `TIEN_COC_YEU_CAU`, `MA_QR`, `TRANG_THAI_DON`, `TRANG_THAI_THANH_TOAN`) VALUES
  ('DD001', 'CTDG-260501-0001', 'ND008', 'Tran Anh Tuan', '0912345008', 'khach08@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-01 08:30:00', 1900000, 570000, 'QR-CTDG-0001', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD002', 'CTDG-260501-0002', 'ND009', 'Nguyen Minh Chau', '0912345009', 'khach09@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-01 09:15:00', 3250000, 975000, 'QR-CTDG-0002', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD003', 'CTDG-260502-0003', 'ND010', 'Vo Van Kiet', '0912345010', 'khach10@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-02 10:00:00', 4600000, 1380000, 'QR-CTDG-0003', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD004', 'CTDG-260502-0004', 'ND011', 'Dang Thuy Tien', '0912345011', 'khach11@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-02 11:20:00', 1450000, 435000, 'QR-CTDG-0004', 'CHO_XAC_NHAN', 'CHUA_THANH_TOAN'),
  ('DD005', 'CTDG-260503-0005', 'ND012', 'Bui Duc Thinh', '0912345012', 'khach12@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-03 14:10:00', 1900000, 570000, 'QR-CTDG-0005', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD006', 'CTDG-260503-0006', 'ND013', 'Hoang My Linh', '0912345013', 'khach13@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-03 15:45:00', 3300000, 990000, 'QR-CTDG-0006', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD007', 'CTDG-260504-0007', 'ND014', 'Ngo Quoc Bao', '0912345014', 'khach14@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-04 08:00:00', 0, 0, 'QR-CTDG-0007', 'DA_HUY', 'DA_HOAN_TIEN'),
  ('DD008', 'CTDG-260504-0008', 'ND015', 'Duong Cam Tu', '0912345015', 'khach15@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-04 10:30:00', 1200000, 360000, 'QR-CTDG-0008', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD009', 'CTDG-260505-0009', 'ND016', 'Truong Gia Binh', '0912345016', 'khach16@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-05 13:20:00', 5500000, 1650000, 'QR-CTDG-0009', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD010', 'CTDG-260505-0010', 'ND017', 'Dinh Tien Dung', '0912345017', 'khach17@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-05 16:00:00', 1640000, 492000, 'QR-CTDG-0010', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD011', 'CTDG-260506-0011', 'ND018', 'Phan Thanh Hau', '0912345018', 'khach18@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-06 09:00:00', 1750000, 525000, 'QR-CTDG-0011', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD012', 'CTDG-260506-0012', 'ND019', 'Vu Hoang Yen', '0912345019', 'khach19@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-06 11:40:00', 2400000, 720000, 'QR-CTDG-0012', 'CHO_XAC_NHAN', 'CHUA_THANH_TOAN'),
  ('DD013', 'CTDG-260507-0013', 'ND020', 'Lam Truong An', '0912345020', 'khach20@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-07 14:00:00', 1260000, 378000, 'QR-CTDG-0013', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD014', 'CTDG-260507-0014', 'ND021', 'Doan Ngoc Hai', '0912345021', 'khach21@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-07 15:30:00', 0, 0, 'QR-CTDG-0014', 'DA_HUY', 'DA_HOAN_TIEN'),
  ('DD015', 'CTDG-260508-0015', 'ND022', 'Nguyen Huu Tho', '0912345022', 'khach22@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-08 08:45:00', 2100000, 630000, 'QR-CTDG-0015', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD016', 'CTDG-260508-0016', 'ND023', 'Trinh Cong Son', '0912345023', 'khach23@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-08 10:15:00', 5200000, 1560000, 'QR-CTDG-0016', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD017', 'CTDG-260509-0017', 'ND024', 'Ha Kieu Anh', '0912345024', 'khach24@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-09 13:00:00', 1500000, 450000, 'QR-CTDG-0017', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD018', 'CTDG-260509-0018', 'ND025', 'Luong The Vinh', '0912345025', 'khach25@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-09 16:20:00', 2850000, 855000, 'QR-CTDG-0018', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD019', 'CTDG-260510-0019', 'ND008', 'Tran Anh Tuan', '0912345008', 'khach08@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-10 09:30:00', 1900000, 570000, 'QR-CTDG-0019', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD020', 'CTDG-260510-0020', 'ND009', 'Nguyen Minh Chau', '0912345009', 'khach09@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-10 11:00:00', 1800000, 540000, 'QR-CTDG-0020', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD021', 'CTDG-260511-0021', 'ND010', 'Vo Van Kiet', '0912345010', 'khach10@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-11 14:00:00', 1180000, 354000, 'QR-CTDG-0021', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD022', 'CTDG-260511-0022', 'ND011', 'Dang Thuy Tien', '0912345011', 'khach11@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-11 15:50:00', 0, 0, 'QR-CTDG-0022', 'DA_HUY', 'DA_HOAN_TIEN'),
  ('DD023', 'CTDG-260512-0023', 'ND012', 'Bui Duc Thinh', '0912345012', 'khach12@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-12 08:30:00', 2600000, 780000, 'QR-CTDG-0023', 'DA_HOAN_THANH', 'DA_THANH_TOAN'),
  ('DD024', 'CTDG-260512-0024', 'ND013', 'Hoang My Linh', '0912345013', 'khach13@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-12 10:45:00', 1360000, 408000, 'QR-CTDG-0024', 'DA_XAC_NHAN', 'DA_DAT_COC'),
  ('DD025', 'CTDG-260513-0025', 'ND014', 'Ngo Quoc Bao', '0912345014', 'khach14@gmail.com', 'Dat truc tuyen tren SkyGateHub portal', '2026-05-13 13:15:00', 2500000, 750000, 'QR-CTDG-0025', 'DA_HOAN_THANH', 'DA_THANH_TOAN');

-- 18. CHI_TIET_DON (40 ban ghi)
-- Chu y: THANH_TIEN la cot tinh toan GENERATED STORED -> KHONG dua vao danh sach cot INSERT
INSERT INTO `CHI_TIET_DON` (`MA_CHI_TIET`, `MA_DON_DAT`, `LOAI_DICH_VU`, `MA_LOAI_VE`, `MA_LOAI_PHONG`, `MA_DV_AN_UONG`, `TEN_SAN_PHAM`, `SO_LUONG`, `DON_GIA`, `TRANG_THAI`, `NGAY_SU_DUNG`) VALUES
  ('CT001', 'DD001', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 2, 350000, 'HIEU_LUC', '2026-05-02'),
  ('CT002', 'DD001', 'PHONG', NULL, 1, NULL, 'Superior DBL (FIVITEL Song Ngan)', 1, 1200000, 'HIEU_LUC', '2026-05-02'),
  ('CT003', 'DD002', 'VE', 'VE03', NULL, NULL, 'Combo Cong Troi 1 Ngay Nguoi Lon', 4, 650000, 'HIEU_LUC', '2026-05-05'),
  ('CT004', 'DD002', 'AN_UONG', NULL, NULL, 'DV03', 'Set Menu Com Lam Ga Nuong 4 Nguoi', 1, 650000, 'HIEU_LUC', '2026-05-05'),
  ('CT005', 'DD003', 'PHONG', NULL, 3, NULL, 'Deluxe DBL (FIVITEL Song Ngan)', 2, 1600000, 'HIEU_LUC', '2026-05-10'),
  ('CT006', 'DD003', 'AN_UONG', NULL, NULL, 'DV02', 'Buffet Toi Nuong BBQ Cao Nguyen', 4, 350000, 'HIEU_LUC', '2026-05-10'),
  ('CT007', 'DD004', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 3, 350000, 'HIEU_LUC', '2026-05-12'),
  ('CT008', 'DD004', 'VE', 'VE02', NULL, NULL, 'Ve Tham Quan Tre Em', 2, 200000, 'HIEU_LUC', '2026-05-12'),
  ('CT009', 'DD005', 'VE', 'VE08', NULL, NULL, 'Combo Trai Nghiem Sinh Thai VIP', 2, 950000, 'HIEU_LUC', '2026-05-08'),
  ('CT010', 'DD006', 'PHONG', NULL, 5, NULL, 'Suite DBL (FIVITEL Song Ngan)', 1, 2800000, 'HIEU_LUC', '2026-05-15'),
  ('CT011', 'DD006', 'AN_UONG', NULL, NULL, 'DV01', 'Buffet Trua Am Thuc Co Tu', 2, 250000, 'HIEU_LUC', '2026-05-15'),
  ('CT012', 'DD007', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 2, 350000, 'DA_HUY', '2026-05-20'),
  ('CT013', 'DD007', 'PHONG', NULL, 1, NULL, 'Superior DBL (FIVITEL Song Ngan)', 1, 1200000, 'DA_HUY', '2026-05-20'),
  ('CT014', 'DD008', 'VE', 'VE13', NULL, NULL, 'Combo Gia Dinh 2 Lon 1 Tre', 1, 1200000, 'HIEU_LUC', '2026-05-09'),
  ('CT015', 'DD009', 'PHONG', NULL, 9, NULL, 'Vip DBL (FIVITEL Cau Kieu)', 1, 4500000, 'HIEU_LUC', '2026-05-18'),
  ('CT016', 'DD009', 'VE', 'VE05', NULL, NULL, 'Ve Cap Treo Nguoi Lon', 4, 250000, 'HIEU_LUC', '2026-05-18'),
  ('CT017', 'DD010', 'AN_UONG', NULL, NULL, 'DV05', 'Set Menu Thit Lon Ban 7 Mon', 2, 820000, 'HIEU_LUC', '2026-05-16'),
  ('CT018', 'DD011', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 5, 350000, 'HIEU_LUC', '2026-05-11'),
  ('CT019', 'DD012', 'PHONG', NULL, 6, NULL, 'Family DBL + Single (FIVITEL Song Ngan)', 1, 2400000, 'HIEU_LUC', '2026-05-22'),
  ('CT020', 'DD013', 'VE', 'VE17', NULL, NULL, 'Combo Cap Treo Va Buffet', 2, 520000, 'HIEU_LUC', '2026-05-14'),
  ('CT021', 'DD013', 'AN_UONG', NULL, NULL, 'DV11', 'Ruou Can Co Tu Dac Biet', 1, 220000, 'HIEU_LUC', '2026-05-14'),
  ('CT022', 'DD014', 'PHONG', NULL, 15, NULL, 'Nha Moong DBL/Bungalow', 2, 850000, 'DA_HUY', '2026-05-25'),
  ('CT023', 'DD015', 'VE', 'VE03', NULL, NULL, 'Combo Cong Troi 1 Ngay Nguoi Lon', 3, 650000, 'HIEU_LUC', '2026-05-13'),
  ('CT024', 'DD015', 'VE', 'VE07', NULL, NULL, 'Ve Xe Dien Noi Khu', 3, 50000, 'HIEU_LUC', '2026-05-13'),
  ('CT025', 'DD016', 'PHONG', NULL, 8, NULL, 'Deluxe DBL (FIVITEL Cau Kieu)', 1, 5200000, 'HIEU_LUC', '2026-05-30'),
  ('CT026', 'DD017', 'AN_UONG', NULL, NULL, 'DV01', 'Buffet Trua Am Thuc Co Tu', 6, 250000, 'HIEU_LUC', '2026-05-17'),
  ('CT027', 'DD018', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 4, 350000, 'HIEU_LUC', '2026-05-19'),
  ('CT028', 'DD018', 'VE', 'VE05', NULL, NULL, 'Ve Cap Treo Nguoi Lon', 4, 250000, 'HIEU_LUC', '2026-05-19'),
  ('CT029', 'DD018', 'AN_UONG', NULL, NULL, 'DV06', 'Lau Ga La E Tay Giang', 1, 450000, 'HIEU_LUC', '2026-05-19'),
  ('CT030', 'DD019', 'PHONG', NULL, 10, NULL, 'Standard DBL (FIVITEL O Thuoc)', 2, 950000, 'HIEU_LUC', '2026-05-24'),
  ('CT031', 'DD020', 'VE', 'VE25', NULL, NULL, 'Combo All-Inclusive Sieu VIP', 1, 1800000, 'HIEU_LUC', '2026-05-21'),
  ('CT032', 'DD021', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 2, 350000, 'HIEU_LUC', '2026-05-23'),
  ('CT033', 'DD021', 'AN_UONG', NULL, NULL, 'DV04', 'Set Menu Ca Nien Rau Rung', 1, 480000, 'HIEU_LUC', '2026-05-23'),
  ('CT034', 'DD022', 'VE', 'VE03', NULL, NULL, 'Combo Cong Troi 1 Ngay Nguoi Lon', 2, 650000, 'DA_HUY', '2026-05-28'),
  ('CT035', 'DD023', 'PHONG', NULL, 11, NULL, 'Superior DBL (FIVITEL O Thuoc)', 1, 1100000, 'HIEU_LUC', '2026-05-26'),
  ('CT036', 'DD023', 'AN_UONG', NULL, NULL, 'DV14', 'Set Tiec Nuong San Vuon VIP', 1, 1500000, 'HIEU_LUC', '2026-05-26'),
  ('CT037', 'DD024', 'VE', 'VE09', NULL, NULL, 'Ve Tham Quan Suoi Hoa Nguoi Lon', 4, 280000, 'HIEU_LUC', '2026-05-29'),
  ('CT038', 'DD024', 'VE', 'VE11', NULL, NULL, 'Ve Cheo Thuyen Kayak Cay Xanh', 2, 120000, 'HIEU_LUC', '2026-05-29'),
  ('CT039', 'DD025', 'VE', 'VE01', NULL, NULL, 'Ve Tham Quan Nguoi Lon', 2, 350000, 'HIEU_LUC', '2026-05-27'),
  ('CT040', 'DD025', 'PHONG', NULL, 13, NULL, 'Deluxe DBL (FIVITEL O Thuoc)', 1, 1800000, 'HIEU_LUC', '2026-05-27');

-- 19. VE (25 ve phat hanh)
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

-- 20. DAT_PHONG (22 ban ghi)
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

-- 21. PHAN_PHONG (22 ban ghi phan bo phong)
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

-- 22. QUYEN_LOI_DON (25 ban ghi chup quyen loi combo)
INSERT INTO `QUYEN_LOI_DON` (`MA_QUYEN_LOI`, `MA_DON_DAT`, `MA_CHI_TIET`, `MA_THANH_PHAN`, `TEN_THANH_PHAN_CHUP`, `LOAI_THANH_PHAN_CHUP`, `SO_LUONG_TOI_DA`, `SO_LUONG_DA_DUNG`, `THEO_DOI_SU_DUNG`, `TRANG_THAI`) VALUES
  ('QL001', 'DD002', 'CT003', 'TP01', 'Ve Vao Cong Tham Quan', 'THAM_QUAN', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL002', 'DD002', 'CT003', 'TP02', 'Cap Treo Khuu Hoi', 'CAP_TREO', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL003', 'DD002', 'CT003', 'TP03', 'Suat Buffet Trua Tay Giang', 'AN_UONG', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL004', 'DD005', 'CT009', 'TP01', 'Ve Vao Cong Tham Quan', 'THAM_QUAN', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL005', 'DD005', 'CT009', 'TP02', 'Cap Treo Khuu Hoi', 'CAP_TREO', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL006', 'DD005', 'CT009', 'TP03', 'Suat Buffet Trua VIP', 'AN_UONG', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL007', 'DD005', 'CT009', 'TP08', 'Huong Dan Vien Rieng', 'KHAC', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL008', 'DD008', 'CT014', 'TP01', 'Ve Vao Cong 2 Nguoi Lon', 'THAM_QUAN', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL009', 'DD008', 'CT014', 'TP06', 'Ve Cong Tre Em', 'THAM_QUAN', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL010', 'DD008', 'CT014', 'TP03', 'Buffet Trua Nguoi Lon', 'AN_UONG', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL011', 'DD008', 'CT014', 'TP07', 'Buffet Trua Tre Em', 'AN_UONG', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL012', 'DD013', 'CT020', 'TP02', 'Cap Treo Khuu Hoi', 'CAP_TREO', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL013', 'DD013', 'CT020', 'TP03', 'Buffet Trua', 'AN_UONG', 2, 2, 1, 'DA_DUNG_HET'),
  ('QL014', 'DD015', 'CT023', 'TP01', 'Ve Cong Nguoi Lon', 'THAM_QUAN', 3, 3, 1, 'DA_DUNG_HET'),
  ('QL015', 'DD015', 'CT023', 'TP02', 'Cap Treo Khuu Hoi', 'CAP_TREO', 3, 3, 1, 'DA_DUNG_HET'),
  ('QL016', 'DD015', 'CT023', 'TP03', 'Buffet Trua', 'AN_UONG', 3, 3, 1, 'DA_DUNG_HET'),
  ('QL017', 'DD020', 'CT031', 'TP01', 'Ve Cong VIP', 'THAM_QUAN', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL018', 'DD020', 'CT031', 'TP02', 'Cap Treo VIP', 'CAP_TREO', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL019', 'DD020', 'CT031', 'TP03', 'Buffet Trua VIP', 'AN_UONG', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL020', 'DD020', 'CT031', 'TP04', 'Xe Dien Khong Gioi Han', 'XE_DIEN', 1, 1, 1, 'DA_DUNG_HET'),
  ('QL021', 'DD022', 'CT034', 'TP01', 'Ve Cong Nguoi Lon', 'THAM_QUAN', 2, 0, 1, 'DA_HUY'),
  ('QL022', 'DD022', 'CT034', 'TP02', 'Cap Treo Khuu Hoi', 'CAP_TREO', 2, 0, 1, 'DA_HUY'),
  ('QL023', 'DD022', 'CT034', 'TP03', 'Buffet Trua', 'AN_UONG', 2, 0, 1, 'DA_HUY'),
  ('QL024', 'DD002', 'CT003', 'TP04', 'Xe Dien Noi Khu', 'XE_DIEN', 4, 4, 1, 'DA_DUNG_HET'),
  ('QL025', 'DD015', 'CT024', 'TP04', 'Xe Dien Noi Khu', 'XE_DIEN', 3, 3, 1, 'DA_DUNG_HET');

-- 23. DAT_BAN (25 ban ghi dat ban)
INSERT INTO `DAT_BAN` (`MA_DAT_BAN`, `MA_DON_DAT`, `MA_DICH_VU`, `MA_CA_AN`, `NGAY_AN`, `SO_KHACH`, `MA_BAN_AN`, `MA_CHI_TIET`, `MA_QUYEN_LOI`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('DB001', 'DD002', 'DV03', 'CA_TRUA', '2026-05-05', 4, 'BA01', 'CT004', NULL, 'DA_HOAN_THANH', 'Ban canh cua so'),
  ('DB002', 'DD003', 'DV02', 'CA_TOI', '2026-05-10', 4, 'BA02', 'CT006', NULL, 'DA_HOAN_THANH', 'Tiec nuong ngoai troi'),
  ('DB003', 'DD006', 'DV01', 'CA_TRUA', '2026-05-15', 2, 'BA03', 'CT011', NULL, 'DA_XAC_NHAN', 'Ban 2 nguoi am cung'),
  ('DB004', 'DD010', 'DV05', 'CA_TRUA', '2026-05-16', 8, 'BA04', 'CT017', NULL, 'DA_XAC_NHAN', 'Doan 8 nguoi'),
  ('DB005', 'DD013', 'DV11', 'CA_TOI', '2026-05-14', 2, 'BA05', 'CT021', NULL, 'DA_HOAN_THANH', 'Thuong thuc ruou can'),
  ('DB006', 'DD017', 'DV01', 'CA_TRUA', '2026-05-17', 6, 'BA06', 'CT026', NULL, 'DA_HOAN_THANH', 'Buffet gia dinh'),
  ('DB007', 'DD018', 'DV06', 'CA_TOI', '2026-05-19', 4, 'BA07', 'CT029', NULL, 'DA_HOAN_THANH', 'Lau ga nong'),
  ('DB008', 'DD021', 'DV04', 'CA_TRUA', '2026-05-23', 2, 'BA08', 'CT033', NULL, 'DA_HOAN_THANH', 'Ca nien nuong'),
  ('DB009', 'DD023', 'DV14', 'CA_TOI', '2026-05-26', 2, 'BA09', 'CT036', NULL, 'DA_HOAN_THANH', 'BBQ san vuon'),
  ('DB10', 'DD002', 'DV01', 'CA_TRUA', '2026-05-05', 4, 'BA10', NULL, 'QL003', 'DA_HOAN_THANH', 'Buffet tu combo QL003'),
  ('DB11', 'DD005', 'DV01', 'CA_TRUA', '2026-05-08', 2, 'BA11', NULL, 'QL006', 'DA_HOAN_THANH', 'Buffet VIP tu combo'),
  ('DB12', 'DD008', 'DV01', 'CA_TRUA', '2026-05-09', 3, 'BA12', NULL, 'QL010', 'DA_HOAN_THANH', 'Buffet gia dinh tu combo'),
  ('DB13', 'DD013', 'DV01', 'CA_TRUA', '2026-05-14', 2, 'BA13', NULL, 'QL013', 'DA_HOAN_THANH', 'Buffet tu combo'),
  ('DB14', 'DD015', 'DV01', 'CA_TRUA', '2026-05-13', 3, 'BA14', NULL, 'QL016', 'DA_HOAN_THANH', 'Buffet tu combo'),
  ('DB15', 'DD020', 'DV01', 'CA_TRUA', '2026-05-21', 1, 'BA15', NULL, 'QL019', 'DA_HOAN_THANH', 'Buffet All-in'),
  ('DB16', 'DD002', 'DV03', 'CA_TRUA', '2026-06-05', 4, 'BA16', 'CT004', NULL, 'DA_XAC_NHAN', 'Ban dat truoc thang 6'),
  ('DB17', 'DD003', 'DV02', 'CA_TOI', '2026-06-10', 4, 'BA17', 'CT006', NULL, 'DA_XAC_NHAN', 'Ban tiec nuong'),
  ('DB18', 'DD006', 'DV01', 'CA_TRUA', '2026-06-15', 2, 'BA18', 'CT011', NULL, 'DA_XAC_NHAN', 'Dat truoc trua'),
  ('DB19', 'DD010', 'DV05', 'CA_TRUA', '2026-06-16', 4, 'BA19', 'CT017', NULL, 'DA_XAC_NHAN', 'Dat truoc lon ban'),
  ('DB20', 'DD013', 'DV11', 'CA_TOI', '2026-06-14', 2, 'BA20', 'CT021', NULL, 'DA_XAC_NHAN', 'Ruou can toi'),
  ('DB21', 'DD017', 'DV01', 'CA_TRUA', '2026-06-17', 6, 'BA21', 'CT026', NULL, 'DA_XAC_NHAN', 'Doan 6 nguoi'),
  ('DB22', 'DD018', 'DV06', 'CA_TOI', '2026-06-19', 4, 'BA22', 'CT029', NULL, 'DA_XAC_NHAN', 'Lau ga la e'),
  ('DB23', 'DD021', 'DV04', 'CA_TRUA', '2026-06-23', 2, 'BA23', 'CT033', NULL, 'DA_XAC_NHAN', 'Ca nien bua trua'),
  ('DB24', 'DD023', 'DV14', 'CA_TOI', '2026-06-26', 2, 'BA24', 'CT036', NULL, 'DA_XAC_NHAN', 'BBQ ngoai troi'),
  ('DB25', 'DD007', 'DV01', 'CA_TRUA', '2026-05-20', 2, NULL, 'CT011', NULL, 'DA_HUY', 'Dat ban da huy');

-- 24. THANH_TOAN (30 ban ghi giao dich thanh toan)
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

-- 25. HUY_DON (22 ban ghi yeu cau huy)
INSERT INTO `HUY_DON` (`MA_HUY`, `MA_NHOM_HUY`, `MA_CHI_TIET`, `MA_DON_DAT`, `MA_CHINH_SACH_HUY`, `SO_NGAY_TRUOC`, `THOI_GIAN_YEU_CAU`, `LY_DO`, `TIEN_HOAN`, `TIEN_GIU_LAI`, `TRANG_THAI_XU_LY`, `MA_NGUOI_XU_LY`) VALUES
  ('HD001', 'GRP-HUY-001', 'CT012', 'DD007', 'HUY01', 16, '2026-05-04 10:00:00', 'Khach thay doi lich trinh cong tac', 700000, 0, 'DA_XU_LY', 'ND001'),
  ('HD002', 'GRP-HUY-001', 'CT013', 'DD007', 'HUY01', 16, '2026-05-04 10:00:00', 'Khach thay doi lich trinh cong tac', 1200000, 0, 'DA_XU_LY', 'ND001'),
  ('HD003', 'GRP-HUY-002', 'CT022', 'DD014', 'HUY01', 18, '2026-05-07 16:00:00', 'Thoi tiet mua bao', 1700000, 0, 'DA_XU_LY', 'ND001'),
  ('HD004', 'GRP-HUY-003', 'CT034', 'DD022', 'HUY01', 17, '2026-05-11 16:30:00', 'Gia dinh co viec dot xuat', 1300000, 0, 'DA_XU_LY', 'ND001'),
  ('HD005', 'GRP-HUY-004', 'CT001', 'DD001', 'HUY02', 10, '2026-05-01 12:00:00', 'Yeu cau huy thu nghiem', 490000, 210000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD006', 'GRP-HUY-005', 'CT002', 'DD001', 'HUY02', 10, '2026-05-01 12:05:00', 'Yeu cau huy thu nghiem phong', 840000, 360000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD007', 'GRP-HUY-006', 'CT003', 'DD002', 'HUY03', 5, '2026-05-01 14:00:00', 'Huy 1 phan combo', 1300000, 1300000, 'DA_DUYET', 'ND001'),
  ('HD008', 'GRP-HUY-007', 'CT005', 'DD003', 'HUY01', 15, '2026-05-02 12:00:00', 'Yeu cau huy truoc han', 3200000, 0, 'DA_DUYET', 'ND001'),
  ('HD009', 'GRP-HUY-008', 'CT007', 'DD004', 'HUY04', 2, '2026-05-02 14:00:00', 'Huy sat ngay', 0, 1050000, 'DA_DUYET', 'ND001'),
  ('HD010', 'GRP-HUY-009', 'CT009', 'DD005', 'HUY01', 20, '2026-05-03 16:00:00', 'Doi sang dip khac', 1900000, 0, 'CAN_XAC_NHAN', 'ND001'),
  ('HD011', 'GRP-HUY-010', 'CT010', 'DD006', 'HUY02', 9, '2026-05-03 17:00:00', 'Giam so luong phong', 1960000, 840000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD012', 'GRP-HUY-011', 'CT014', 'DD008', 'HUY01', 14, '2026-05-04 11:00:00', 'Huy ve gia dinh', 1200000, 0, 'DA_DUYET', 'ND001'),
  ('HD013', 'GRP-HUY-012', 'CT015', 'DD009', 'HUY03', 4, '2026-05-05 14:00:00', 'Huy phong villa', 2250000, 2250000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD014', 'GRP-HUY-013', 'CT017', 'DD010', 'HUY01', 15, '2026-05-05 17:00:00', 'Huy ban tiec', 1640000, 0, 'DA_DUYET', 'ND001'),
  ('HD015', 'GRP-HUY-014', 'CT018', 'DD011', 'HUY02', 8, '2026-05-06 10:00:00', 'Huy ve tham quan', 1225000, 525000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD016', 'GRP-HUY-015', 'CT019', 'DD012', 'HUY01', 16, '2026-05-06 12:00:00', 'Huy don cho', 2400000, 0, 'DA_DUYET', 'ND001'),
  ('HD017', 'GRP-HUY-016', 'CT020', 'DD013', 'HUY04', 1, '2026-05-07 16:00:00', 'Huy combo sat ngay', 0, 1040000, 'DA_XU_LY', 'ND001'),
  ('HD018', 'GRP-HUY-017', 'CT023', 'DD015', 'HUY01', 14, '2026-05-08 11:00:00', 'Huy truoc 14 ngay', 1950000, 0, 'DA_DUYET', 'ND001'),
  ('HD019', 'GRP-HUY-018', 'CT025', 'DD016', 'HUY02', 11, '2026-05-08 12:00:00', 'Huy villa hoang hon', 3640000, 1560000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD020', 'GRP-HUY-019', 'CT026', 'DD017', 'HUY01', 15, '2026-05-09 14:00:00', 'Huy buffet', 1500000, 0, 'DA_DUYET', 'ND001'),
  ('HD021', 'GRP-HUY-020', 'CT027', 'DD018', 'HUY03', 5, '2026-05-09 17:00:00', 'Huy ve nguoi lon', 700000, 700000, 'CAN_XAC_NHAN', 'ND001'),
  ('HD022', 'GRP-HUY-021', 'CT030', 'DD019', 'HUY01', 16, '2026-05-10 10:00:00', 'Huy homestay ben suoi', 1900000, 0, 'DA_DUYET', 'ND001');

-- 26. HOAN_TIEN (15 ban ghi hoan tien tro nguoc THANH_TOAN)
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

-- 27. CHECK_IN (25 ban ghi check-in qua Cong/Quay/Nha hang)
INSERT INTO `CHECK_IN` (`MA_CHECK_IN`, `MA_DON_DAT`, `LOAI_CHECK_IN`, `MA_VE`, `MA_DAT_PHONG`, `MA_DAT_BAN`, `MA_QUYEN_LOI`, `MA_NHAN_VIEN`, `SO_LUONG`, `THOI_GIAN_CHECK_IN`, `TRANG_THAI`, `GHI_CHU`) VALUES
  ('CI001', 'DD001', 'VE', 'VE_TK001', NULL, NULL, NULL, 'ND003', 1, '2026-05-02 08:35:00', 'DA_CHECK_IN', 'Qua cong chinh Tram 1'),
  ('CI002', 'DD001', 'VE', 'VE_TK002', NULL, NULL, NULL, 'ND003', 1, '2026-05-02 08:36:00', 'DA_CHECK_IN', 'Qua cong chinh Tram 1'),
  ('CI003', 'DD001', 'PHONG', NULL, 'DP001', NULL, NULL, 'ND004', 1, '2026-05-02 14:00:00', 'DA_CHECK_IN', 'Nhan phong tai quay Le Tan Tram 2'),
  ('CI004', 'DD005', 'VE', 'VE_TK012', NULL, NULL, NULL, 'ND003', 1, '2026-05-08 09:10:00', 'DA_CHECK_IN', 'Cong VIP Tram 1'),
  ('CI005', 'DD005', 'VE', 'VE_TK013', NULL, NULL, NULL, 'ND003', 1, '2026-05-08 09:11:00', 'DA_CHECK_IN', 'Cong VIP Tram 1'),
  ('CI006', 'DD008', 'VE', 'VE_TK016', NULL, NULL, NULL, 'ND003', 1, '2026-05-09 08:50:00', 'DA_CHECK_IN', 'Cong gia dinh'),
  ('CI007', 'DD009', 'VE', 'VE_TK017', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:20:00', 'DA_CHECK_IN', 'Qua tram cap treo'),
  ('CI008', 'DD009', 'VE', 'VE_TK018', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:21:00', 'DA_CHECK_IN', 'Qua tram cap treo'),
  ('CI009', 'DD009', 'VE', 'VE_TK019', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:22:00', 'DA_CHECK_IN', 'Qua tram cap treo'),
  ('CI010', 'DD009', 'VE', 'VE_TK020', NULL, NULL, NULL, 'ND003', 1, '2026-05-18 10:23:00', 'DA_CHECK_IN', 'Qua tram cap treo'),
  ('CI011', 'DD009', 'PHONG', NULL, 'DP005', NULL, NULL, 'ND004', 1, '2026-05-18 14:15:00', 'DA_CHECK_IN', 'Nhan villa 2PN'),
  ('CI012', 'DD011', 'VE', 'VE_TK021', NULL, NULL, NULL, 'ND003', 1, '2026-05-11 09:00:00', 'DA_CHECK_IN', 'Cong 1'),
  ('CI013', 'DD011', 'VE', 'VE_TK022', NULL, NULL, NULL, 'ND003', 1, '2026-05-11 09:01:00', 'DA_CHECK_IN', 'Cong 1'),
  ('CI014', 'DD013', 'VE', 'VE_TK023', NULL, NULL, NULL, 'ND003', 1, '2026-05-14 09:15:00', 'DA_CHECK_IN', 'Cap treo Tram 1'),
  ('CI015', 'DD013', 'VE', 'VE_TK024', NULL, NULL, NULL, 'ND003', 1, '2026-05-14 09:16:00', 'DA_CHECK_IN', 'Cap treo Tram 1'),
  ('CI016', 'DD015', 'VE', 'VE_TK025', NULL, NULL, NULL, 'ND003', 1, '2026-05-13 08:40:00', 'DA_CHECK_IN', 'Cong chinh'),
  ('CI017', 'DD003', 'PHONG', NULL, 'DP002', NULL, NULL, 'ND004', 2, '2026-05-10 14:30:00', 'DA_CHECK_IN', 'Nhan 2 phong deluxe'),
  ('CI018', 'DD023', 'PHONG', NULL, 'DP010', NULL, NULL, 'ND004', 1, '2026-05-26 14:00:00', 'DA_CHECK_IN', 'Nhan glamping'),
  ('CI019', 'DD025', 'PHONG', NULL, 'DP011', NULL, NULL, 'ND004', 1, '2026-05-27 14:00:00', 'DA_CHECK_IN', 'Nhan glamping gia dinh'),
  ('CI020', 'DD002', 'AN_UONG', NULL, NULL, 'DB001', NULL, 'ND007', 4, '2026-05-05 11:30:00', 'DA_CHECK_IN', 'Vao ban an com lam ga nuong'),
  ('CI021', 'DD003', 'AN_UONG', NULL, NULL, 'DB002', NULL, 'ND007', 4, '2026-05-10 18:00:00', 'DA_CHECK_IN', 'Vao tiec BBQ toi'),
  ('CI022', 'DD013', 'AN_UONG', NULL, NULL, 'DB005', NULL, 'ND007', 2, '2026-05-14 18:30:00', 'DA_CHECK_IN', 'Uong ruou can'),
  ('CI023', 'DD017', 'AN_UONG', NULL, NULL, 'DB006', NULL, 'ND007', 6, '2026-05-17 11:45:00', 'DA_CHECK_IN', 'Buffet trua nha hang'),
  ('CI024', 'DD018', 'AN_UONG', NULL, NULL, 'DB007', NULL, 'ND007', 4, '2026-05-19 18:00:00', 'DA_CHECK_IN', 'Dung lau ga la e'),
  ('CI025', 'DD021', 'AN_UONG', NULL, NULL, 'DB008', NULL, 'ND007', 2, '2026-05-23 12:00:00', 'DA_CHECK_IN', 'Ca nien nuong trua');

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- KIEM TRA VA DOI SOAT DU LIEU SEED DATA
-- =============================================================================
SELECT 'Kiem tra tong tien DON_DAT khop voi CHI_TIET_DON co HIEU_LUC:' AS `MUC_KIEM_TRA`;
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

SELECT 'So luong ban ghi trong cac bang chinh (Tieu chi 4.1 >= 20):' AS `MUC_KIEM_TRA`;
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

# ARCHITECTURE.md — SkyGateHub (Hệ thống Quản lý & Đặt dịch vụ KDL Sinh thái Cổng Trời Đông Giang)

> **Nguồn sự thật duy nhất:** `QL_Cong_Troi_Dong_Giang_MySQL.sql` (29 bảng + View `V_DON_DAT_TONG_HOP`)
> **Stack:** Spring Boot 3 · Spring Data JPA · Thymeleaf · Bootstrap 5 · MySQL 8.x · Java 21

---

## Mục lục

1. [Tổng quan kiến trúc](#1-tổng-quan-kiến-trúc)
2. [Phân tích Bounded Context & Ánh xạ 29 bảng](#2-phân-tích-bounded-context--ánh-xạ-29-bảng)
3. [Quy chuẩn Mapping JPA cho Entity](#3-quy-chuẩn-mapping-jpa-cho-entity)
4. [Phân chia công việc cho 5 thành viên (M1–M5)](#4-phân-chia-công-việc-cho-5-thành-viên-m1m5)
5. [Thiết kế Xử lý đồng thời](#5-thiết-kế-xử-lý-đồng-thời)
6. [Thiết kế Lập trình mạng Socket](#6-thiết-kế-lập-trình-mạng-socket)
7. [Bảng đối chiếu 24 tiêu chí Rubric](#7-bảng-đối-chiếu-24-tiêu-chí-rubric)

---

## 1. Tổng quan kiến trúc

### 1.1. Kiến trúc tổng thể

```
┌─────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                          │
│  Thymeleaf + Bootstrap 5  │  GateCheckinClientApp (Java Swing)     │
└────────────────┬──────────────────────────────┬─────────────────────┘
                 │ HTTP (8080)                  │ TCP Socket (9090)
┌────────────────▼──────────────────────────────▼─────────────────────┐
│                        APPLICATION LAYER                            │
│  Spring MVC Controllers   │   CheckInSocketServer (ServerSocket)    │
│  @Controller / @RestController                                      │
└────────────────┬────────────────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────────────────┐
│                        SERVICE LAYER                                │
│  @Service  │  @Async  │  @Scheduled  │  @Transactional              │
│  Business Logic + Concurrency Control (Pessimistic Lock)            │
└────────────────┬────────────────────────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────────────────────────┐
│                       PERSISTENCE LAYER                             │
│  Spring Data JPA Repositories                                       │
│  29 Entity ↔ 29 Table · 1 View (V_DON_DAT_TONG_HOP - read-only)   │
└────────────────┬────────────────────────────────────────────────────┘
                 │ JDBC (HikariCP)
┌────────────────▼────────────────────────────────────────────────────┐
│                       DATABASE LAYER                                │
│  MySQL 8.x · Schema: QL_Cong_Troi_Dong_Giang                       │
│  CSDL là NGUỒN SỰ THẬT → ddl-auto=validate                        │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.2. Cấu trúc Package (Package-by-Layer)

Dự án được tổ chức theo kiến trúc **Package-by-Layer** (các thư mục tầng kiến trúc lớn chứa các phân hệ con):

```
com.example.skygatehub
├── SkyGateHubApplication.java          // Main class, @EnableAsync, @EnableScheduling
├── config/                             // Cấu hình hệ thống (auth, security, web...)
│   ├── auth/
│   ├── AsyncConfig.java                // ThreadPool cho @Async
│   ├── SchedulerConfig.java            // ThreadPool cho @Scheduled
│   └── AppProperties.java             // @ConfigurationProperties
│
├── common/                             // Module dùng chung
│   ├── enums/                          // Enum dùng chung (TrangThaiChung, v.v.)
│   ├── util/                           // Utility class (MaGenerator, DateUtils)
│   ├── concurrent/                     // [Tiêu chí 5.1-5.2] Module xử lý đồng thời
│   │   ├── AsyncTaskService.java       // Tác vụ nền @Async
│   │   └── ScheduledTaskService.java   // Tác vụ định kỳ @Scheduled
│   └── network/                        // [Tiêu chí 5.3-5.4] Module lập trình mạng
│       ├── server/
│       │   ├── CheckInSocketServer.java
│       │   └── CheckInClientHandler.java
│       └── client/
│           └── GateCheckinClientApp.java
│
├── controller/                         // Tầng CONTROLLER theo phân hệ
│   ├── account/                        // (M1) Quản lý tài khoản
│   ├── auth/                           // (M1) Đăng nhập, xác thực
│   ├── cart/                           // (M5) Giỏ hàng session
│   ├── checkin/                        // (M5) Quét mã check-in
│   ├── dashboard/                      // (M5) Báo cáo thống kê
│   ├── dining/                         // (M4) Ẩm thực & Đặt bàn
│   ├── order/                          // (M5) Đơn đặt & Checkout
│   ├── payment/                        // (M5) Thanh toán & Cọc
│   ├── room/                           // (M3) Quản trị phòng & Room Map
│   ├── roombooking/                    // (M2) Đặt phòng (khách hàng)
│   └── ticket/                         // (M1) Quản lý vé & Combo
│
├── dto/                                // Tầng DTO (Data Transfer Objects)
│   ├── account/
│   ├── auth/
│   ├── cancellation/
│   ├── cart/
│   ├── checkin/
│   ├── dashboard/
│   ├── dining/
│   ├── order/
│   ├── payment/
│   ├── room/                           // LoaiPhongRequest, PhongRequest, RoomMapCell...
│   ├── roombooking/                    // RoomQuoteRequest, RoomSearchRequest...
│   └── ticket/
│
├── entity/                             // Tầng ENTITY ánh xạ 29 bảng theo phân hệ
│   ├── account/                        // NguoiDung, VaiTro, NguoiDungVaiTro
│   ├── cancellation/                   // HuyDon, HoanTien
│   ├── checkin/                        // CheckIn
│   ├── dining/                         // DichVuAnUong, CaAn, BanAn, DichVuCaAn, DatBan
│   ├── order/                          // DonDat, ChiTietDon, Ve, QuyenLoiDon, VDonDatTongHop
│   ├── payment/                        // ThanhToan, ChinhSachCoc, ChinhSachHuy
│   ├── room/                           // KhuLuuTru, LoaiPhong, Phong, PhanPhong
│   ├── roombooking/                    // DatPhong
│   └── ticket/                         // LoaiVe, BangGiaVe, NgayDacBiet, KhoVe, ThanhPhanDichVu, ComboThanhPhan
│
├── enums/                              // Tầng ENUMS theo phân hệ
│   ├── account/
│   ├── auth/
│   ├── cancellation/
│   ├── dining/
│   ├── order/
│   ├── payment/
│   ├── room/
│   ├── roombooking/
│   └── ticket/
│
├── repository/                         // Tầng REPOSITORY (Spring Data JPA)
│   ├── account/
│   ├── cancellation/
│   ├── checkin/
│   ├── dining/
│   ├── order/
│   ├── payment/
│   ├── room/                           // KhuLuuTruRepository, LoaiPhongRepository...
│   ├── roombooking/
│   └── ticket/
│
└── service/                            // Tầng SERVICE & SERVICE IMPL
    ├── account/
    ├── auth/
    ├── cancellation/
    ├── cart/
    ├── checkin/
    ├── dashboard/
    ├── dining/
    ├── order/
    ├── payment/
    ├── room/
    ├── roombooking/
    └── ticket/
```

---

## 2. Phân tích Bounded Context & Ánh xạ 29 bảng

### 2.1. Bản đồ Bounded Context

```
┌────────────────────────────────────────────────────────────────────────────────┐
│  IDENTITY & ACCESS CONTEXT              TICKET & COMBO CONTEXT                │
│  ┌─────────────────────┐                ┌──────────────────────────────────┐   │
│  │ NGUOI_DUNG           │                │ LOAI_VE                          │   │
│  │ VAI_TRO              │                │ BANG_GIA_VE                      │   │
│  │ NGUOI_DUNG_VAI_TRO   │                │ NGAY_DAC_BIET                    │   │
│  └─────────────────────┘                │ KHO_VE                           │   │
│                                          │ THANH_PHAN_DICH_VU               │   │
│                                          │ COMBO_THANH_PHAN                 │   │
│                                          └──────────────────────────────────┘   │
│                                                                                 │
│  ACCOMMODATION CONTEXT                  DINING CONTEXT                         │
│  ┌─────────────────────┐                ┌──────────────────────────────────┐   │
│  │ KHU_LUU_TRU         │                │ DICH_VU_AN_UONG                  │   │
│  │ LOAI_PHONG           │                │ CA_AN                            │   │
│  │ PHONG                │                │ BAN_AN                           │   │
│  │ DAT_PHONG            │                │ DICH_VU_CA_AN                    │   │
│  │ PHAN_PHONG           │                │ DAT_BAN                          │   │
│  └─────────────────────┘                └──────────────────────────────────┘   │
│                                                                                 │
│  ORDER & FULFILLMENT CONTEXT (Aggregate Root: DON_DAT)                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐   │
│  │ DON_DAT  ──< CHI_TIET_DON ──< VE                                       │   │
│  │                            ──< QUYEN_LOI_DON                            │   │
│  │ THANH_TOAN   CHINH_SACH_COC   CHINH_SACH_HUY                           │   │
│  │ HUY_DON  ──< HOAN_TIEN                                                 │   │
│  │ CHECK_IN                                                                │   │
│  └─────────────────────────────────────────────────────────────────────────┘   │
│                                                                                 │
│  VIEW: V_DON_DAT_TONG_HOP (read-only, đối soát tiền tự động)                  │
└────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2. Vai trò chi tiết từng bảng (29 bảng)

#### IDENTITY & ACCESS CONTEXT (3 bảng)

| # | Bảng | Vai trò |
|---|------|---------|
| 1 | `NGUOI_DUNG` | Lưu thông tin tài khoản người dùng (admin, nhân viên, khách hàng). PK vật lý `ID BIGINT`, khóa nghiệp vụ `MA_NGUOI_DUNG VARCHAR(20) UNIQUE`. Mật khẩu lưu BCrypt hash trong `MAT_KHAU_HASH`. Cờ `CAN_DOI_MAT_KHAU` buộc đổi mật khẩu lần đầu đăng nhập. |
| 2 | `VAI_TRO` | Danh mục vai trò hệ thống: `QUAN_LY`, `NHAN_VIEN_VE`, `NHAN_VIEN_NHA_HANG`, `NHAN_VIEN_LUU_TRU`. |
| 3 | `NGUOI_DUNG_VAI_TRO` | Bảng liên kết M:N giữa NGUOI_DUNG và VAI_TRO. PK composite `(MA_NGUOI_DUNG, MA_VAI_TRO)`. FK trỏ vào mã nghiệp vụ, xóa User cascade xóa phân vai trò. |

#### TICKET & COMBO CONTEXT (6 bảng)

| # | Bảng | Vai trò |
|---|------|---------|
| 4 | `LOAI_VE` | Master data loại vé. `LOAI_VE ∈ {NGUOI_LON, TRE_EM, TRON_GOI, KHAC}`. Vé TRON_GOI (combo) có `GIA_VE` riêng; các thành phần được khai báo qua `COMBO_THANH_PHAN`. |
| 5 | `BANG_GIA_VE` | Bảng giá vé theo ngày/lễ/mùa. Mỗi bản ghi gắn một `MA_LOAI_VE`, có `HIEU_LUC_TU/DEN`, `UU_TIEN` (cao hơn ưu tiên hơn), `DIEU_KIEN_GIA` (MAC_DINH, NGAY_LE, CUOI_TUAN). |
| 6 | `NGAY_DAC_BIET` | Danh sách ngày lễ/sự kiện đặc biệt. Dùng để tra cứu phụ thu giá vé và giá phòng. |
| 7 | `KHO_VE` | Quản lý tồn kho vé theo từng loại và ngày tham quan. `SO_LUONG_DA_BAN <= SO_LUONG_TOI_DA`. Unique constraint `(MA_LOAI_VE, NGAY_THAM_QUAN)` đảm bảo mỗi loại vé mỗi ngày chỉ có 1 dòng kho. |
| 8 | `THANH_PHAN_DICH_VU` | Master data thành phần dịch vụ (tham quan, ăn uống, cáp treo, xe điện, khác). Mỗi thành phần có thể liên kết tới 1 `LOAI_VE` hoặc 1 `DICH_VU_AN_UONG` tùy loại. |
| 9 | `COMBO_THANH_PHAN` | Ánh xạ M:N giữa LOAI_VE (vé combo) và THANH_PHAN_DICH_VU. `SO_LUONG_MOI_COMBO` = bao nhiêu lượt mỗi combo. `THEO_DOI_SU_DUNG` = có cần đếm sử dụng hay không (chống buffet trùng). |

#### ACCOMMODATION CONTEXT (5 bảng)

| # | Bảng | Vai trò |
|---|------|---------|
| 10 | `KHU_LUU_TRU` | Master data khu lưu trú (FIVITEL Sông Ngân, FIVITEL Cầu Kiều, FIVITEL Ô Thước, Nhà Moong). `HANG_SAO`, `DIA_CHI`, `EMAIL`, `HOTLINE`, `HINH_ANH`. |
| 11 | `LOAI_PHONG` | Master data 15 loại phòng thực tế phân theo từng khu lưu trú (`MA_KHU`). `DIEN_TICH`, `LOAI_GIUONG`, `SUC_CHUA_NGUOI_LON`, `SUC_CHUA_TRE_EM`, `GIA_CO_BAN`, `HINH_ANH`. |
| 12 | `PHONG` | Danh sách 30 phòng vật lý. FK → LOAI_PHONG. `TRANG_THAI ∈ {SAN_SANG, DANG_SU_DUNG, BAO_TRI}`. Unique constraint `(MA_PHONG, MA_LOAI_PHONG)` phục vụ FK phức hợp từ PHAN_PHONG. |
| 13 | `DAT_PHONG` | Booking phòng liên kết tới `CHI_TIET_DON`. `NGAY_NHAN_PHONG < NGAY_TRA_PHONG`. `TRANG_THAI ∈ {CHO_XAC_NHAN, DA_XAC_NHAN, DA_NHAN_PHONG, DA_TRA_PHONG, DA_HUY}`. |
| 14 | `PHAN_PHONG` | Phân bổ phòng cụ thể cho booking. FK phức hợp `(MA_DAT_PHONG, MA_LOAI_PHONG) → DAT_PHONG` và `(MA_PHONG, MA_LOAI_PHONG) → PHONG` đảm bảo type-safety: chỉ gán phòng đúng loại. Unique `(MA_DAT_PHONG, MA_PHONG)` chống gán trùng. |

#### DINING CONTEXT (5 bảng)

| # | Bảng | Vai trò |
|---|------|---------|
| 15 | `DICH_VU_AN_UONG` | Master data dịch vụ ăn uống (buffet, set menu, à la carte...). |
| 16 | `CA_AN` | Ca ăn: buổi sáng/trưa/tối. `GIO_BAT_DAU/KET_THUC` tùy chọn. |
| 17 | `BAN_AN` | Bàn ăn vật lý: `SO_BAN`, `KHU_VUC`, `SUC_CHUA`. `TRANG_THAI ∈ {SAN_SANG, DANG_SU_DUNG, BAO_TRI}`. |
| 18 | `DICH_VU_CA_AN` | Bảng liên kết M:N giữa `DICH_VU_AN_UONG` và `CA_AN`: dịch vụ nào phục vụ ca nào. |
| 19 | `DAT_BAN` | Đặt bàn ăn. Có 2 nguồn: từ `CHI_TIET_DON` (đặt trực tiếp) HOẶC từ `QUYEN_LOI_DON` (combo). FK `(MA_DICH_VU, MA_CA_AN) → DICH_VU_CA_AN`. **Cột tính toán `MA_BAN_AN_DANG_GIU`**: = MA_BAN_AN khi chưa hủy, = NULL khi đã hủy → Unique `(MA_BAN_AN_DANG_GIU, NGAY_AN, MA_CA_AN)` **chống đặt trùng bàn cùng ca cùng ngày** mà vẫn cho phép tái sử dụng bàn sau khi hủy. |

#### ORDER & FULFILLMENT CONTEXT (10 bảng + 1 View)

| # | Bảng | Vai trò |
|---|------|---------|
| 20 | `DON_DAT` | **Aggregate Root** – Đơn đặt tổng hợp. `MA_DON_HANG` = mã hiển thị khách hàng. `TONG_TIEN` = tổng phải thu (cập nhật từ service, khớp 100% với SUM(CHI_TIET_DON.THANH_TIEN) khi HIEU_LUC). `TIEN_COC_YEU_CAU` = số tiền cọc. `MA_QR` = mã QR token duy nhất cho đơn. |
| 21 | `CHI_TIET_DON` | **Nguồn tiền duy nhất** của hệ thống. Mỗi dòng = 1 dịch vụ trong đơn. `LOAI_DICH_VU ∈ {VE, PHONG, AN_UONG}` + CHECK constraint đảm bảo chỉ 1 FK tương ứng != NULL. **`THANH_TIEN = SO_LUONG * DON_GIA` là GENERATED STORED** → không INSERT/UPDATE. `TRANG_THAI ∈ {HIEU_LUC, DA_HUY}`. |
| 22 | `VE` | Vé thực tế phát hành. FK composite `(MA_CHI_TIET, MA_DON_DAT) → CHI_TIET_DON`. `MA_VE_CHUOI` = chuỗi token duy nhất cho check-in. `TRANG_THAI ∈ {CHUA_SU_DUNG, DA_SU_DUNG, DA_HUY}`. |
| 23 | `QUYEN_LOI_DON` | **Chụp (snapshot) thành phần combo** tại thời điểm đặt. Chống tính trùng khi combo bao gồm buffet: `SO_LUONG_TOI_DA/DA_DUNG`, `THEO_DOI_SU_DUNG` = có đếm hay không. Khi check-in combo buffet, hệ thống trừ `SO_LUONG_DA_DUNG`. |
| 24 | `THANH_TOAN` | Ghi nhận từng đợt thanh toán. `LOAI_GIAO_DICH ∈ {DAT_COC, THANH_TOAN_DU, THANH_TOAN_CON_LAI, THANH_TOAN}`. `PHUONG_THUC ∈ {TIEN_MAT, CHUYEN_KHOAN, VNPAY, MOMO, THE_KHAC}`. **Hỗ trợ thanh toán nhiều đợt** cho 1 đơn. |
| 25 | `CHINH_SACH_COC` | Chính sách đặt cọc theo phạm vi (TOAN_BO, VE, PHONG, AN_UONG). Có tỷ lệ % hoặc mức cố định. |
| 26 | `CHINH_SACH_HUY` | Chính sách hủy theo số ngày trước ngày sử dụng. `HANH_DONG ∈ {HOAN_TIEN, MAT_COC, CAN_XAC_NHAN}`. |
| 27 | `HUY_DON` | Yêu cầu hủy **từng CHI_TIET_DON** (không hủy cả đơn 1 cục). `MA_NHOM_HUY` nhóm nhiều dòng hủy cùng lúc. Quy trình: CAN_XAC_NHAN → DA_DUYET/TU_CHOI → DA_XU_LY. `TIEN_HOAN/TIEN_GIU_LAI` tính từ chính sách. |
| 28 | `HOAN_TIEN` | Ghi nhận hoàn tiền **trỏ ngược lại THANH_TOAN gốc**. Tách biệt hoàn toàn với THANH_TOAN (1 thanh toán có thể hoàn nhiều lần). `TRANG_THAI ∈ {CHO_HOAN, DA_HOAN, THAT_BAI}`. |
| 29 | `CHECK_IN` | Check-in đa khu vực: `LOAI_CHECK_IN ∈ {VE, PHONG, AN_UONG}`. CHECK constraint đảm bảo đúng loại FK. Unique trên `MA_VE`, `MA_DAT_PHONG`, `MA_DAT_BAN` → **chống quét trùng QR**. |
| View | `V_DON_DAT_TONG_HOP` | View read-only đối soát: so sánh `TONG_TIEN` đang lưu vs `TONG_CHI_TIET_HIEU_LUC`, tính `DA_THU`, `DA_HOAN`, `DA_THU_RONG`, `CON_PHAI_THU`, `TRANG_THAI_THANH_TOAN_TINH`. |

### 2.3. Cơ chế tránh lỗi tiền bạc

```
                  Luồng tiền trong hệ thống
                  ═════════════════════════

  ┌─────────────────┐
  │  CHI_TIET_DON   │  ← NGUỒN TIỀN DUY NHẤT
  │ THANH_TIEN =    │
  │ SO_LUONG*DON_GIA│  (GENERATED STORED – DB tự tính, app không ghi)
  │ TRANG_THAI =    │
  │ HIEU_LUC/DA_HUY │
  └───────┬─────────┘
          │ SUM(THANH_TIEN) WHERE TRANG_THAI='HIEU_LUC'
          ▼
  ┌─────────────────┐
  │    DON_DAT      │
  │ TONG_TIEN =     │  ← Service cập nhật = SUM(chi tiết hiệu lực)
  │ TIEN_COC_YEU_CAU│
  └───────┬─────────┘
          │
    ┌─────┴─────┐
    ▼           ▼
  THANH_TOAN  HUY_DON ──> HOAN_TIEN
  (nhiều đợt)  (từng CT)   (trỏ THANH_TOAN gốc)

  V_DON_DAT_TONG_HOP = View kiểm chéo:
    DA_THU_RONG = SUM(THANH_TOAN.DA_THANH_TOAN) - SUM(HOAN_TIEN.DA_HOAN)
    CON_PHAI_THU = TONG_TIEN - DA_THU_RONG
```

**Nguyên tắc then chốt:**
1. **`CHI_TIET_DON.THANH_TIEN`** là cột `GENERATED ALWAYS AS (SO_LUONG * DON_GIA) STORED` → DB tự tính, Java KHÔNG ĐƯỢC INSERT/UPDATE.
2. **`DON_DAT.TONG_TIEN`** = `SUM(CHI_TIET_DON.THANH_TIEN WHERE TRANG_THAI='HIEU_LUC')` → Service layer tính và cập nhật.
3. **`QUYEN_LOI_DON`** chụp thành phần combo tại thời điểm đặt → chống tính trùng buffet: ví dụ combo có 1 suất buffet trưa, hệ thống ghi `SO_LUONG_TOI_DA=1`, khi check-in tăng `SO_LUONG_DA_DUNG`, khi đạt max thì `TRANG_THAI='DA_DUNG_HET'`.
4. **1 đơn = 1 mã QR** (`DON_DAT.MA_QR UNIQUE`). Khi quét QR, hệ thống lookup toàn bộ quyền lợi.
5. **Quy trình hủy/hoàn tiền độc lập:** `HUY_DON` hủy từng `CHI_TIET_DON` → tính `TIEN_HOAN/TIEN_GIU_LAI` theo `CHINH_SACH_HUY` → tạo `HOAN_TIEN` trỏ ngược `THANH_TOAN` gốc. Không dùng số âm trong THANH_TOAN.

### 2.4. Sơ đồ quan hệ cốt lõi (ER)

```
NGUOI_DUNG ──M:N──> VAI_TRO (qua NGUOI_DUNG_VAI_TRO)
     │
     │ 0..1
     ▼
DON_DAT ──1:N──> CHI_TIET_DON ──1:N──> VE
     │                │                
     │                ├──1:N──> QUYEN_LOI_DON
     │                │
     │                ├──1:1──> DAT_PHONG ──1:N──> PHAN_PHONG ──> PHONG
     │                │                                               │
     │                └──0..N──> HUY_DON ──> HOAN_TIEN               │
     │                                                                │
     ├──1:N──> THANH_TOAN     KHU_LUU_TRU ──1:N──> LOAI_PHONG ─────────┘
     │
     ├──1:N──> CHECK_IN
     │
     └──0..N──> DAT_BAN ──> BAN_AN
                   │
          DICH_VU_AN_UONG ──M:N──> CA_AN (qua DICH_VU_CA_AN)

LOAI_VE ──> BANG_GIA_VE
     │  ──> KHO_VE
     │  ──> COMBO_THANH_PHAN ──> THANH_PHAN_DICH_VU
```

---

## 3. Quy chuẩn Mapping JPA cho Entity

### 3.1. Nguyên tắc khóa chính & khóa nghiệp vụ

Schema sử dụng **khóa chính vật lý** `ID BIGINT UNSIGNED AUTO_INCREMENT` và **khóa nghiệp vụ** `MA_... VARCHAR(20) UNIQUE`. Tất cả khóa ngoại trong schema trỏ vào **mã nghiệp vụ `MA_...`**, không trỏ vào `ID`.

```java
@Entity
@Table(name = "TEN_BANG")
@Getter @Setter @NoArgsConstructor
public class TenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_...", length = 20, nullable = false, unique = true)
    private String ma...;
    
    // ...các cột khác
}
```

> **Lưu ý:** `BIGINT UNSIGNED` trong MySQL ánh xạ sang `Long` trong Java. Không dùng `Integer`.

### 3.2. Quy chuẩn quan hệ (Foreign Key)

Vì FK trỏ vào mã nghiệp vụ `MA_...` (không phải `ID`), bắt buộc dùng `referencedColumnName`:

```java
// Quan hệ đơn giản (1 cột FK)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE")
private LoaiVe loaiVe;

// Quan hệ composite FK (2 cột)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumns({
    @JoinColumn(name = "MA_CHI_TIET", referencedColumnName = "MA_CHI_TIET"),
    @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT")
})
private ChiTietDon chiTietDon;
```

### 3.3. Quy chuẩn Enum

Enum trong Java tương ứng 1-1 với các giá trị trong `CHECK` constraint của SQL. Sử dụng `@Enumerated(EnumType.STRING)`:

```java
// Enum
public enum TrangThaiDon {
    CHO_XAC_NHAN, DA_XAC_NHAN, DA_HOAN_THANH, DA_HUY
}

// Trong Entity
@Enumerated(EnumType.STRING)
@Column(name = "TRANG_THAI_DON", length = 30, nullable = false)
private TrangThaiDon trangThaiDon;
```

### 3.4. Cột tính toán GENERATED STORED

**2 cột** trong schema là `GENERATED ALWAYS AS ... STORED`:

1. `CHI_TIET_DON.THANH_TIEN` = `SO_LUONG * DON_GIA`
2. `DAT_BAN.MA_BAN_AN_DANG_GIU` = `CASE WHEN TRANG_THAI <> 'DA_HUY' THEN MA_BAN_AN ELSE NULL END`

Bắt buộc annotation:

```java
// CHI_TIET_DON
@Column(name = "THANH_TIEN", insertable = false, updatable = false)
private BigDecimal thanhTien;

// DAT_BAN
@Column(name = "MA_BAN_AN_DANG_GIU", insertable = false, updatable = false)
private String maBanAnDangGiu;
```

### 3.5. Bảng liên kết (Join Table) với PK composite

`NGUOI_DUNG_VAI_TRO` có PK composite `(MA_NGUOI_DUNG, MA_VAI_TRO)` → dùng `@IdClass` hoặc `@EmbeddedId`:

```java
@Entity
@Table(name = "NGUOI_DUNG_VAI_TRO")
@Getter @Setter @NoArgsConstructor
@IdClass(NguoiDungVaiTroId.class)
public class NguoiDungVaiTro {

    @Id
    @Column(name = "MA_NGUOI_DUNG", length = 20)
    private String maNguoiDung;

    @Id
    @Column(name = "MA_VAI_TRO", length = 20)
    private String maVaiTro;

    @Column(name = "NGAY_GAN")
    private LocalDateTime ngayGan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_NGUOI_DUNG", referencedColumnName = "MA_NGUOI_DUNG",
                insertable = false, updatable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_VAI_TRO", referencedColumnName = "MA_VAI_TRO",
                insertable = false, updatable = false)
    private VaiTro vaiTro;
}
```

### 3.6. Quy tắc Lombok

Chỉ sử dụng 3 annotation Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`. **KHÔNG** dùng `@Data`, `@EqualsAndHashCode`, `@ToString` (gây lazy-load issues và StackOverflow khi có quan hệ bidirectional).

### 3.7. Tổng hợp quy chuẩn

| Quy tắc | Mô tả |
|---------|-------|
| `@Id` | Luôn đặt trên trường `id` (Long) |
| `@GeneratedValue(strategy = IDENTITY)` | Vì MySQL dùng AUTO_INCREMENT |
| `@Column(name = "...")` | Tên cột **VIẾT HOA** đúng schema |
| `@ManyToOne(fetch = LAZY)` | Mặc định LAZY hết |
| `@JoinColumn(referencedColumnName = "MA_...")` | Bắt buộc vì FK trỏ mã nghiệp vụ |
| `@Enumerated(EnumType.STRING)` | Enum lưu dạng chuỗi |
| `insertable=false, updatable=false` | Cho cột GENERATED và cột trùng @Id |
| `@PrePersist` | Gán giá trị default (thay DEFAULT trong DB) |

---

## 4. Phân chia công việc cho 5 thành viên (M1–M5)

### 4.1. Ma trận phân công

```
┌──────┬────────────────────────────────────────────────────────────────────┐
│ M1   │ ACCOUNT & TICKET (8 bảng)                                        │
│      │ Quản lý Người dùng, Xác thực, Vé tham quan, Combo, Bảng giá      │
│      │ ─────────────────────────────────────────────────────────────────  │
│      │ Bảng: NGUOI_DUNG, VAI_TRO, NGUOI_DUNG_VAI_TRO,                   │
│      │       LOAI_VE, BANG_GIA_VE, NGAY_DAC_BIET, KHO_VE,               │
│      │       THANH_PHAN_DICH_VU, COMBO_THANH_PHAN                        │
│      │ Package: account/, ticket/                                        │
│      │ Trọn vẹn: Entity → Repository → Service → Controller → View      │
├──────┼────────────────────────────────────────────────────────────────────┤
│ M2   │ ROOM BOOKING - Phía Khách hàng (1 bảng chính)                    │
│      │ Đặt phòng: tìm phòng trống, tính phụ thu ngày lễ, giỏ hàng      │
│      │ ─────────────────────────────────────────────────────────────────  │
│      │ Bảng: DAT_PHONG                                                   │
│      │ Package: roombooking/                                             │
│      │ Tham chiếu đọc: LOAI_PHONG, PHONG, NGAY_DAC_BIET, BANG_GIA_VE   │
├──────┼────────────────────────────────────────────────────────────────────┤
│ M3   │ ROOM OPS - Phía Quản trị (4 bảng)                                │
│      │ Quản lý khu lưu trú, loại phòng, phòng, phân phòng, Room Map     │
│      │ ─────────────────────────────────────────────────────────────────  │
│      │ Bảng: KHU_LUU_TRU, LOAI_PHONG, PHONG, PHAN_PHONG                 │
│      │ Package: controller/room, service/room, repository/room, entity/room│
│      │ Tham chiếu đọc: DAT_PHONG                                        │
├──────┼────────────────────────────────────────────────────────────────────┤
│ M4   │ DINING (5 bảng)                                                   │
│      │ Ẩm thực, Bàn ăn, Ca ăn, Đặt bàn                                 │
│      │ ─────────────────────────────────────────────────────────────────  │
│      │ Bảng: DICH_VU_AN_UONG, CA_AN, BAN_AN, DICH_VU_CA_AN, DAT_BAN    │
│      │ Package: dining/                                                  │
├──────┼────────────────────────────────────────────────────────────────────┤
│ M5   │ ORDER & FULFILLMENT (10 bảng + module Socket)                     │
│      │ Đơn đặt, Chi tiết, Vé, Quyền lợi, Thanh toán, Hủy/Hoàn tiền,   │
│      │ Check-in đa khu vực, Dashboard, Giỏ hàng Session, Socket Server  │
│      │ ─────────────────────────────────────────────────────────────────  │
│      │ Bảng: DON_DAT, CHI_TIET_DON, VE, QUYEN_LOI_DON, THANH_TOAN,     │
│      │       CHINH_SACH_COC, CHINH_SACH_HUY, HUY_DON, HOAN_TIEN,       │
│      │       CHECK_IN                                                    │
│      │ Package: order/, payment/, cancellation/, checkin/, common/       │
└──────┴────────────────────────────────────────────────────────────────────┘
```

### 4.2. Chi tiết nhiệm vụ từng thành viên

#### M1 — Quản lý Người dùng, Xác thực & Vé tham quan

**Chức năng chính:**
- CRUD NguoiDung (admin tạo tài khoản, đổi mật khẩu buộc đầu đăng nhập)
- Quản lý vai trò (gán/thu hồi)
- Đăng nhập/Đăng xuất (Spring Security cơ bản hoặc Filter + Session)
- CRUD LOAI_VE (bao gồm vé combo)
- CRUD BANG_GIA_VE (giá theo mùa/lễ, ưu tiên)
- CRUD NGAY_DAC_BIET
- Khai báo THANH_PHAN_DICH_VU & COMBO_THANH_PHAN cho vé trọn gói
- Quản lý KHO_VE (xem tồn, điều chỉnh tối đa)

**View Thymeleaf:**
- Trang đăng nhập, đổi mật khẩu
- Trang quản lý danh sách người dùng (admin)
- Trang quản lý loại vé & bảng giá
- Trang quản lý ngày đặc biệt
- Trang cấu hình combo (kéo thả thành phần)

#### M2 — Đặt phòng (Khách hàng)

**Chức năng chính:**
- Tìm kiếm phòng trống theo khoảng ngày (check-in/check-out) + Khu lưu trú + loại phòng + số khách
- Hiển thị giá theo đêm (phụ thu ngày lễ tra từ NGAY_DAC_BIET + BANG_GIA_VE)
- Tạo DAT_PHONG liên kết vào CHI_TIET_DON → thêm vào giỏ hàng session
- Xem lịch sử đặt phòng của tài khoản

**View Thymeleaf:**
- Trang tìm kiếm phòng (form: ngày nhận/trả, khu lưu trú, loại phòng, số khách)
- Trang kết quả tìm kiếm (danh sách loại phòng còn trống theo từng khu, giá)
- Trang chi tiết loại phòng (kèm hình ảnh, kiểu giường, diện tích, sức chứa người lớn/trẻ em)
- Trang lịch sử đặt phòng

#### M3 — Vận hành phòng (Quản trị)

**Chức năng chính:**
- CRUD KHU_LUU_TRU (tên khu, hạng sao, hotline, email, địa chỉ, ảnh đại diện)
- CRUD LOAI_PHONG (tên, khu lưu trú, diện tích, kiểu giường, sức chứa người lớn/trẻ em, giá cơ bản, hình ảnh)
- CRUD PHONG (số phòng, gắn loại, trạng thái SAN_SANG/BAO_TRI)
- PHAN_PHONG: Gán phòng cụ thể cho booking đã xác nhận
- **Room Map:** Sơ đồ trạng thái phòng trực quan theo ngày (calendar grid):
  - Trục X = ngày, trục Y = phòng
  - Ô = trạng thái (trống/đã đặt/đang ở/bảo trì)
  - Nhân viên có thể kéo thả gán phòng

**View Thymeleaf:**
- Trang quản lý khu lưu trú
- Trang quản lý loại phòng
- Trang quản lý phòng
- Trang Room Map (bảng lưới ngày × phòng)
- Trang phân phòng (danh sách booking chờ gán)

#### M4 — Ẩm thực, Bàn ăn & Ca ăn

**Chức năng chính:**
- CRUD DICH_VU_AN_UONG (tên, loại, giá, mô tả)
- CRUD CA_AN (ca sáng/trưa/tối, giờ)
- Liên kết DICH_VU_CA_AN (dịch vụ nào phục vụ ca nào)
- CRUD BAN_AN (số bàn, khu vực, sức chứa, trạng thái)
- DAT_BAN: Đặt bàn ăn (từ đơn hoặc từ quyền lợi combo)
- Xem lịch đặt bàn theo ngày + ca

**View Thymeleaf:**
- Trang quản lý menu (dịch vụ ăn uống)
- Trang quản lý ca ăn
- Trang quản lý bàn ăn (sơ đồ bàn theo khu vực)
- Trang đặt bàn (chọn ngày/ca/bàn/số khách)
- Trang lịch đặt bàn (calendar view theo ca)

#### M5 — Đơn đặt tổng hợp, Thanh toán & Check-in

**Chức năng chính:**
- **Giỏ hàng Session:** Gom các dịch vụ (vé + phòng + ăn uống) vào session → khi checkout tạo DON_DAT + CHI_TIET_DON + sinh VE + sinh QUYEN_LOI_DON
- **Tạo đơn & tính tổng:** TONG_TIEN = SUM(CHI_TIET_DON.THANH_TIEN WHERE HIEU_LUC)
- **Sinh mã QR:** @Async sinh MA_QR token unique cho đơn (sử dụng thư viện ZXing)
- **Thanh toán nhiều đợt:** THANH_TOAN (đặt cọc lần 1, thanh toán còn lại lần 2...)
- **CHINH_SACH_COC:** Tính tiền cọc theo chính sách
- **HUY_DON:** Hủy từng chi tiết, áp CHINH_SACH_HUY → tạo HOAN_TIEN
- **CHECK_IN:** Quét QR → lookup đơn → check-in vé/phòng/ăn uống
- **Dashboard:** Thống kê doanh thu, số đơn, tỷ lệ check-in, top dịch vụ

**View Thymeleaf:**
- Trang giỏ hàng (session cart)
- Trang checkout (xác nhận đơn, chọn phương thức thanh toán)
- Trang chi tiết đơn (khách hàng/admin)
- Trang danh sách đơn (admin)
- Trang thanh toán (chọn đợt, phương thức)
- Trang hủy đơn (chọn chi tiết cần hủy, xem chính sách)
- Trang hoàn tiền (admin duyệt)
- Trang check-in (quét QR hoặc nhập mã)
- Trang dashboard (charts: doanh thu theo tháng, top dịch vụ)

---

## 5. Thiết kế Xử lý đồng thời (Tiêu chí 5.1 & 5.2)

### 5.1. Tác vụ nền @Async và @Scheduled

#### Cấu hình Thread Pool

```java
// config/AsyncConfig.java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("SKG-ASYNC-");
        executor.initialize();
        return executor;
    }
}

// config/SchedulerConfig.java
@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("SKG-SCHED-");
        scheduler.initialize();
        registrar.setTaskScheduler(scheduler);
    }
}
```

#### Tác vụ @Async: Sinh mã QR & Gửi thông báo

```java
// common/concurrent/AsyncTaskService.java
@Service
public class AsyncTaskService {

    private static final Logger log = LoggerFactory.getLogger(AsyncTaskService.class);

    @Async
    public CompletableFuture<String> generateQrToken(String maDonDat) {
        log.info("[THREAD: {}] Bắt đầu sinh QR cho đơn: {}",
                 Thread.currentThread().getName(), maDonDat);
        
        String token = UUID.randomUUID().toString();
        // ... logic sinh QR image bằng ZXing ...
        
        log.info("[THREAD: {}] Hoàn tất sinh QR cho đơn: {} → token: {}",
                 Thread.currentThread().getName(), maDonDat, token);
        return CompletableFuture.completedFuture(token);
    }

    @Async
    public void sendOrderConfirmationEmail(String email, String maDonHang) {
        log.info("[THREAD: {}] Gửi email xác nhận đơn {} tới {}",
                 Thread.currentThread().getName(), maDonHang, email);
        // ... gửi email ...
        log.info("[THREAD: {}] Đã gửi email xác nhận đơn {}",
                 Thread.currentThread().getName(), maDonHang);
    }
}
```

#### Tác vụ @Scheduled: Rà soát đơn hết hạn giữ chỗ

```java
// common/concurrent/ScheduledTaskService.java
@Service
public class ScheduledTaskService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private DonDatRepository donDatRepository;

    /**
     * Chạy mỗi 5 phút: tìm đơn CHO_XAC_NHAN quá 30 phút chưa thanh toán → tự động hủy
     */
    @Scheduled(fixedRate = 300_000) // 5 phút
    @Transactional
    public void autoExpireUnpaidOrders() {
        log.info("[THREAD: {}] ═══ Bắt đầu rà soát đơn hết hạn giữ chỗ ═══",
                 Thread.currentThread().getName());
        
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);
        List<DonDat> expired = donDatRepository
            .findByTrangThaiDonAndThoiGianDatBefore(
                TrangThaiDon.CHO_XAC_NHAN, cutoff);
        
        for (DonDat don : expired) {
            don.setTrangThaiDon(TrangThaiDon.DA_HUY);
            log.info("[THREAD: {}] Hủy đơn hết hạn: {} (đặt lúc {})",
                     Thread.currentThread().getName(),
                     don.getMaDonDat(), don.getThoiGianDat());
        }
        
        log.info("[THREAD: {}] ═══ Kết thúc rà soát: đã hủy {} đơn ═══",
                 Thread.currentThread().getName(), expired.size());
    }

    /**
     * Chạy mỗi ngày lúc 00:00: kiểm tra VE quá ngày tham quan chưa sử dụng → đánh dấu hết hạn
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoExpireUnusedTickets() {
        log.info("[THREAD: {}] ═══ Rà soát vé hết hạn ═══",
                 Thread.currentThread().getName());
        // ... logic ...
    }
}
```

### 5.2. Đồng bộ hóa tài nguyên dùng chung (Pessimistic Lock)

#### Chống đặt trùng phòng cuối cùng

```java
// roombooking/repository/DatPhongRepository.java
public interface DatPhongRepository extends JpaRepository<DatPhong, Long> {

    /**
     * Đếm số phòng đã đặt cho loại phòng + khoảng ngày, có khóa bi quan
     * → chống 2 khách đặt cùng lúc căn phòng cuối cùng
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COUNT(dp) FROM DatPhong dp " +
           "WHERE dp.maLoaiPhong = :maLoaiPhong " +
           "AND dp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG') " +
           "AND dp.ngayNhanPhong < :ngayTra AND dp.ngayTraPhong > :ngayNhan")
    long countOverlappingBookingsForUpdate(
        @Param("maLoaiPhong") String maLoaiPhong,
        @Param("ngayNhan") LocalDate ngayNhan,
        @Param("ngayTra") LocalDate ngayTra);
}
```

#### Chống quét trùng mã QR check-in vé

```java
// checkin/repository/CheckInRepository.java
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    /**
     * Kiểm tra vé đã check-in chưa, có khóa bi quan
     * → chống 2 trạm quét QR cùng 1 vé đồng thời
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Ve v WHERE v.maVeChuoi = :token")
    Optional<Ve> findVeByTokenForUpdate(@Param("token") String token);
}
```

#### Quy trình đặt phòng an toàn (Service)

```java
// roombooking/service/impl/RoomBookingServiceImpl.java
@Service
public class RoomBookingServiceImpl implements RoomBookingService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public DatPhong bookRoom(BookRoomRequest req) {
        // 1. Khóa bi quan: đếm phòng đã đặt
        long booked = datPhongRepository.countOverlappingBookingsForUpdate(
            req.getMaLoaiPhong(), req.getNgayNhan(), req.getNgayTra());
        
        // 2. Kiểm tra còn phòng
        long totalRooms = phongRepository.countByMaLoaiPhongAndTrangThai(
            req.getMaLoaiPhong(), TrangThaiPhong.SAN_SANG);
        
        if (booked + req.getSoLuongPhong() > totalRooms) {
            throw new InsufficientRoomException("Không đủ phòng trống!");
        }
        
        // 3. Tạo booking (an toàn vì đã khóa)
        DatPhong dp = new DatPhong();
        // ... set fields ...
        return datPhongRepository.save(dp);
    }
}
```

---

## 6. Thiết kế Lập trình mạng Socket (Tiêu chí 5.3 & 5.4)

### 6.1. Kiến trúc tổng thể

```
  ┌──────────────────────────────────────────────────────────────┐
  │              Spring Boot Application (port 8080)              │
  │                                                              │
  │  ┌─────────────────────────────────────────────────────────┐ │
  │  │  CheckInSocketServer (TCP ServerSocket port 9090)       │ │
  │  │  - Chạy ngầm trong Spring Boot (@PostConstruct)         │ │
  │  │  - ExecutorService pool = 5 threads                     │ │
  │  │  - Mỗi client → 1 CheckInClientHandler thread           │ │
  │  │                                                         │ │
  │  │  ┌───────────────┐  ┌───────────────┐  ┌─────────────┐ │ │
  │  │  │ Handler #1    │  │ Handler #2    │  │ Handler #3  │ │ │
  │  │  │ (Cổng vào)    │  │ (Khu lưu trú) │  │ (Nhà hàng) │ │ │
  │  │  └───────────────┘  └───────────────┘  └─────────────┘ │ │
  │  └─────────────────────────────────────────────────────────┘ │
  └──────────────────────────────────────────────────────────────┘
              ▲ TCP          ▲ TCP           ▲ TCP
              │              │               │
  ┌───────────┴──┐  ┌───────┴──────┐  ┌─────┴──────────┐
  │ GateCheckin   │  │ GateCheckin   │  │ GateCheckin     │
  │ ClientApp #1  │  │ ClientApp #2  │  │ ClientApp #3    │
  │ (Swing)       │  │ (Swing)       │  │ (Swing)         │
  │ Trạm Cổng Vào│  │ Trạm Lưu Trú │  │ Trạm Nhà Hàng  │
  └───────────────┘  └──────────────┘  └─────────────────┘
```

### 6.2. Đặc tả giao thức bản tin Text

Mỗi bản tin là **1 dòng text** kết thúc bằng `\n`. Format: `COMMAND|param1|param2|...`

| Lệnh | Client → Server | Server → Client |
|-------|----------------|-----------------|
| `AUTH` | `AUTH\|username\|password` | `AUTH_OK\|MA_NHAN_VIEN\|HO_TEN` hoặc `AUTH_FAIL\|Lỗi` |
| `LOOKUP` | `LOOKUP\|maQrHoacMaDon` | `LOOKUP_OK\|JSON{đơn,chiTiet,vé,quyềnLợi}` hoặc `LOOKUP_FAIL\|Lỗi` |
| `CHECKIN_VE` | `CHECKIN_VE\|maVeChuoi\|maNhanVien` | `CHECKIN_VE_OK\|maCheckIn\|tenVe` hoặc `CHECKIN_VE_FAIL\|Lỗi` |
| `CHECKIN_PHONG` | `CHECKIN_PHONG\|maDatPhong\|maNhanVien` | `CHECKIN_PHONG_OK\|maCheckIn\|soPhong` hoặc `CHECKIN_PHONG_FAIL\|Lỗi` |
| `CHECKIN_AN` | `CHECKIN_AN\|maDatBanHoacMaQuyenLoi\|maNhanVien\|loaiNguon` | `CHECKIN_AN_OK\|maCheckIn` hoặc `CHECKIN_AN_FAIL\|Lỗi` |
| `STATS` | `STATS` | `STATS_OK\|JSON{tongCheckInHomNay,veCheckedIn,phongCheckedIn,...}` |
| `QUIT` | `QUIT` | `BYE` (đóng kết nối) |

### 6.3. CheckInSocketServer

```java
// common/network/server/CheckInSocketServer.java
@Component
public class CheckInSocketServer {

    private static final Logger log = LoggerFactory.getLogger(CheckInSocketServer.class);
    private static final int PORT = 9090;
    private static final int MAX_CLIENTS = 5;

    @Autowired private ApplicationContext springContext;
    private ExecutorService pool;
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        pool = Executors.newFixedThreadPool(MAX_CLIENTS, r -> {
            Thread t = new Thread(r);
            t.setName("SKG-SOCKET-" + t.getId());
            t.setDaemon(true);
            return t;
        });

        Thread serverThread = new Thread(this::acceptClients, "SKG-SERVER-MAIN");
        serverThread.setDaemon(true);
        serverThread.start();
        log.info("[THREAD: {}] CheckInSocketServer khởi động trên port {}",
                 Thread.currentThread().getName(), PORT);
    }

    private void acceptClients() {
        try {
            serverSocket = new ServerSocket(PORT);
            log.info("[THREAD: {}] Đang lắng nghe kết nối...",
                     Thread.currentThread().getName());
            while (running) {
                Socket client = serverSocket.accept();
                log.info("[THREAD: {}] Client kết nối: {}",
                         Thread.currentThread().getName(), client.getRemoteSocketAddress());
                CheckInClientHandler handler = springContext.getBean(CheckInClientHandler.class);
                handler.setClientSocket(client);
                pool.submit(handler);
            }
        } catch (IOException e) {
            if (running) log.error("Lỗi server socket", e);
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        pool.shutdownNow();
        try { serverSocket.close(); } catch (Exception ignored) {}
        log.info("CheckInSocketServer đã dừng.");
    }
}
```

### 6.4. CheckInClientHandler

```java
// common/network/server/CheckInClientHandler.java
@Component
@Scope("prototype") // Mỗi client 1 instance
public class CheckInClientHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CheckInClientHandler.class);
    
    @Autowired private CheckInService checkInService;
    @Autowired private VeRepository veRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;
    
    private Socket clientSocket;
    private String authenticatedUser;

    public void setClientSocket(Socket socket) { this.clientSocket = socket; }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        try (BufferedReader in = new BufferedReader(
                 new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                 new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            
            log.info("[THREAD: {}] Xử lý client: {}", threadName, clientSocket.getRemoteSocketAddress());
            String line;
            while ((line = in.readLine()) != null) {
                log.info("[THREAD: {}] Nhận: {}", threadName, line);
                String response = processCommand(line);
                out.println(response);
                log.info("[THREAD: {}] Gửi: {}", threadName, response);
                if ("BYE".equals(response)) break;
            }
        } catch (IOException e) {
            log.warn("[THREAD: {}] Client ngắt kết nối: {}", threadName, e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (Exception ignored) {}
            log.info("[THREAD: {}] Đã đóng kết nối client", threadName);
        }
    }

    private String processCommand(String line) {
        String[] parts = line.split("\\|");
        String cmd = parts[0].toUpperCase();
        return switch (cmd) {
            case "AUTH"          -> handleAuth(parts);
            case "LOOKUP"        -> handleLookup(parts);
            case "CHECKIN_VE"    -> handleCheckinVe(parts);
            case "CHECKIN_PHONG" -> handleCheckinPhong(parts);
            case "CHECKIN_AN"    -> handleCheckinAn(parts);
            case "STATS"         -> handleStats();
            case "QUIT"          -> "BYE";
            default              -> "ERROR|Lệnh không hợp lệ: " + cmd;
        };
    }
    
    // ... implement từng handler method ...
}
```

### 6.5. GateCheckinClientApp (Java Swing)

```java
// common/network/client/GateCheckinClientApp.java
public class GateCheckinClientApp extends JFrame {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9090;

    private JTextArea logArea;
    private JTextField inputField;
    private JLabel statusLabel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public GateCheckinClientApp(String stationName) {
        super("Trạm Check-in: " + stationName);
        initUI(stationName);
    }

    private void initUI(String stationName) {
        setSize(700, 500);
        setLayout(new BorderLayout(5, 5));

        // Panel trên: nút kết nối, trạng thái
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnConnect = new JButton("Kết nối Server");
        statusLabel = new JLabel("⚪ Chưa kết nối");
        topPanel.add(btnConnect);
        topPanel.add(statusLabel);
        add(topPanel, BorderLayout.NORTH);

        // Khu vực log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Panel dưới: input + các nút chức năng
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnLookup = new JButton("Tra cứu đơn");
        JButton btnCheckinVe = new JButton("Check-in Vé");
        JButton btnCheckinPhong = new JButton("Check-in Phòng");
        JButton btnCheckinAn = new JButton("Check-in Ăn uống");
        JButton btnStats = new JButton("Thống kê");
        btnPanel.add(btnLookup);
        btnPanel.add(btnCheckinVe);
        btnPanel.add(btnCheckinPhong);
        btnPanel.add(btnCheckinAn);
        btnPanel.add(btnStats);
        bottomPanel.add(inputField, BorderLayout.NORTH);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event handlers
        btnConnect.addActionListener(e -> connectToServer());
        btnLookup.addActionListener(e -> sendCommand("LOOKUP|" + inputField.getText().trim()));
        btnCheckinVe.addActionListener(e -> sendCommand("CHECKIN_VE|" + inputField.getText().trim()));
        // ... các nút khác tương tự ...
    }

    // ... connect, send, receive methods ...

    /**
     * Main: mở cùng lúc 3 trạm check-in độc lập
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GateCheckinClientApp("Cổng Vào").setVisible(true);
            new GateCheckinClientApp("Khu Lưu Trú").setVisible(true);
            new GateCheckinClientApp("Nhà Hàng").setVisible(true);
        });
    }
}
```

---

## 7. Bảng đối chiếu 24 tiêu chí Rubric

### Nhóm 1: Lập trình hướng đối tượng (OOP) — 2,0 điểm

| TC | Tiêu chí | Điểm | Giải pháp |
|----|----------|------|-----------|
| 1.1 | Khai báo class & thuộc tính | 0,4 | 28 Entity class (`account/entity/`, `ticket/entity/`, ...). Mỗi class có `private` fields, `@Getter @Setter`. Tuân thủ Java Naming Convention. |
| 1.2 | Getter/Setter, Constructor | 0,4 | Lombok `@Getter @Setter @NoArgsConstructor` trên tất cả Entity. Custom constructor trong DTO nếu cần. |
| 1.3 | Kế thừa | 0,4 | `BaseEntity` (abstract class chứa `id`, `ngayTao`, `ngayCapNhat`) → các entity kế thừa. Repository kế thừa `JpaRepository<T, Long>`. Service interface → ServiceImpl. |
| 1.4 | Đa hình & Interface | 0,4 | `PaymentGateway` interface → `VnPayGateway`, `MomoGateway`, `MockGateway`. `CheckInStrategy` interface → `VeCheckInStrategy`, `PhongCheckInStrategy`, `AnUongCheckInStrategy`. Service interface → ServiceImpl. |
| 1.5 | Package & Encapsulation | 0,4 | Package-by-Feature: `account/`, `ticket/`, `room/`, `roombooking/`, `dining/`, `order/`, `payment/`, `cancellation/`, `checkin/`, `common/`. Mỗi package có `entity/`, `repository/`, `service/`, `controller/`. |

### Nhóm 2: Xử lý ngoại lệ & Collections — 1,6 điểm

| TC | Tiêu chí | Điểm | Giải pháp |
|----|----------|------|-----------|
| 2.1 | Try-catch, custom exception | 0,4 | `InsufficientRoomException`, `TicketSoldOutException`, `OrderNotFoundException`, `InvalidCheckInException`, `PaymentFailedException`. Global `@ControllerAdvice` + `@ExceptionHandler`. |
| 2.2 | Nhiều loại exception | 0,4 | `BusinessException` (base) → `ResourceNotFoundException`, `ConflictException`, `ValidationException`. Socket handler bắt `IOException`, `SocketTimeoutException`. |
| 2.3 | Collections (List, Map, Set) | 0,4 | `List<ChiTietDon>` trong service. `Map<LocalDate, List<RoomMapCell>>` cho Room Map. `Set<String>` mã vé đã check-in. `Queue<String>` pending notifications. |
| 2.4 | Duyệt, sort, filter | 0,4 | Stream API: `chiTietList.stream().filter(ct -> ct.getTrangThai() == HIEU_LUC).mapToDouble(...).sum()`. Sorting: `rooms.sort(Comparator.comparing(Phong::getSoPhong))`. |

### Nhóm 3: Thiết kế giao diện & UX — 2,0 điểm

| TC | Tiêu chí | Điểm | Giải pháp |
|----|----------|------|-----------|
| 3.1 | Layout, component cơ bản | 0,4 | Bootstrap 5 Navbar, Sidebar admin, Card grid, Data table, Form. Thymeleaf fragment layout (`_layout.html`, `_navbar.html`, `_sidebar.html`). |
| 3.2 | Xử lý sự kiện | 0,4 | Form submit, button click, AJAX search phòng, modal confirm hủy, date picker change → recalculate price. Thymeleaf `th:action`, `th:onclick`. JavaScript event handlers. |
| 3.3 | Form hợp lệ, validate | 0,4 | Bean Validation: `@NotBlank`, `@Size`, `@Min`, `@Email`, `@Pattern`. Thymeleaf `th:errors`. JavaScript client-side validation. Custom `@ValidDateRange`. |
| 3.4 | Thiết kế trực quan, UX tốt | 0,4 | Dashboard charts (Chart.js), Room Map grid, Calendar view đặt bàn, Toast notification, Loading spinner, Responsive. Dark/Light mode toggle. |
| 3.5 | Tích hợp CSS framework | 0,4 | Bootstrap 5.3+ CDN. Custom CSS theme cho SkyGateHub. Bootstrap Icons. Responsive breakpoints. |

### Nhóm 4: Truy cập dữ liệu & CRUD — 2,0 điểm

| TC | Tiêu chí | Điểm | Giải pháp |
|----|----------|------|-----------|
| 4.1 | CSDL ≥ 5 bảng, ≥ 20 bản ghi/bảng | 0,5 | **29 bảng** trong MySQL. File `02_seed_data.sql` có ≥20 bản ghi cho mỗi bảng chính. |
| 4.2 | CRUD đầy đủ | 0,5 | CRUD cho tất cả master data: NGUOI_DUNG, LOAI_VE, BANG_GIA_VE, LOAI_PHONG, PHONG, DICH_VU_AN_UONG, BAN_AN, CA_AN. Soft-delete (đổi TRANG_THAI). |
| 4.3 | Tìm kiếm, lọc | 0,5 | Tìm phòng trống theo khoảng ngày. Lọc đơn theo trạng thái/ngày. Tìm vé theo mã QR. Lọc bàn trống theo ca/ngày. Full-text search tên dịch vụ. |
| 4.4 | Phân trang, sắp xếp | 0,5 | `Pageable` + `Page<T>` trên tất cả danh sách admin. Thymeleaf pagination component. Sort by column header click. |

### Nhóm 5: Đa luồng & Mạng — 1,6 điểm

| TC | Tiêu chí | Điểm | Giải pháp |
|----|----------|------|-----------|
| 5.1 | Thread/Runnable/ExecutorService | 0,4 | `AsyncConfig` (ThreadPoolTaskExecutor), `CheckInSocketServer` (ExecutorService, newFixedThreadPool). `CheckInClientHandler` implements `Runnable`. Log `[THREAD: ...]`. |
| 5.2 | Đồng bộ hóa, tránh race condition | 0,4 | `@Lock(PESSIMISTIC_WRITE)` trên repository queries. `@Transactional(isolation = SERIALIZABLE)` cho booking phòng. Unique constraint DB cho check-in vé. |
| 5.3 | Socket Server phục vụ ≥ 3 client | 0,4 | `CheckInSocketServer` (TCP ServerSocket port 9090), `ExecutorService` pool=5, đồng thời tối thiểu 3 `CheckInClientHandler`. |
| 5.4 | Socket Client kết nối, giao thức rõ ràng | 0,4 | `GateCheckinClientApp` (Java Swing). Giao thức text: AUTH, LOOKUP, CHECKIN_VE/PHONG/AN, STATS, QUIT. Nút mở 3 trạm cùng lúc. |

### Nhóm 6: Tài liệu & Trình bày — 0,8 điểm

| TC | Tiêu chí | Điểm | Giải pháp |
|----|----------|------|-----------|
| 6.1 | Tài liệu kỹ thuật | 0,2 | `ARCHITECTURE.md` (file này), `README.md`, Javadoc trên Service interface. |
| 6.2 | Hướng dẫn cài đặt & chạy | 0,2 | `README.md`: hướng dẫn MySQL setup, Maven build, Spring Boot run, Socket client run. |
| 6.3 | Trình bày demo | 0,2 | Kịch bản demo: Đặt vé → Đặt phòng → Đặt bàn → Checkout → Thanh toán → Check-in (3 trạm Socket đồng thời). |
| 6.4 | Phân công & quản lý dự án | 0,2 | Bảng phân công M1–M5 (mục 4). Git workflow: `main` + `dev` + `feature/m1-account`, `feature/m2-booking`... |

### Tổng điểm tối đa: 10,0 / 10,0

---

## Phụ lục A: Danh sách Enum ↔ CHECK constraint

| Enum Java | Bảng.Cột | Giá trị |
|-----------|----------|---------|
| `LoaiVeType` | LOAI_VE.LOAI_VE | `NGUOI_LON, TRE_EM, TRON_GOI, KHAC` |
| `TrangThaiPhong` | PHONG.TRANG_THAI | `SAN_SANG, DANG_SU_DUNG, BAO_TRI` |
| `TrangThaiBanAn` | BAN_AN.TRANG_THAI | `SAN_SANG, DANG_SU_DUNG, BAO_TRI` |
| `TrangThaiDon` | DON_DAT.TRANG_THAI_DON | `CHO_XAC_NHAN, DA_XAC_NHAN, DA_HOAN_THANH, DA_HUY` |
| `TrangThaiThanhToanDon` | DON_DAT.TRANG_THAI_THANH_TOAN | `CHUA_THANH_TOAN, DA_DAT_COC, DA_THANH_TOAN, DA_HOAN_TIEN` |
| `LoaiDichVuDon` | CHI_TIET_DON.LOAI_DICH_VU | `VE, PHONG, AN_UONG` |
| `TrangThaiChiTiet` | CHI_TIET_DON.TRANG_THAI | `HIEU_LUC, DA_HUY` |
| `TrangThaiVe` | VE.TRANG_THAI | `CHUA_SU_DUNG, DA_SU_DUNG, DA_HUY` |
| `TrangThaiDatPhong` | DAT_PHONG.TRANG_THAI | `CHO_XAC_NHAN, DA_XAC_NHAN, DA_NHAN_PHONG, DA_TRA_PHONG, DA_HUY` |
| `TrangThaiPhanPhong` | PHAN_PHONG.TRANG_THAI | `DANG_PHAN, DA_NHAN_PHONG, DA_TRA_PHONG, DA_HUY` |
| `LoaiThanhPhan` | THANH_PHAN_DICH_VU.LOAI_THANH_PHAN | `THAM_QUAN, AN_UONG, CAP_TREO, XE_DIEN, KHAC` |
| `TrangThaiQuyenLoi` | QUYEN_LOI_DON.TRANG_THAI | `HIEU_LUC, DA_HUY, DA_DUNG_HET` |
| `TrangThaiDatBan` | DAT_BAN.TRANG_THAI | `CHO_XAC_NHAN, DA_XAC_NHAN, DA_CHECK_IN, DA_HOAN_THANH, DA_HUY` |
| `LoaiGiaoDich` | THANH_TOAN.LOAI_GIAO_DICH | `DAT_COC, THANH_TOAN_DU, THANH_TOAN_CON_LAI, THANH_TOAN` |
| `PhuongThucThanhToan` | THANH_TOAN.PHUONG_THUC_THANH_TOAN | `TIEN_MAT, CHUYEN_KHOAN, VNPAY, MOMO, THE_KHAC` |
| `TrangThaiThanhToan` | THANH_TOAN.TRANG_THAI | `CHO_THANH_TOAN, DA_THANH_TOAN, THAT_BAI, DA_HUY` |
| `PhamViCoc` | CHINH_SACH_COC.PHAM_VI_AP_DUNG | `TOAN_BO, VE, PHONG, AN_UONG` |
| `TrangThaiChinhSachCoc` | CHINH_SACH_COC.TRANG_THAI | `CAN_XAC_NHAN, DANG_AP_DUNG, NGUNG_AP_DUNG` |
| `HanhDongHuy` | CHINH_SACH_HUY.HANH_DONG | `HOAN_TIEN, MAT_COC, CAN_XAC_NHAN` |
| `TrangThaiHuyDon` | HUY_DON.TRANG_THAI_XU_LY | `CHO_XU_LY, CAN_XAC_NHAN, DA_DUYET, TU_CHOI, DA_XU_LY` |
| `TrangThaiHoanTien` | HOAN_TIEN.TRANG_THAI | `CHO_HOAN, DA_HOAN, THAT_BAI` |
| `LoaiCheckIn` | CHECK_IN.LOAI_CHECK_IN | `VE, PHONG, AN_UONG` |
| `TrangThaiCheckIn` | CHECK_IN.TRANG_THAI | `DA_CHECK_IN, DA_HUY` |
| `DieuKienGia` | BANG_GIA_VE.DIEU_KIEN_GIA | `MAC_DINH` + các giá trị tùy chỉnh |

---

## Phụ lục B: Quy ước đặt tên

| Thành phần | Quy ước | Ví dụ |
|-----------|---------|-------|
| Entity class | PascalCase, tên Việt | `DonDat`, `ChiTietDon`, `BangGiaVe` |
| Repository | `{Entity}Repository` | `DonDatRepository` |
| Service interface | `{Feature}Service` | `OrderService`, `CheckInService` |
| Service impl | `{Feature}ServiceImpl` | `OrderServiceImpl` |
| Controller | `{Feature}Controller` / `Admin{Feature}Controller` | `OrderController`, `AdminRoomController` |
| Enum | PascalCase mô tả | `TrangThaiDon`, `LoaiCheckIn` |
| DTO | `{Tên}Request` / `{Tên}Response` | `BookRoomRequest`, `OrderSummaryResponse` |
| Package | lowercase-by-feature | `com.example.skygatehub.order` |
| Thymeleaf view | `{feature}/{action}.html` | `order/list.html`, `room/map.html` |

---

*Cập nhật lần cuối: Sprint 0 — Kiến trúc nền tảng*

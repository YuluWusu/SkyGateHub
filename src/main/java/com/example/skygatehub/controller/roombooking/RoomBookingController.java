package com.example.skygatehub.controller.roombooking;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.util.OrderCodeGenerator;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RoomBookingController {

    private final KhuLuuTruRepository khuLuuTruRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final PhongRepository phongRepository;
    private final DonDatRepository donDatRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final DatPhongRepository datPhongRepository;

    @GetMapping({"/khach-san", "/rooms"})
    public String showHotelAndRooms(
            @RequestParam(value = "ngayNhan", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhan,
            @RequestParam(value = "ngayTra", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayTra,
            @RequestParam(value = "maKhu", required = false) Integer maKhu,
            @RequestParam(value = "soKhach", required = false) Integer soKhach,
            Model model) {

        LocalDate checkIn = (ngayNhan != null && !ngayNhan.isBefore(LocalDate.now())) ? ngayNhan : LocalDate.now();
        LocalDate checkOut = (ngayTra != null) ? ngayTra : checkIn.plusDays(1);
        if (!checkOut.isAfter(checkIn)) {
            checkOut = checkIn.plusDays(1);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        model.addAttribute("ngayNhan", checkIn.format(inputFmt));
        model.addAttribute("ngayTra", checkOut.format(inputFmt));
        model.addAttribute("displayNgayNhan", checkIn.format(dtf));
        model.addAttribute("displayNgayTra", checkOut.format(dtf));
        model.addAttribute("selectedMaKhu", maKhu);
        model.addAttribute("soKhach", soKhach);

        // Danh sách phân khu lưu trú (FIVITEL Sông Ngân, Cầu Kiều, Ô Thước, Làng Moong...)
        List<KhuLuuTru> khuList = khuLuuTruRepository.findAllByOrderByMaKhuAsc();
        model.addAttribute("khuList", khuList);

        // Lấy danh sách hạng phòng
        List<LoaiPhong> rawRoomTypes;
        if (maKhu != null) {
            rawRoomTypes = loaiPhongRepository.findByKhuLuuTru_MaKhuAndTrangThai(maKhu, true);
        } else {
            rawRoomTypes = loaiPhongRepository.findByTrangThai(true);
        }

        // Lọc theo số lượng khách nếu có
        List<LoaiPhong> filteredRoomTypes = new ArrayList<>();
        Map<String, Integer> availableCountMap = new HashMap<>();

        for (LoaiPhong lp : rawRoomTypes) {
            if (soKhach != null && soKhach > 0) {
                int capacity = (lp.getSucChuaNguoiLon() != null ? lp.getSucChuaNguoiLon() : 2)
                        + (lp.getSucChuaTreEm() != null ? lp.getSucChuaTreEm() : 0);
                if (capacity < soKhach) {
                    continue;
                }
            }

            // Tính số phòng trống khả dụng trong khoảng [checkIn, checkOut)
            List<Phong> availableRooms = phongRepository.findAvailableRooms(lp.getMaLoaiPhong(), checkIn, checkOut);
            availableCountMap.put(lp.getMaLoaiPhong(), availableRooms.size());
            filteredRoomTypes.add(lp);
        }

        model.addAttribute("roomTypes", filteredRoomTypes);
        model.addAttribute("availableCountMap", availableCountMap);

        return "roombooking/customer/search";
    }

    @GetMapping("/khach-san/chi-tiet/{id}")
    public String showRoomDetail(@PathVariable("id") Long id, Model model) {
        LoaiPhong loaiPhong = loaiPhongRepository.findById(id).orElse(null);
        if (loaiPhong == null) {
            return "redirect:/khach-san";
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        List<Phong> availableRooms = phongRepository.findAvailableRooms(loaiPhong.getMaLoaiPhong(), today, tomorrow);

        model.addAttribute("roomType", loaiPhong);
        model.addAttribute("availableCount", availableRooms.size());
        model.addAttribute("today", today);
        model.addAttribute("tomorrow", tomorrow);

        return "roombooking/customer/results";
    }

    /**
     * API tiếp nhận yêu cầu đặt phòng nhanh từ Modal khách hàng.
     * Lưu vào DonDat, ChiTietDon, DatPhong với trạng thái CHO_XAC_NHAN để hiển thị ngay trên:
     * 1) /admin/orders (Quản lý đơn hàng)
     * 2) /admin/room-map (Cột "Đơn Chờ Phân Phòng" để lễ tân xếp phòng vật lý)
     */
    @PostMapping("/khach-san/dat-nhanh")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> quickBookRoom(
            @RequestParam("hoTenKhach") String hoTenKhach,
            @RequestParam("sdtKhach") String sdtKhach,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            @RequestParam("maLoaiPhong") String maLoaiPhong,
            @RequestParam(value = "ngayNhan", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhan,
            @RequestParam(value = "ngayTra", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayTra) {

        Map<String, Object> response = new HashMap<>();

        if (hoTenKhach == null || hoTenKhach.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Vui lòng nhập họ và tên người đại diện!");
            return ResponseEntity.badRequest().body(response);
        }

        if (sdtKhach == null || !sdtKhach.trim().matches("^(0[35789])[0-9]{8}$")) {
            response.put("success", false);
            response.put("message", "Số điện thoại không hợp lệ! Vui lòng nhập số điện thoại Việt Nam gồm đúng 10 chữ số (bắt đầu bằng 03, 05, 07, 08, 09).");
            return ResponseEntity.badRequest().body(response);
        }

        LocalDate checkIn = (ngayNhan != null && !ngayNhan.isBefore(LocalDate.now())) ? ngayNhan : LocalDate.now();
        LocalDate checkOut = (ngayTra != null) ? ngayTra : checkIn.plusDays(1);
        if (!checkOut.isAfter(checkIn)) {
            checkOut = checkIn.plusDays(1);
        }

        LoaiPhong lp = loaiPhongRepository.findByMaLoaiPhong(maLoaiPhong).orElse(null);
        if (lp == null) {
            response.put("success", false);
            response.put("message", "Không tìm thấy thông tin hạng phòng đã chọn!");
            return ResponseEntity.badRequest().body(response);
        }

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) nights = 1;
        BigDecimal basePrice = lp.getGiaCoBan() != null ? lp.getGiaCoBan() : BigDecimal.ZERO;
        BigDecimal totalPerRoom = basePrice.multiply(BigDecimal.valueOf(nights));

        String maDonHang = OrderCodeGenerator.generateMaDonHang("CTDG");
        String maDonDat = OrderCodeGenerator.generateMaDonDat();

        DonDat donDat = new DonDat();
        donDat.setMaDonDat(maDonDat);
        donDat.setMaDonHang(maDonHang);
        donDat.setHoTenKhach(hoTenKhach.trim());
        donDat.setSdtKhach(sdtKhach.trim());
        donDat.setGhiChu(ghiChu != null && !ghiChu.trim().isEmpty() ? ghiChu.trim() : null);
        donDat.setThoiGianDat(LocalDateTime.now());
        donDat.setTrangThaiDon(TrangThaiDon.CHO_XAC_NHAN);
        donDat.setTrangThaiThanhToan(TrangThaiThanhToanDon.CHUA_THANH_TOAN);
        donDat.setTongTien(totalPerRoom);
        donDat.setTienCocYeuCau(totalPerRoom.multiply(BigDecimal.valueOf(0.3)).setScale(0, RoundingMode.HALF_UP));
        donDat.setMaQr("QR-" + maDonHang);
        donDat.setNgayCapNhat(LocalDateTime.now());
        donDatRepository.save(donDat);

        ChiTietDon ctd = new ChiTietDon();
        ctd.setMaChiTiet(OrderCodeGenerator.generateMaChiTiet());
        ctd.setDonDat(donDat);
        ctd.setMaDonDat(donDat.getMaDonDat());
        ctd.setLoaiDichVu(LoaiDichVuDon.PHONG);
        ctd.setLoaiPhong(lp);
        ctd.setTenSanPham(lp.getTenLoaiPhong() + " (" + nights + " đêm)");
        ctd.setSoLuong(1);
        ctd.setDonGia(totalPerRoom);
        ctd.setNgaySuDung(checkIn);
        ctd.setTrangThai(TrangThaiChiTiet.HIEU_LUC);
        chiTietDonRepository.save(ctd);

        DatPhong dp = new DatPhong();
        dp.setMaDatPhong(OrderCodeGenerator.generateMaDatPhong());
        dp.setChiTietDon(ctd);
        dp.setLoaiPhong(lp);
        dp.setNgayNhanPhong(checkIn);
        dp.setNgayTraPhong(checkOut);
        dp.setSoLuongPhong(1);
        dp.setSoKhach(lp.getSucChuaNguoiLon() != null ? lp.getSucChuaNguoiLon() : 2);
        dp.setTrangThai(TrangThaiDatPhong.CHO_XAC_NHAN);
        datPhongRepository.save(dp);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        response.put("success", true);
        response.put("message", "Gửi yêu cầu đặt phòng thành công!");
        response.put("maDonHang", maDonHang);
        response.put("maDatPhong", dp.getMaDatPhong());
        response.put("hoTenKhach", hoTenKhach.trim());
        response.put("tenPhong", lp.getTenLoaiPhong());
        response.put("tongTien", totalPerRoom);
        response.put("checkIn", checkIn.format(dtf));
        response.put("checkOut", checkOut.format(dtf));
        response.put("soDem", nights);
        return ResponseEntity.ok(response);
    }
}

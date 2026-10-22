package com.example.skygatehub.controller.roombooking;

import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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

    @GetMapping({"/khach-san", "/rooms"})
    public String showHotelAndRooms(
            @RequestParam(value = "ngayNhan", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhan,
            @RequestParam(value = "ngayTra", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayTra,
            @RequestParam(value = "maKhu", required = false) Integer maKhu,
            @RequestParam(value = "soKhach", required = false) Integer soKhach,
            Model model) {

        LocalDate checkIn = (ngayNhan != null) ? ngayNhan : LocalDate.now();
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
}

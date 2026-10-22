package com.example.skygatehub.controller.room;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.room.PendingAssignmentResponse;
import com.example.skygatehub.dto.room.RoomAssignRequest;
import com.example.skygatehub.dto.room.RoomMapCell;
import com.example.skygatehub.dto.room.RoomMapSummaryDto;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.service.room.LoaiPhongService;
import com.example.skygatehub.service.room.RoomOperationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/room-map")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_LUU_TRU", "VT006"})
@RequiredArgsConstructor
@Slf4j
public class AdminRoomMapController {

    private final RoomOperationService roomOperationService;
    private final LoaiPhongService loaiPhongService;

    @GetMapping("")
    public String showRoomMap(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "maKhu", required = false) Integer maKhu,
            @RequestParam(value = "maLoaiPhong", required = false) String maLoaiPhong,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        List<RoomMapCell> cells = roomOperationService.buildRoomMap(targetDate, maKhu, maLoaiPhong, status);
        RoomMapSummaryDto summary = roomOperationService.calculateRoomMapSummary(targetDate, maKhu, maLoaiPhong);
        List<PendingAssignmentResponse> pendingBookings = roomOperationService.getPendingBookingsForAssignment(targetDate);

        List<KhuLuuTru> khuList = loaiPhongService.getAllKhuLuuTru();
        List<LoaiPhong> loaiPhongList = loaiPhongService.getAllLoaiPhong();

        // Nhóm cells theo Khu lưu trú để render từng khu riêng biệt (giữ thứ tự phân khu)
        Map<String, List<RoomMapCell>> cellsByKhu = new java.util.LinkedHashMap<>();
        for (RoomMapCell c : cells) {
            String kName = c.getTenKhu() != null ? c.getTenKhu() : "Khu vực chung";
            cellsByKhu.computeIfAbsent(kName, k -> new ArrayList<>()).add(c);
        }

        model.addAttribute("targetDate", targetDate);
        model.addAttribute("targetDateFormatted", targetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("selectedMaKhu", maKhu);
        model.addAttribute("selectedMaLoaiPhong", maLoaiPhong);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("cells", cells);
        model.addAttribute("cellsByKhu", cellsByKhu);
        model.addAttribute("summary", summary);
        model.addAttribute("pendingBookings", pendingBookings);

        model.addAttribute("khuList", khuList);
        model.addAttribute("loaiPhongList", loaiPhongList);
        model.addAttribute("assignRequest", new RoomAssignRequest());

        return "room/admin/room-map";
    }

    @GetMapping("/available-rooms")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAvailableRooms(@RequestParam("maDatPhong") String maDatPhong) {
        try {
            List<Phong> availableRooms = roomOperationService.getAvailablePhysicalRoomsForBooking(maDatPhong);
            List<Map<String, Object>> response = new ArrayList<>();
            for (Phong p : availableRooms) {
                Map<String, Object> item = new HashMap<>();
                item.put("maPhong", p.getMaPhong());
                item.put("soPhong", p.getSoPhong());
                item.put("tenLoaiPhong", p.getLoaiPhong().getTenLoaiPhong());
                item.put("giaCoBan", p.getLoaiPhong().getGiaCoBan());
                item.put("loaiGiuong", p.getLoaiPhong().getLoaiGiuong());
                response.add(item);
            }
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Lỗi lấy danh sách phòng trống cho đơn {}: {}", maDatPhong, ex.getMessage());
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @PostMapping("/assign")
    public String assignRoom(
            @Valid @ModelAttribute("assignRequest") RoomAssignRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return buildRedirectUrl(returnDate);
        }

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : null;

        try {
            roomOperationService.assignRoomToBooking(request.getMaDatPhong(), request.getMaPhong(), staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Phân phòng thành công cho đơn " + request.getMaDatPhong() + " vào phòng " + request.getMaPhong());
        } catch (Exception ex) {
            log.warn("Lỗi phân phòng: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate);
    }

    @PostMapping("/unassign")
    public String unassignRoom(
            @RequestParam("maPhanPhong") String maPhanPhong,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            RedirectAttributes redirectAttributes) {

        try {
            roomOperationService.unassignRoom(maPhanPhong);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Hủy phân phòng [" + maPhanPhong + "] thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi hủy phân phòng: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate);
    }

    @PostMapping("/check-in")
    public String checkIn(
            @RequestParam("maDatPhong") String maDatPhong,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : null;

        try {
            String rooms = roomOperationService.checkInRoom(maDatPhong, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Check-in nhận phòng thành công cho đơn [" + maDatPhong + "]. Phòng: " + rooms);
        } catch (Exception ex) {
            log.warn("Lỗi Check-in: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate);
    }

    @PostMapping("/check-out")
    public String checkOut(
            @RequestParam("maDatPhong") String maDatPhong,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : null;

        try {
            String rooms = roomOperationService.checkOutRoom(maDatPhong, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Trả phòng (Check-out) thành công cho đơn [" + maDatPhong + "]. Các phòng [" + rooms + "] đã trở về trạng thái SẴN SÀNG.");
        } catch (Exception ex) {
            log.warn("Lỗi Check-out: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate);
    }

    private String buildRedirectUrl(String returnDate) {
        if (returnDate != null && !returnDate.trim().isEmpty()) {
            return "redirect:/admin/room-map?date=" + returnDate.trim();
        }
        return "redirect:/admin/room-map";
    }
}

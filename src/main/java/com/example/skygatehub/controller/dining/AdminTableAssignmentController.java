package com.example.skygatehub.controller.dining;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.dining.MealScheduleRow;
import com.example.skygatehub.dto.dining.TableAssignRequest;
import com.example.skygatehub.dto.dining.TableMapCell;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.service.dining.TableAssignmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin/dining")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_NHA_HANG", "VT005"})
@RequiredArgsConstructor
@Slf4j
public class AdminTableAssignmentController {

    private final TableAssignmentService tableAssignmentService;

    @GetMapping({"/table-assignment", "/table-assignments"})
    public String showTableAssignment(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "caAn", required = false) String caAn,
            @RequestParam(value = "khuVuc", required = false) String khuVuc,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        List<TableMapCell> cells = tableAssignmentService.buildTableMap(targetDate, caAn, khuVuc, status);
        List<DatBan> pendingBookings = tableAssignmentService.getPendingBookings(targetDate, caAn);
        List<CaAn> caAnList = tableAssignmentService.getAllCaAn();
        List<String> khuVucList = tableAssignmentService.getAllKhuVuc();

        // Nhóm cells theo Khu vực
        Map<String, List<TableMapCell>> cellsByKhu = new LinkedHashMap<>();
        for (TableMapCell c : cells) {
            String kName = (c.getKhuVuc() != null && !c.getKhuVuc().isEmpty()) ? c.getKhuVuc() : "Khu vực chung";
            cellsByKhu.computeIfAbsent(kName, k -> new ArrayList<>()).add(c);
        }

        // Thống kê nhanh KPI bàn
        long tongBan = cells.size();
        long banTrong = cells.stream().filter(TableMapCell::isTrong).count();
        long banChoKhach = cells.stream().filter(TableMapCell::isDaXep).count();
        long banDangAn = cells.stream().filter(TableMapCell::isDangAn).count();
        long banBaoTri = cells.stream().filter(TableMapCell::isBaoTri).count();

        model.addAttribute("targetDate", targetDate);
        model.addAttribute("targetDateFormatted", targetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("selectedCaAn", caAn);
        model.addAttribute("selectedKhuVuc", khuVuc);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("cells", cells);
        model.addAttribute("cellsByKhu", cellsByKhu);
        model.addAttribute("pendingBookings", pendingBookings);
        model.addAttribute("caAnList", caAnList);
        model.addAttribute("khuVucList", khuVucList);

        model.addAttribute("tongBan", tongBan);
        model.addAttribute("banTrong", banTrong);
        model.addAttribute("banChoKhach", banChoKhach);
        model.addAttribute("banDangAn", banDangAn);
        model.addAttribute("banBaoTri", banBaoTri);

        model.addAttribute("assignRequest", new TableAssignRequest());

        return "dining/admin/table-assignment";
    }

    /**
     * AJAX lấy danh sách bàn ăn còn trống cho đơn đặt bàn
     */
    @GetMapping("/available-tables")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAvailableTables(@RequestParam("maDatBan") String maDatBan) {
        try {
            List<BanAn> available = tableAssignmentService.getAvailableTablesForBooking(maDatBan);
            List<Map<String, Object>> response = new ArrayList<>();
            for (BanAn b : available) {
                Map<String, Object> item = new HashMap<>();
                item.put("maBanAn", b.getMaBanAn());
                item.put("soBan", b.getSoBan());
                item.put("khuVuc", b.getKhuVuc());
                item.put("sucChua", b.getSucChua());
                response.add(item);
            }
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Lỗi lấy bàn trống cho đơn đặt bàn {}: {}", maDatBan, ex.getMessage());
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    /**
     * Gán bàn ăn cho đơn đặt bàn
     */
    @PostMapping("/assign")
    public String assignTable(
            @Valid @ModelAttribute("assignRequest") TableAssignRequest request,
            BindingResult bindingResult,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            @RequestParam(value = "returnCaAn", required = false) String returnCaAn,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return buildRedirectUrl(returnDate, returnCaAn);
        }

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : null;

        try {
            tableAssignmentService.assignTableToBooking(request.getMaDatBan(), request.getMaBanAn(), staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Xếp bàn thành công cho đơn [" + request.getMaDatBan() + "] vào bàn " + request.getMaBanAn());
        } catch (Exception ex) {
            log.warn("Lỗi phân bàn: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate, returnCaAn);
    }

    /**
     * Hủy xếp bàn
     */
    @PostMapping("/unassign")
    public String unassignTable(
            @RequestParam("maDatBan") String maDatBan,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            @RequestParam(value = "returnCaAn", required = false) String returnCaAn,
            RedirectAttributes redirectAttributes) {

        try {
            tableAssignmentService.unassignTable(maDatBan);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Hủy gán bàn cho đơn [" + maDatBan + "] thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi hủy xếp bàn: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate, returnCaAn);
    }

    /**
     * Check-in nhận bàn khi khách đến nhà hàng
     */
    @PostMapping("/check-in")
    public String checkInTable(
            @RequestParam("maDatBan") String maDatBan,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            @RequestParam(value = "returnCaAn", required = false) String returnCaAn,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : null;

        try {
            String soBan = tableAssignmentService.checkInTable(maDatBan, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Check-in nhận bàn thành công cho đơn [" + maDatBan + "]. Bàn: " + soBan);
        } catch (Exception ex) {
            log.warn("Lỗi Check-in bàn: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate, returnCaAn);
    }

    /**
     * Hoàn tất bữa ăn và trả bàn
     */
    @PostMapping("/complete")
    public String completeTable(
            @RequestParam("maDatBan") String maDatBan,
            @RequestParam(value = "returnDate", required = false) String returnDate,
            @RequestParam(value = "returnCaAn", required = false) String returnCaAn,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : null;

        try {
            String soBan = tableAssignmentService.completeTable(maDatBan, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Trả bàn thành công cho đơn [" + maDatBan + "]. Bàn [" + soBan + "] đã trở về trạng thái SẴN SÀNG.");
        } catch (Exception ex) {
            log.warn("Lỗi trả bàn: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return buildRedirectUrl(returnDate, returnCaAn);
    }

    @GetMapping({"/meal-schedule", "/meal-schedules"})
    public String showMealSchedule(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<MealScheduleRow> schedule = tableAssignmentService.getMealSchedule(targetDate);

        int tongKhachNgay = schedule.stream().mapToInt(MealScheduleRow::getTongSoKhach).sum();
        int tongDonNgay = schedule.stream().mapToInt(MealScheduleRow::getTongDonDat).sum();

        model.addAttribute("targetDate", targetDate);
        model.addAttribute("targetDateFormatted", targetDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("schedule", schedule);
        model.addAttribute("tongKhachNgay", tongKhachNgay);
        model.addAttribute("tongDonNgay", tongDonNgay);

        return "dining/admin/meal-schedule";
    }

    private String buildRedirectUrl(String returnDate, String returnCaAn) {
        StringBuilder sb = new StringBuilder("redirect:/admin/dining/table-assignment");
        boolean hasQuery = false;
        if (returnDate != null && !returnDate.trim().isEmpty()) {
            sb.append("?date=").append(returnDate.trim());
            hasQuery = true;
        }
        if (returnCaAn != null && !returnCaAn.trim().isEmpty()) {
            sb.append(hasQuery ? "&" : "?").append("caAn=").append(returnCaAn.trim());
        }
        return sb.toString();
    }
}

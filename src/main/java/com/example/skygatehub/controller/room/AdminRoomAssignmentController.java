package com.example.skygatehub.controller.room;

import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.room.PendingAssignmentResponse;
import com.example.skygatehub.service.room.RoomOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/room-assignments")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_LUU_TRU", "VT006"})
@RequiredArgsConstructor
public class AdminRoomAssignmentController {

    private final RoomOperationService roomOperationService;

    @GetMapping("")
    public String showAssignmentQueue(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        List<PendingAssignmentResponse> pendingList = roomOperationService.getPendingBookingsForAssignment(targetDate);

        model.addAttribute("targetDate", targetDate);
        model.addAttribute("pendingList", pendingList);

        // Chuyển hướng trực tiếp sang Sơ đồ phòng tích hợp hoặc render trang danh sách phân phòng
        return "redirect:/admin/room-map?date=" + targetDate;
    }
}

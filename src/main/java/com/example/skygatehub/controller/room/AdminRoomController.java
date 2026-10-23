package com.example.skygatehub.controller.room;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.room.PhongRequest;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.service.room.LoaiPhongService;
import com.example.skygatehub.service.room.PhongService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_LUU_TRU", "VT006"})
@RequiredArgsConstructor
@Slf4j
public class AdminRoomController {

    private final PhongService phongService;
    private final LoaiPhongService loaiPhongService;

    @GetMapping("")
    public String listRooms(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "maKhu", required = false) Integer maKhu,
            @RequestParam(value = "maLoaiPhong", required = false) String maLoaiPhong,
            @RequestParam(value = "trangThai", required = false) TrangThaiPhong trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "soPhong,asc") String sortParam,
            Model model) {

        String[] sortParts = sortParam.split(",");
        String sortField = sortParts[0];
        if (!"soPhong".equals(sortField) && !"maPhong".equals(sortField) && !"trangThai".equals(sortField) && !"id".equals(sortField)) {
            sortField = "soPhong";
        }
        Sort.Direction direction = (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, Math.max(15, size), Sort.by(direction, sortField));
        Page<Phong> roomsPage = phongService.searchPhong(maKhu, maLoaiPhong, trangThai, keyword, pageable);

        List<KhuLuuTru> khuList = loaiPhongService.getAllKhuLuuTru();
        List<LoaiPhong> loaiPhongList = loaiPhongService.getAllLoaiPhong();

        model.addAttribute("roomsPage", roomsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomsPage.getTotalPages());
        model.addAttribute("totalElements", roomsPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedMaKhu", maKhu);
        model.addAttribute("selectedMaLoaiPhong", maLoaiPhong);
        model.addAttribute("selectedTrangThai", trangThai);
        model.addAttribute("sortParam", sortParam);

        model.addAttribute("khuList", khuList);
        model.addAttribute("loaiPhongList", loaiPhongList);
        model.addAttribute("trangThaiList", TrangThaiPhong.values());

        return "room/admin/room-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        PhongRequest request = new PhongRequest();
        request.setTrangThai(TrangThaiPhong.SAN_SANG);

        model.addAttribute("roomRequest", request);
        model.addAttribute("loaiPhongList", loaiPhongService.getActiveLoaiPhong());
        model.addAttribute("trangThaiList", TrangThaiPhong.values());
        model.addAttribute("isEdit", false);
        return "room/admin/room-form";
    }

    @PostMapping("/create")
    public String createRoom(
            @Valid @ModelAttribute("roomRequest") PhongRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loaiPhongList", loaiPhongService.getActiveLoaiPhong());
            model.addAttribute("trangThaiList", TrangThaiPhong.values());
            model.addAttribute("isEdit", false);
            return "room/admin/room-form";
        }

        try {
            phongService.createPhong(request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Thêm mới phòng [" + request.getSoPhong() + "] thành công.");
            return "redirect:/admin/rooms";
        } catch (Exception ex) {
            log.warn("Lỗi tạo phòng: {}", ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            model.addAttribute("loaiPhongList", loaiPhongService.getActiveLoaiPhong());
            model.addAttribute("trangThaiList", TrangThaiPhong.values());
            model.addAttribute("isEdit", false);
            return "room/admin/room-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Phong phong = phongService.getPhongById(id);
            PhongRequest request = new PhongRequest();
            request.setId(phong.getId());
            request.setMaPhong(phong.getMaPhong());
            request.setSoPhong(phong.getSoPhong());
            request.setMaLoaiPhong(phong.getLoaiPhong().getMaLoaiPhong());
            request.setTrangThai(phong.getTrangThai());

            model.addAttribute("roomRequest", request);
            model.addAttribute("loaiPhongList", loaiPhongService.getAllLoaiPhong());
            model.addAttribute("trangThaiList", TrangThaiPhong.values());
            model.addAttribute("isEdit", true);
            return "room/admin/room-form";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            return "redirect:/admin/rooms";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateRoom(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("roomRequest") PhongRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("loaiPhongList", loaiPhongService.getAllLoaiPhong());
            model.addAttribute("trangThaiList", TrangThaiPhong.values());
            model.addAttribute("isEdit", true);
            return "room/admin/room-form";
        }

        try {
            phongService.updatePhong(id, request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Cập nhật phòng [" + request.getSoPhong() + "] thành công.");
            return "redirect:/admin/rooms";
        } catch (Exception ex) {
            log.warn("Lỗi sửa phòng ID {}: {}", id, ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            model.addAttribute("loaiPhongList", loaiPhongService.getAllLoaiPhong());
            model.addAttribute("trangThaiList", TrangThaiPhong.values());
            model.addAttribute("isEdit", true);
            return "room/admin/room-form";
        }
    }

    @PostMapping("/toggle-maintenance/{id}")
    public String toggleMaintenance(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : "SYSTEM";

        try {
            phongService.toggleMaintenance(id, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Chuyển đổi trạng thái bảo trì cho phòng thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi đổi bảo trì phòng ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return "redirect:/admin/rooms";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoom(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            phongService.deletePhong(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa phòng vật lý thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi xóa phòng ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return "redirect:/admin/rooms";
    }
}

package com.example.skygatehub.controller.room;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.room.KhuLuuTruDto;
import com.example.skygatehub.dto.room.LoaiPhongRequest;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.service.room.LoaiPhongService;
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
@RequestMapping("/admin/room-types")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_LUU_TRU", "VT006"})
@RequiredArgsConstructor
@Slf4j
public class AdminRoomTypeController {

    private final LoaiPhongService loaiPhongService;

    @GetMapping("")
    public String listRoomTypes(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "maKhu", required = false) Integer maKhu,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "maLoaiPhong,asc") String sortParam,
            Model model) {

        String[] sortParts = sortParam.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<LoaiPhong> roomTypesPage = loaiPhongService.searchLoaiPhong(maKhu, trangThai, keyword, pageable);

        List<KhuLuuTru> khuList = loaiPhongService.getAllKhuLuuTru();

        model.addAttribute("roomTypesPage", roomTypesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomTypesPage.getTotalPages());
        model.addAttribute("totalElements", roomTypesPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedMaKhu", maKhu);
        model.addAttribute("selectedTrangThai", trangThai);
        model.addAttribute("sortParam", sortParam);

        model.addAttribute("khuList", khuList);
        model.addAttribute("newKhuDto", new KhuLuuTruDto());

        return "room/admin/type-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        LoaiPhongRequest request = new LoaiPhongRequest();
        request.setTrangThai(true);

        model.addAttribute("roomTypeRequest", request);
        model.addAttribute("khuList", loaiPhongService.getAllKhuLuuTru());
        model.addAttribute("isEdit", false);
        return "room/admin/type-form";
    }

    @PostMapping("/create")
    public String createRoomType(
            @Valid @ModelAttribute("roomTypeRequest") LoaiPhongRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("khuList", loaiPhongService.getAllKhuLuuTru());
            model.addAttribute("isEdit", false);
            return "room/admin/type-form";
        }

        try {
            loaiPhongService.createLoaiPhong(request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Thêm loại phòng mới [" + request.getTenLoaiPhong() + "] thành công.");
            return "redirect:/admin/room-types";
        } catch (Exception ex) {
            log.warn("Lỗi thêm loại phòng: {}", ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            model.addAttribute("khuList", loaiPhongService.getAllKhuLuuTru());
            model.addAttribute("isEdit", false);
            return "room/admin/type-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            LoaiPhong lp = loaiPhongService.getLoaiPhongById(id);

            LoaiPhongRequest request = new LoaiPhongRequest();
            request.setId(lp.getId());
            request.setMaLoaiPhong(lp.getMaLoaiPhong());
            request.setMaKhu(lp.getKhuLuuTru().getMaKhu());
            request.setTenLoaiPhong(lp.getTenLoaiPhong());
            request.setDienTich(lp.getDienTich());
            request.setLoaiGiuong(lp.getLoaiGiuong());
            request.setSucChuaNguoiLon(lp.getSucChuaNguoiLon());
            request.setSucChuaTreEm(lp.getSucChuaTreEm());
            request.setGiaCoBan(lp.getGiaCoBan());
            request.setHinhAnh(lp.getHinhAnh());
            request.setMoTa(lp.getMoTa());
            request.setTrangThai(lp.getTrangThai());

            model.addAttribute("roomTypeRequest", request);
            model.addAttribute("khuList", loaiPhongService.getAllKhuLuuTru());
            model.addAttribute("isEdit", true);
            return "room/admin/type-form";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            return "redirect:/admin/room-types";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateRoomType(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("roomTypeRequest") LoaiPhongRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("khuList", loaiPhongService.getAllKhuLuuTru());
            model.addAttribute("isEdit", true);
            return "room/admin/type-form";
        }

        try {
            loaiPhongService.updateLoaiPhong(id, request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Cập nhật loại phòng [" + request.getTenLoaiPhong() + "] thành công.");
            return "redirect:/admin/room-types";
        } catch (Exception ex) {
            log.warn("Lỗi sửa loại phòng ID {}: {}", id, ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            model.addAttribute("khuList", loaiPhongService.getAllKhuLuuTru());
            model.addAttribute("isEdit", true);
            return "room/admin/type-form";
        }
    }

    @PostMapping("/toggle/{id}")
    public String toggleStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongService.toggleLoaiPhongStatus(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Thay đổi trạng thái kinh doanh thành công.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return "redirect:/admin/room-types";
    }

    @PostMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongService.deleteLoaiPhong(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa loại phòng thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi xóa loại phòng ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return "redirect:/admin/room-types";
    }

    // ==================== KHU LƯU TRÚ ACTIONS ====================

    @PostMapping("/khu/create")
    public String createKhu(
            @Valid @ModelAttribute("newKhuDto") KhuLuuTruDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/room-types";
        }

        try {
            loaiPhongService.createKhuLuuTru(dto);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Thêm khu lưu trú mới [" + dto.getTenKhu() + "] thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi tạo khu: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return "redirect:/admin/room-types";
    }

    @PostMapping("/khu/edit/{maKhu}")
    public String updateKhu(
            @PathVariable("maKhu") Integer maKhu,
            @Valid @ModelAttribute KhuLuuTruDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/admin/room-types";
        }

        try {
            loaiPhongService.updateKhuLuuTru(maKhu, dto);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Cập nhật khu lưu trú [" + dto.getTenKhu() + "] thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi sửa khu ID {}: {}", maKhu, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return "redirect:/admin/room-types";
    }

    @PostMapping("/khu/delete/{maKhu}")
    public String deleteKhu(@PathVariable("maKhu") Integer maKhu, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongService.deleteKhuLuuTru(maKhu);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa khu lưu trú thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi xóa khu ID {}: {}", maKhu, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return "redirect:/admin/room-types";
    }
}

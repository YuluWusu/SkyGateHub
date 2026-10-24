package com.example.skygatehub.controller.dining;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.dining.CaAnRequest;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.service.dining.CaAnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/dining/shifts")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_NHA_HANG", "VT005"})
@RequiredArgsConstructor
@Slf4j
public class AdminMealShiftController {

    private static final String LIST_VIEW = "dining/admin/shift-list";
    private static final String FORM_VIEW = "dining/admin/shift-form";
    private static final String REDIRECT_LIST = "redirect:/admin/dining/shifts";

    private final CaAnService caAnService;

    @GetMapping("")
    public String listShifts(Model model) {
        model.addAttribute("caAnList", caAnService.getAll());
        model.addAttribute("dishCountByCa", caAnService.countActiveDishesByCa());
        return LIST_VIEW;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("shiftRequest", new CaAnRequest());
        model.addAttribute("isEdit", false);
        return FORM_VIEW;
    }

    @PostMapping("/create")
    public String createShift(
            @Valid @ModelAttribute("shiftRequest") CaAnRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return FORM_VIEW;
        }

        try {
            caAnService.create(request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Thêm mới ca ăn [" + request.getTenCaAn() + "] thành công.");
            return REDIRECT_LIST;
        } catch (Exception ex) {
            log.warn("Lỗi tạo ca ăn: {}", ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            model.addAttribute("isEdit", false);
            return FORM_VIEW;
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CaAn ca = caAnService.getById(id);
            CaAnRequest request = new CaAnRequest();
            request.setId(ca.getId());
            request.setMaCaAn(ca.getMaCaAn());
            request.setTenCaAn(ca.getTenCaAn());
            request.setGioBatDau(ca.getGioBatDau());
            request.setGioKetThuc(ca.getGioKetThuc());
            request.setTrangThai(ca.getTrangThai());

            model.addAttribute("shiftRequest", request);
            model.addAttribute("isEdit", true);
            return FORM_VIEW;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            return REDIRECT_LIST;
        }
    }

    @PostMapping("/edit/{id}")
    public String updateShift(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("shiftRequest") CaAnRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return FORM_VIEW;
        }

        try {
            caAnService.update(id, request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Cập nhật ca ăn [" + request.getTenCaAn() + "] thành công.");
            return REDIRECT_LIST;
        } catch (Exception ex) {
            log.warn("Lỗi sửa ca ăn ID {}: {}", id, ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            model.addAttribute("isEdit", true);
            return FORM_VIEW;
        }
    }

    @PostMapping("/toggle/{id}")
    public String toggleShift(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            caAnService.toggleTrangThai(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đã đổi trạng thái ca ăn.");
        } catch (Exception ex) {
            log.warn("Lỗi đổi trạng thái ca ăn ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return REDIRECT_LIST;
    }

    @PostMapping("/delete/{id}")
    public String deleteShift(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            caAnService.delete(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa ca ăn thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi xóa ca ăn ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return REDIRECT_LIST;
    }
}

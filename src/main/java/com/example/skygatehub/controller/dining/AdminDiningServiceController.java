package com.example.skygatehub.controller.dining;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.dining.DichVuAnRequest;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.enums.dining.LoaiDichVuAn;
import com.example.skygatehub.service.dining.DichVuAnService;
import com.example.skygatehub.service.dining.TableAssignmentService;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dining/services")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_NHA_HANG", "VT005"})
@RequiredArgsConstructor
@Slf4j
public class AdminDiningServiceController {

    private static final String VIEW_LIST = "dining/admin/service-list";
    private static final String VIEW_FORM = "dining/admin/service-form";
    private static final String REDIRECT_LIST = "redirect:/admin/dining/services";
    private static final List<String> SORT_FIELDS = List.of("maDichVu", "tenDichVu", "loaiDichVu", "giaDichVu");

    private final DichVuAnService dichVuAnService;
    private final TableAssignmentService tableAssignmentService;

    @GetMapping("")
    public String listServices(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "loai", required = false) String loai,
            @RequestParam(value = "maCaAn", required = false) String maCaAn,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "maDichVu,asc") String sortParam,
            Model model) {

        page = Math.max(0, page);
        size = Math.min(100, Math.max(1, size));

        String[] sortParts = sortParam.split(",");
        String sortField = SORT_FIELDS.contains(sortParts[0]) ? sortParts[0] : "maDichVu";
        Sort.Direction direction = (sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1].trim()))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String normalizedSort = sortField + "," + (direction == Sort.Direction.DESC ? "desc" : "asc");

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<DichVuAnUong> servicesPage = dichVuAnService.search(keyword, loai, maCaAn, trangThai, pageable);

        List<String> maList = servicesPage.getContent().stream().map(DichVuAnUong::getMaDichVu).toList();

        model.addAttribute("servicesPage", servicesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", servicesPage.getTotalPages());
        model.addAttribute("totalElements", servicesPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedLoai", loai);
        model.addAttribute("selectedMaCaAn", maCaAn);
        model.addAttribute("selectedTrangThai", trangThai);
        model.addAttribute("size", size);
        model.addAttribute("sortParam", normalizedSort);

        model.addAttribute("caTheoDichVu", dichVuAnService.getTenCaAnTheoDichVu(maList));
        model.addAttribute("loaiList", LoaiDichVuAn.values());
        model.addAttribute("loaiNhan", Arrays.stream(LoaiDichVuAn.values())
                .collect(Collectors.toMap(LoaiDichVuAn::name, LoaiDichVuAn::getNhan)));
        model.addAttribute("caAnList", tableAssignmentService.getAllCaAn());

        return VIEW_LIST;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("serviceRequest", new DichVuAnRequest());
        addFormLists(model, false);
        return VIEW_FORM;
    }

    @PostMapping("/create")
    public String createService(
            @Valid @ModelAttribute("serviceRequest") DichVuAnRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormLists(model, false);
            return VIEW_FORM;
        }

        try {
            dichVuAnService.create(request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Thêm mới dịch vụ [" + request.getTenDichVu() + "] thành công.");
            return REDIRECT_LIST;
        } catch (Exception ex) {
            log.warn("Lỗi tạo dịch vụ ăn uống: {}", ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            addFormLists(model, false);
            return VIEW_FORM;
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            DichVuAnUong dv = dichVuAnService.getById(id);
            DichVuAnRequest request = new DichVuAnRequest();
            request.setId(dv.getId());
            request.setMaDichVu(dv.getMaDichVu());
            request.setTenDichVu(dv.getTenDichVu());
            request.setLoaiDichVu(dv.getLoaiDichVu());
            request.setMoTa(dv.getMoTa());
            request.setGiaDichVu(dv.getGiaDichVu());
            request.setTrangThai(dv.getTrangThai());
            request.setMaCaAnList(dichVuAnService.getMaCaAnDangPhucVu(dv.getMaDichVu()));

            model.addAttribute("serviceRequest", request);
            addFormLists(model, true);
            return VIEW_FORM;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            return REDIRECT_LIST;
        }
    }

    @PostMapping("/edit/{id}")
    public String updateService(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("serviceRequest") DichVuAnRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormLists(model, true);
            return VIEW_FORM;
        }

        try {
            dichVuAnService.update(id, request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Cập nhật dịch vụ [" + request.getTenDichVu() + "] thành công.");
            return REDIRECT_LIST;
        } catch (Exception ex) {
            log.warn("Lỗi sửa dịch vụ ăn uống ID {}: {}", id, ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            addFormLists(model, true);
            return VIEW_FORM;
        }
    }

    @PostMapping("/toggle/{id}")
    public String toggleService(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            dichVuAnService.toggleTrangThai(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đã đổi trạng thái kinh doanh của dịch vụ.");
        } catch (Exception ex) {
            log.warn("Lỗi đổi trạng thái dịch vụ ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return REDIRECT_LIST;
    }

    @PostMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            dichVuAnService.delete(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa dịch vụ ăn uống thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi xóa dịch vụ ăn uống ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return REDIRECT_LIST;
    }

    private void addFormLists(Model model, boolean isEdit) {
        model.addAttribute("loaiList", LoaiDichVuAn.values());
        model.addAttribute("caAnList", tableAssignmentService.getAllCaAn());
        model.addAttribute("isEdit", isEdit);
    }
}

package com.example.skygatehub.controller.dining;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.dining.BanAnRequest;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.service.dining.BanAnService;
import com.example.skygatehub.service.dining.TableAssignmentService;
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
@RequestMapping("/admin/dining/tables")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_NHA_HANG", "VT005"})
@RequiredArgsConstructor
@Slf4j
public class AdminTableController {

    private static final String VIEW_LIST = "dining/admin/table-list";
    private static final String VIEW_FORM = "dining/admin/table-form";
    private static final String REDIRECT_LIST = "redirect:/admin/dining/tables";
    private static final List<String> SORT_FIELDS = List.of("soBan", "maBanAn", "khuVuc", "sucChua", "trangThai");

    private final BanAnService banAnService;
    private final TableAssignmentService tableAssignmentService;

    @GetMapping("")
    public String listTables(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "khuVuc", required = false) String khuVuc,
            @RequestParam(value = "trangThai", required = false) TrangThaiBanAn trangThai,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "soBan,asc") String sortParam,
            Model model) {

        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));

        String[] sortParts = sortParam.split(",");
        String sortField = SORT_FIELDS.contains(sortParts[0]) ? sortParts[0] : "soBan";
        boolean desc = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1].trim());
        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
        String normalizedSort = sortField + "," + (desc ? "desc" : "asc");

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, sortField));
        Page<BanAn> tablesPage = banAnService.search(keyword, khuVuc, trangThai, pageable);

        model.addAttribute("tablesPage", tablesPage);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", tablesPage.getTotalPages());
        model.addAttribute("totalElements", tablesPage.getTotalElements());
        model.addAttribute("pageSize", safeSize);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedKhuVuc", khuVuc);
        model.addAttribute("selectedTrangThai", trangThai);
        model.addAttribute("sortParam", normalizedSort);

        model.addAttribute("khuVucList", tableAssignmentService.getAllKhuVuc());
        model.addAttribute("trangThaiList", TrangThaiBanAn.values());

        return VIEW_LIST;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        BanAnRequest request = new BanAnRequest();
        request.setTrangThai(TrangThaiBanAn.SAN_SANG);
        prepareForm(model, request, false, false);
        return VIEW_FORM;
    }

    @PostMapping("/create")
    public String createTable(
            @Valid @ModelAttribute("tableRequest") BanAnRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            prepareForm(model, request, false, false);
            return VIEW_FORM;
        }

        try {
            banAnService.create(request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Thêm mới bàn ăn [" + request.getSoBan().trim() + "] thành công.");
            return REDIRECT_LIST;
        } catch (Exception ex) {
            log.warn("Lỗi tạo bàn ăn: {}", ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            prepareForm(model, request, false, false);
            return VIEW_FORM;
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BanAn ban = banAnService.getById(id);
            BanAnRequest request = new BanAnRequest();
            request.setId(ban.getId());
            request.setMaBanAn(ban.getMaBanAn());
            request.setSoBan(ban.getSoBan());
            request.setKhuVuc(ban.getKhuVuc());
            request.setSucChua(ban.getSucChua());
            request.setTrangThai(ban.getTrangThai());

            prepareForm(model, request, true, ban.getTrangThai() == TrangThaiBanAn.DANG_SU_DUNG);
            return VIEW_FORM;
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            return REDIRECT_LIST;
        }
    }

    @PostMapping("/edit/{id}")
    public String updateTable(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("tableRequest") BanAnRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            prepareForm(model, request, true, isInUse(id));
            return VIEW_FORM;
        }

        try {
            banAnService.update(id, request);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Cập nhật bàn ăn [" + request.getSoBan().trim() + "] thành công.");
            return REDIRECT_LIST;
        } catch (Exception ex) {
            log.warn("Lỗi sửa bàn ăn ID {}: {}", id, ex.getMessage());
            model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            prepareForm(model, request, true, isInUse(id));
            return VIEW_FORM;
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
            banAnService.toggleMaintenance(id, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Chuyển đổi trạng thái bảo trì cho bàn ăn thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi đổi bảo trì bàn ăn ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return REDIRECT_LIST;
    }

    @PostMapping("/release/{id}")
    public String releaseTable(
            @PathVariable("id") Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (user != null) ? user.getMaNguoiDung() : "SYSTEM";

        try {
            banAnService.releaseTable(id, staffCode);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đã giải phóng bàn ăn về trạng thái sẵn sàng.");
        } catch (Exception ex) {
            log.warn("Lỗi giải phóng bàn ăn ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        return REDIRECT_LIST;
    }

    @PostMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            banAnService.delete(id);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa bàn ăn thành công.");
        } catch (Exception ex) {
            log.warn("Lỗi xóa bàn ăn ID {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return REDIRECT_LIST;
    }

    private void prepareForm(Model model, BanAnRequest request, boolean isEdit, boolean inUse) {
        model.addAttribute("tableRequest", request);
        model.addAttribute("khuVucList", tableAssignmentService.getAllKhuVuc());
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("inUse", inUse);
    }

    // Dùng khi trả lại form lỗi: bàn đang phục vụ thì khóa ô trạng thái
    private boolean isInUse(Long id) {
        try {
            return banAnService.getById(id).getTrangThai() == TrangThaiBanAn.DANG_SU_DUNG;
        } catch (Exception ex) {
            return false;
        }
    }
}

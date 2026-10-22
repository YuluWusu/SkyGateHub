package com.example.skygatehub.controller.ticket;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.ticket.ComboItemRequest;
import com.example.skygatehub.dto.ticket.SanPhamVeRequest;
import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import com.example.skygatehub.entity.ticket.KhoVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.ThanhPhanDichVu;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketBookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/tickets")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE"})
public class AdminTicketController {

    private final SanPhamVeService sanPhamVeService;
    private final TicketBookingService ticketBookingService;

    public AdminTicketController(SanPhamVeService sanPhamVeService, TicketBookingService ticketBookingService) {
        this.sanPhamVeService = sanPhamVeService;
        this.ticketBookingService = ticketBookingService;
    }

    @GetMapping("")
    public String danhSachLoaiVe(Model moHinh,
                                 @RequestParam(value = "keyword", required = false) String tuKhoa,
                                 @RequestParam(value = "page", defaultValue = "0") int trang,
                                 @RequestParam(value = "size", defaultValue = "10") int kichThuoc) {
        Pageable phanTrang = PageRequest.of(trang, kichThuoc, Sort.by(Sort.Direction.DESC, "id"));
        Page<LoaiVe> trangLoaiVe;

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            trangLoaiVe = sanPhamVeService.timKiemLoaiVe(tuKhoa.trim(), phanTrang);
            moHinh.addAttribute("keyword", tuKhoa.trim());
        } else {
            trangLoaiVe = sanPhamVeService.layTatCaLoaiVe(phanTrang);
        }

        moHinh.addAttribute("ticketsPage", trangLoaiVe);
        moHinh.addAttribute("currentPage", trang);
        moHinh.addAttribute("totalPages", trangLoaiVe.getTotalPages());
        return "ticket/admin/list";
    }

    @GetMapping("/create")
    public String hienThiFormTao(Model moHinh) {
        SanPhamVeRequest request = new SanPhamVeRequest();
        request.setMaLoaiVe(sanPhamVeService.generateNextMaLoaiVe());
        moHinh.addAttribute("ticketRequest", request);
        moHinh.addAttribute("loaiVeTypes", LoaiVeType.values());
        moHinh.addAttribute("isEdit", false);
        return "ticket/admin/form";
    }

    @PostMapping("/create")
    public String taoLoaiVe(@ModelAttribute("ticketRequest") SanPhamVeRequest yeuCau,
                            RedirectAttributes dieuHuong) {
        try {
            sanPhamVeService.taoLoaiVe(yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Tạo loại vé mới thành công.");
            return "redirect:/admin/tickets";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/admin/tickets/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String hienThiFormSua(@PathVariable("id") Long id, Model moHinh, RedirectAttributes dieuHuong) {
        LoaiVe loaiVe = sanPhamVeService.layVeTheoId(id);
        if (loaiVe == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy loại vé.");
            return "redirect:/admin/tickets";
        }

        SanPhamVeRequest yeuCau = new SanPhamVeRequest();
        yeuCau.setMaLoaiVe(loaiVe.getMaLoaiVe());
        yeuCau.setTenLoaiVe(loaiVe.getTenLoaiVe());
        yeuCau.setLoaiVe(loaiVe.getLoaiVe());
        yeuCau.setMoTa(loaiVe.getMoTa());
        yeuCau.setGiaVe(loaiVe.getGiaVe());
        yeuCau.setTrangThai(loaiVe.getTrangThai());

        moHinh.addAttribute("ticketRequest", yeuCau);
        moHinh.addAttribute("loaiVeTypes", LoaiVeType.values());
        moHinh.addAttribute("ticketId", id);
        moHinh.addAttribute("isEdit", true);
        return "ticket/admin/form";
    }

    @PostMapping("/edit/{id}")
    public String capNhatLoaiVe(@PathVariable("id") Long id,
                                @ModelAttribute("ticketRequest") SanPhamVeRequest yeuCau,
                                RedirectAttributes dieuHuong) {
        try {
            sanPhamVeService.capNhatLoaiVe(id, yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Cập nhật loại vé thành công.");
            return "redirect:/admin/tickets";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/admin/tickets/edit/" + id;
        }
    }

    @GetMapping("/toggle/{id}")
    public String chuyenTrangThaiVe(@PathVariable("id") Long id, RedirectAttributes dieuHuong) {
        sanPhamVeService.chuyenTrangThaiVe(id);
        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đổi trạng thái vé thành công.");
        return "redirect:/admin/tickets";
    }

    @GetMapping("/delete/{id}")
    public String xoaLoaiVe(@PathVariable("id") Long id, RedirectAttributes dieuHuong) {
        try {
            sanPhamVeService.xoaLoaiVe(id);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa loại vé thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không thể xóa vé do có dữ liệu liên quan.");
        }
        return "redirect:/admin/tickets";
    }

    @GetMapping("/detail/{id}")
    public String chiTietLoaiVe(@PathVariable("id") Long id, Model moHinh, RedirectAttributes dieuHuong) {
        LoaiVe loaiVe = sanPhamVeService.layVeTheoId(id);
        if (loaiVe == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy loại vé.");
            return "redirect:/admin/tickets";
        }
        List<ComboThanhPhan> danhSachThanhPhanCombo = sanPhamVeService.layThanhPhanCombo(loaiVe.getMaLoaiVe());
        List<KhoVe> danhSachTonKho = ticketBookingService.layTonKhoTheoVe(loaiVe.getMaLoaiVe());
        moHinh.addAttribute("ticket", loaiVe);
        moHinh.addAttribute("comboItems", danhSachThanhPhanCombo);
        moHinh.addAttribute("inventoryList", danhSachTonKho);
        return "ticket/admin/detail";
    }

    @GetMapping("/{id}/combo")
    public String cauHinhCombo(@PathVariable("id") Long id, Model moHinh, RedirectAttributes dieuHuong) {
        LoaiVe loaiVe = sanPhamVeService.layVeTheoId(id);
        if (loaiVe == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy loại vé.");
            return "redirect:/admin/tickets";
        }

        if (loaiVe.getLoaiVe() != LoaiVeType.TRON_GOI) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Chỉ có loại vé combo trọn gói mới có thể cấu hình thành phần.");
            return "redirect:/admin/tickets/detail/" + id;
        }

        List<ComboThanhPhan> danhSachThanhPhanCombo = sanPhamVeService.layThanhPhanCombo(loaiVe.getMaLoaiVe());
        List<ThanhPhanDichVu> danhSachDichVu = sanPhamVeService.layTatCaDichVuThanhPhan();

        moHinh.addAttribute("ticket", loaiVe);
        moHinh.addAttribute("comboItems", danhSachThanhPhanCombo);
        moHinh.addAttribute("availableServices", danhSachDichVu);
        moHinh.addAttribute("availableComponents", danhSachDichVu);
        moHinh.addAttribute("comboItemRequest", new ComboItemRequest());
        return "ticket/admin/combo-form";
    }

    @PostMapping(value = {"/{id}/combo", "/{id}/combo/add"})
    public String themThanhPhanCombo(@PathVariable("id") Long id,
                                     @ModelAttribute("comboItemRequest") ComboItemRequest yeuCau,
                                     RedirectAttributes dieuHuong) {
        LoaiVe loaiVe = sanPhamVeService.layVeTheoId(id);
        if (loaiVe == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy loại vé.");
            return "redirect:/admin/tickets";
        }

        try {
            sanPhamVeService.themThanhPhanCombo(loaiVe.getMaLoaiVe(), yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Thêm thành phần vào combo thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
        }
        return "redirect:/admin/tickets/" + id + "/combo";
    }

    @GetMapping("/{ticketId}/combo/delete/{comboItemId}")
    public String xoaThanhPhanCombo(@PathVariable("ticketId") Long ticketId,
                                    @PathVariable("comboItemId") Long comboItemId,
                                    RedirectAttributes dieuHuong) {
        try {
            sanPhamVeService.xoaThanhPhanCombo(comboItemId);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa thành phần khỏi combo thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không thể xóa thành phần combo.");
        }
        return "redirect:/admin/tickets/" + ticketId + "/combo";
    }
}

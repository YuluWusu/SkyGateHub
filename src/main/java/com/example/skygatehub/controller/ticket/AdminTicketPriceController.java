package com.example.skygatehub.controller.ticket;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.ticket.BangGiaVeRequest;
import com.example.skygatehub.entity.ticket.BangGiaVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.NgayDacBiet;
import com.example.skygatehub.service.ticket.BangGiaVeService;
import com.example.skygatehub.service.ticket.SanPhamVeService;
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
@RequestMapping("/admin/ticket-prices")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE"})
public class AdminTicketPriceController {

    private final BangGiaVeService bangGiaVeService;
    private final SanPhamVeService sanPhamVeService;

    public AdminTicketPriceController(BangGiaVeService bangGiaVeService,
                                      SanPhamVeService sanPhamVeService) {
        this.bangGiaVeService = bangGiaVeService;
        this.sanPhamVeService = sanPhamVeService;
    }

    @GetMapping("")
    public String danhSachBangGia(Model moHinh,
                                  @RequestParam(value = "keyword", required = false) String tuKhoa,
                                  @RequestParam(value = "page", defaultValue = "0") int trang,
                                  @RequestParam(value = "size", defaultValue = "10") int kichThuoc) {
        Pageable phanTrang = PageRequest.of(trang, kichThuoc, Sort.by(Sort.Direction.DESC, "uuTien"));
        Page<BangGiaVe> trangBangGia;

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            trangBangGia = bangGiaVeService.timKiemBangGia(tuKhoa.trim(), phanTrang);
            moHinh.addAttribute("keyword", tuKhoa.trim());
        } else {
            trangBangGia = bangGiaVeService.layTatCaBangGia(phanTrang);
        }

        List<NgayDacBiet> danhSachNgayDacBiet = bangGiaVeService.layTatCaNgayDacBiet();
        List<LoaiVe> danhSachLoaiVe = sanPhamVeService.layDanhSachVeDangBan();

        moHinh.addAttribute("pricesPage", trangBangGia);
        moHinh.addAttribute("specialDays", danhSachNgayDacBiet);
        moHinh.addAttribute("tickets", danhSachLoaiVe);
        moHinh.addAttribute("currentPage", trang);
        moHinh.addAttribute("totalPages", trangBangGia.getTotalPages());
        moHinh.addAttribute("newSpecialDay", new NgayDacBiet());
        return "ticket/admin/price-list";
    }

    @GetMapping("/create")
    public String hienThiFormTao(Model moHinh) {
        List<LoaiVe> danhSachLoaiVe = sanPhamVeService.layDanhSachVeDangBan();
        BangGiaVeRequest request = new BangGiaVeRequest();
        request.setMaBangGia(bangGiaVeService.generateNextMaBangGia());
        moHinh.addAttribute("priceRequest", request);
        moHinh.addAttribute("tickets", danhSachLoaiVe);
        moHinh.addAttribute("isEdit", false);
        return "ticket/admin/price-form";
    }

    @PostMapping("/create")
    public String taoBangGia(@ModelAttribute("priceRequest") BangGiaVeRequest yeuCau,
                             RedirectAttributes dieuHuong) {
        try {
            bangGiaVeService.taoBangGia(yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Tạo cấu hình bảng giá vé thành công.");
            return "redirect:/admin/ticket-prices";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/admin/ticket-prices/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String hienThiFormSua(@PathVariable("id") Long id, Model moHinh, RedirectAttributes dieuHuong) {
        BangGiaVe bangGia = bangGiaVeService.layBangGiaTheoId(id);
        if (bangGia == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy bảng giá.");
            return "redirect:/admin/ticket-prices";
        }

        BangGiaVeRequest yeuCau = new BangGiaVeRequest();
        yeuCau.setMaBangGia(bangGia.getMaBangGia());
        yeuCau.setMaLoaiVe(bangGia.getLoaiVe().getMaLoaiVe());
        yeuCau.setTenBangGia(bangGia.getTenBangGia());
        yeuCau.setDieuKienGia(bangGia.getDieuKienGia());
        yeuCau.setHieuLucTu(bangGia.getHieuLucTu());
        yeuCau.setHieuLucDen(bangGia.getHieuLucDen());
        yeuCau.setDonGia(bangGia.getDonGia());
        yeuCau.setUuTien(bangGia.getUuTien());
        yeuCau.setTrangThai(bangGia.getTrangThai());
        yeuCau.setGhiChu(bangGia.getGhiChu());

        List<LoaiVe> danhSachLoaiVe = sanPhamVeService.layDanhSachVeDangBan();
        moHinh.addAttribute("priceRequest", yeuCau);
        moHinh.addAttribute("tickets", danhSachLoaiVe);
        moHinh.addAttribute("priceId", id);
        moHinh.addAttribute("isEdit", true);
        return "ticket/admin/price-form";
    }

    @PostMapping("/edit/{id}")
    public String capNhatBangGia(@PathVariable("id") Long id,
                                 @ModelAttribute("priceRequest") BangGiaVeRequest yeuCau,
                                 RedirectAttributes dieuHuong) {
        try {
            bangGiaVeService.capNhatBangGia(id, yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Cập nhật bảng giá vé thành công.");
            return "redirect:/admin/ticket-prices";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/admin/ticket-prices/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String xoaBangGia(@PathVariable("id") Long id, RedirectAttributes dieuHuong) {
        try {
            bangGiaVeService.xoaBangGia(id);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa bảng giá vé thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không thể xóa bảng giá.");
        }
        return "redirect:/admin/ticket-prices";
    }

    @PostMapping("/special-day/create")
    public String taoNgayDacBiet(@ModelAttribute("newSpecialDay") NgayDacBiet ngayDacBiet,
                                 RedirectAttributes dieuHuong) {
        try {
            bangGiaVeService.taoNgayDacBiet(ngayDacBiet);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Thêm ngày lễ/sự kiện đặc biệt thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
        }
        return "redirect:/admin/ticket-prices";
    }

    @GetMapping("/special-day/delete/{id}")
    public String xoaNgayDacBiet(@PathVariable("id") Long id, RedirectAttributes dieuHuong) {
        try {
            bangGiaVeService.xoaNgayDacBiet(id);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa ngày đặc biệt thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không thể xóa ngày đặc biệt.");
        }
        return "redirect:/admin/ticket-prices";
    }
}

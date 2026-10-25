package com.example.skygatehub.controller.order;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE", "VT004", "NHAN_VIEN_LUU_TRU", "VT006"})
public class AdminOrderController {

    private final DonDatRepository donDatRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final ThanhToanRepository thanhToanRepository;

    @GetMapping
    public String listOrders(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by(Sort.Direction.DESC, "thoiGianDat"));
        Page<DonDat> ordersPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            ordersPage = donDatRepository.findByMaDonDatContainingIgnoreCaseOrHoTenKhachContainingIgnoreCaseOrSdtKhachContainingIgnoreCaseOrderByThoiGianDatDesc(
                    kw, kw, kw, pageable);
        } else if (!"ALL".equalsIgnoreCase(status)) {
            try {
                TrangThaiDon enumStatus = TrangThaiDon.valueOf(status.toUpperCase());
                ordersPage = donDatRepository.findByTrangThaiDonOrderByThoiGianDatDesc(enumStatus, pageable);
            } catch (IllegalArgumentException e) {
                ordersPage = donDatRepository.findAllByOrderByThoiGianDatDesc(pageable);
            }
        } else {
            ordersPage = donDatRepository.findAllByOrderByThoiGianDatDesc(pageable);
        }

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
        model.addAttribute("totalOrders", donDatRepository.count());
        model.addAttribute("pendingOrders", donDatRepository.countByTrangThaiDon(TrangThaiDon.CHO_XAC_NHAN));
        model.addAttribute("confirmedOrders", donDatRepository.countByTrangThaiDon(TrangThaiDon.DA_XAC_NHAN));

        return "order/admin/list";
    }

    @GetMapping("/{maDonDat}")
    public String orderDetail(@PathVariable String maDonDat, Model model) {
        DonDat donDat = donDatRepository.findByMaDonDat(maDonDat)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt: " + maDonDat));

        List<ChiTietDon> chiTietList = chiTietDonRepository.findByDonDat_MaDonDat(maDonDat);
        List<ThanhToan> thanhToanList = thanhToanRepository.findByDonDat_MaDonDatOrderByThoiGianTaoAsc(maDonDat);

        model.addAttribute("order", donDat);
        model.addAttribute("chiTietList", chiTietList);
        model.addAttribute("thanhToanList", thanhToanList);

        return "order/admin/detail";
    }

    @PostMapping("/{maDonDat}/status")
    public String updateOrderStatus(@PathVariable String maDonDat,
                                    @RequestParam("trangThaiDon") String trangThaiDon,
                                    RedirectAttributes redirectAttributes) {
        DonDat donDat = donDatRepository.findByMaDonDat(maDonDat)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt: " + maDonDat));

        try {
            TrangThaiDon newStatus = TrangThaiDon.valueOf(trangThaiDon.toUpperCase());
            donDat.setTrangThaiDon(newStatus);
            donDatRepository.save(donDat);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đã cập nhật trạng thái đơn sang: " + newStatus.name());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, "Trạng thái đơn không hợp lệ: " + trangThaiDon);
        }

        return "redirect:/admin/orders/" + maDonDat;
    }
}

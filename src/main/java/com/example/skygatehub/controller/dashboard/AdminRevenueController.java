package com.example.skygatehub.controller.dashboard;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.VDonDatTongHop;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VDonDatTongHopRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE", "VT004"})
public class AdminRevenueController {

    private final DonDatRepository donDatRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final VDonDatTongHopRepository vDonDatTongHopRepository;

    @GetMapping("/admin/revenue")
    public String revenueReport(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        // 1. Chỉ số tổng hợp tài chính
        BigDecimal totalRevenue = donDatRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal totalPaid = thanhToanRepository.sumAllPaid();
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal totalPending = thanhToanRepository.sumAllPending();
        if (totalPending == null) totalPending = BigDecimal.ZERO;

        BigDecimal remainingToCollect = totalRevenue.subtract(totalPaid);
        if (remainingToCollect.compareTo(BigDecimal.ZERO) < 0) remainingToCollect = BigDecimal.ZERO;

        // 2. Cơ cấu doanh thu theo dịch vụ
        BigDecimal revenueVe = chiTietDonRepository.sumRevenueByLoaiDichVu(LoaiDichVuDon.VE);
        if (revenueVe == null) revenueVe = BigDecimal.ZERO;

        BigDecimal revenuePhong = chiTietDonRepository.sumRevenueByLoaiDichVu(LoaiDichVuDon.PHONG);
        if (revenuePhong == null) revenuePhong = BigDecimal.ZERO;

        BigDecimal revenueAnUong = chiTietDonRepository.sumRevenueByLoaiDichVu(LoaiDichVuDon.AN_UONG);
        if (revenueAnUong == null) revenueAnUong = BigDecimal.ZERO;

        BigDecimal sumCategories = revenueVe.add(revenuePhong).add(revenueAnUong);
        int percentVe = 0, percentPhong = 0, percentAnUong = 0;
        if (sumCategories.compareTo(BigDecimal.ZERO) > 0) {
            percentVe = revenueVe.multiply(BigDecimal.valueOf(100)).divide(sumCategories, 0, RoundingMode.HALF_UP).intValue();
            percentPhong = revenuePhong.multiply(BigDecimal.valueOf(100)).divide(sumCategories, 0, RoundingMode.HALF_UP).intValue();
            percentAnUong = 100 - percentVe - percentPhong;
            if (percentAnUong < 0) percentAnUong = 0;
        }

        // 3. Danh sách đơn hàng phân trang
        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by(Sort.Direction.DESC, "thoiGianDat"));
        Page<DonDat> ordersPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            ordersPage = donDatRepository.findByMaDonDatContainingIgnoreCaseOrHoTenKhachContainingIgnoreCaseOrSdtKhachContainingIgnoreCaseOrderByThoiGianDatDesc(
                    kw, kw, kw, pageable);
        } else if (!"ALL".equalsIgnoreCase(status)) {
            try {
                TrangThaiThanhToanDon enumStatus = TrangThaiThanhToanDon.valueOf(status.toUpperCase());
                ordersPage = donDatRepository.findByTrangThaiThanhToanOrderByThoiGianDatDesc(enumStatus, pageable);
            } catch (IllegalArgumentException e) {
                ordersPage = donDatRepository.findAllByOrderByThoiGianDatDesc(pageable);
            }
        } else {
            ordersPage = donDatRepository.findAllByOrderByThoiGianDatDesc(pageable);
        }

        // 4. View tổng hợp đối soát (nếu có trong DB)
        List<VDonDatTongHop> reconciliations = vDonDatTongHopRepository.findAll(PageRequest.of(0, 10)).getContent();

        // Đẩy vào Model
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalPending", totalPending);
        model.addAttribute("remainingToCollect", remainingToCollect);

        model.addAttribute("revenueVe", revenueVe);
        model.addAttribute("revenuePhong", revenuePhong);
        model.addAttribute("revenueAnUong", revenueAnUong);
        model.addAttribute("percentVe", percentVe);
        model.addAttribute("percentPhong", percentPhong);
        model.addAttribute("percentAnUong", percentAnUong);

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("reconciliations", reconciliations);
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");

        return "admin/revenue";
    }
}

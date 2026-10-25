package com.example.skygatehub.controller.dashboard;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.repository.ticket.LoaiVeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_LUU_TRU", "VT006", "NHAN_VIEN_VE", "VT004", "NHAN_VIEN_NHA_HANG", "VT005", "NHAN_VIEN_SOAT_VE"})
public class AdminDashboardController {

    private final DonDatRepository donDatRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final PhongRepository phongRepository;
    private final LoaiVeRepository loaiVeRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @GetMapping({"/admin", "/admin/dashboard"})
    public String dashboard(Model model) {
        // 1. KPI Doanh thu & Dòng tiền
        BigDecimal totalRevenue = donDatRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal paidRevenue = donDatRepository.sumPaidRevenue();
        if (paidRevenue == null) paidRevenue = BigDecimal.ZERO;

        BigDecimal pendingAmount = totalRevenue.subtract(paidRevenue);
        if (pendingAmount.compareTo(BigDecimal.ZERO) < 0) pendingAmount = BigDecimal.ZERO;

        long totalOrders = donDatRepository.count();
        long pendingOrders = donDatRepository.countByTrangThaiDon(TrangThaiDon.CHO_XAC_NHAN);
        long confirmedOrders = donDatRepository.countByTrangThaiDon(TrangThaiDon.DA_XAC_NHAN);
        long completedOrders = donDatRepository.countByTrangThaiDon(TrangThaiDon.DA_HOAN_THANH);
        long paidOrders = donDatRepository.countByTrangThaiThanhToan(TrangThaiThanhToanDon.DA_THANH_TOAN);

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

        // 3. Công suất Phòng nghỉ & Lưu trú
        long totalRooms = phongRepository.count();
        long occupiedRooms = phongRepository.countByTrangThai(TrangThaiPhong.DANG_SU_DUNG);
        long availableRooms = phongRepository.countByTrangThai(TrangThaiPhong.SAN_SANG);
        long maintenanceRooms = phongRepository.countByTrangThai(TrangThaiPhong.BAO_TRI);

        int roomOccupancyRate = 0;
        if (totalRooms > 0) {
            roomOccupancyRate = (int) Math.round(((double) occupiedRooms / totalRooms) * 100);
        }

        // 4. Vé & Khách hàng
        long totalTicketTypes = loaiVeRepository.count();
        long totalUsers = nguoiDungRepository.count();

        // 5. Danh sách đơn gần đây & giao dịch gần đây
        List<DonDat> recentOrders = donDatRepository.findTop8ByOrderByThoiGianDatDesc();
        List<ThanhToan> recentPayments = thanhToanRepository.findTop8ByOrderByThoiGianTaoDesc();

        // Đẩy vào Model
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("paidRevenue", paidRevenue);
        model.addAttribute("pendingAmount", pendingAmount);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("confirmedOrders", confirmedOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("paidOrders", paidOrders);

        model.addAttribute("revenueVe", revenueVe);
        model.addAttribute("revenuePhong", revenuePhong);
        model.addAttribute("revenueAnUong", revenueAnUong);
        model.addAttribute("percentVe", percentVe);
        model.addAttribute("percentPhong", percentPhong);
        model.addAttribute("percentAnUong", percentAnUong);

        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("occupiedRooms", occupiedRooms);
        model.addAttribute("availableRooms", availableRooms);
        model.addAttribute("maintenanceRooms", maintenanceRooms);
        model.addAttribute("roomOccupancyRate", roomOccupancyRate);

        model.addAttribute("totalTicketTypes", totalTicketTypes);
        model.addAttribute("totalUsers", totalUsers);

        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("recentPayments", recentPayments);

        return "dashboard/admin/index";
    }
}

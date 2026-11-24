package com.example.skygatehub.service.cancellation;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiHoanTien;
import com.example.skygatehub.common.enums.TrangThaiHuyDon;
import com.example.skygatehub.common.enums.TrangThaiQuyenLoi;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.common.exception.BusinessException;
import com.example.skygatehub.common.exception.ErrorCode;
import com.example.skygatehub.dto.cancellation.TicketCancelEligibility;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.cancellation.HoanTien;
import com.example.skygatehub.entity.cancellation.HuyDon;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.QuyenLoiDon;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.entity.payment.ChinhSachHuy;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.entity.ticket.KhoVe;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.cancellation.HoanTienRepository;
import com.example.skygatehub.repository.cancellation.HuyDonRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.QuyenLoiDonRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.payment.ChinhSachHuyRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import com.example.skygatehub.repository.ticket.KhoVeRepository;
import com.example.skygatehub.service.dining.TableAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Nghiệp vụ hủy vé 2 chiều (Khách hàng gửi yêu cầu -> Admin duyệt/từ chối).
 * Ràng buộc ngày:
 * - >= 15 ngày: Hủy miễn phí (hoàn 100% phần tiền đã thanh toán/cọc của vé)
 * - 12 đến 14 ngày: Cho phép hủy nhưng mất cọc (giữ lại phần cọc 30%)
 * - < 12 ngày: Không được phép hủy
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketCancellationServiceImpl implements TicketCancellationService {

    private final HuyDonRepository huyDonRepository;
    private final HoanTienRepository hoanTienRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final DonDatRepository donDatRepository;
    private final VeRepository veRepository;
    private final QuyenLoiDonRepository quyenLoiDonRepository;
    private final KhoVeRepository khoVeRepository;
    private final ChinhSachHuyRepository chinhSachHuyRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final TableAssignmentService tableAssignmentService;

    @Override
    @Transactional(readOnly = true)
    public TicketCancelEligibility evaluateEligibility(ChiTietDon item) {
        if (item == null || item.getLoaiDichVu() != LoaiDichVuDon.VE) {
            return TicketCancelEligibility.builder()
                    .allowed(false)
                    .reasonIfDenied("Chỉ hỗ trợ hủy cho mục Vé / Combo.")
                    .build();
        }

        List<HuyDon> requests = huyDonRepository.findByChiTietDon_MaChiTietOrderByThoiGianYeuCauDesc(item.getMaChiTiet());
        if (!requests.isEmpty()) {
            HuyDon latest = requests.get(0);
            if (latest.getTrangThaiXuLy() == TrangThaiHuyDon.CAN_XAC_NHAN
                    || latest.getTrangThaiXuLy() == TrangThaiHuyDon.CHO_XU_LY) {
                return TicketCancelEligibility.builder()
                        .allowed(false)
                        .existingRequestStatus(latest.getTrangThaiXuLy().name())
                        .existingRequestNote("Đang chờ Admin duyệt yêu cầu hủy (" + latest.getMaHuy() + ")")
                        .reasonIfDenied("Yêu cầu hủy đang chờ Admin phê duyệt.")
                        .build();
            }
            if (latest.getTrangThaiXuLy() == TrangThaiHuyDon.DA_DUYET
                    || latest.getTrangThaiXuLy() == TrangThaiHuyDon.DA_XU_LY) {
                return TicketCancelEligibility.builder()
                        .allowed(false)
                        .existingRequestStatus(latest.getTrangThaiXuLy().name())
                        .existingRequestNote("Đã được Admin duyệt hủy (" + latest.getMaHuy() + ")")
                        .reasonIfDenied("Vé đã được duyệt hủy.")
                        .build();
            }
        }

        if (item.getTrangThai() == TrangThaiChiTiet.DA_HUY) {
            return TicketCancelEligibility.builder()
                    .allowed(false)
                    .reasonIfDenied("Mục vé này đã bị hủy.")
                    .build();
        }

        DonDat don = item.getDonDat();
        if (don != null && don.getTrangThaiDon() == TrangThaiDon.DA_HUY) {
            return TicketCancelEligibility.builder()
                    .allowed(false)
                    .reasonIfDenied("Đơn hàng đã bị hủy.")
                    .build();
        }

        List<Ve> tickets = veRepository.findByChiTietDon_MaChiTiet(item.getMaChiTiet());
        boolean anyUsed = tickets.stream().anyMatch(v -> v.getTrangThai() == TrangThaiVe.DA_SU_DUNG);
        if (anyUsed) {
            return TicketCancelEligibility.builder()
                    .allowed(false)
                    .reasonIfDenied("Có vé đã được check-in sử dụng, không thể hủy.")
                    .build();
        }

        LocalDate useDate = item.getNgaySuDung();
        if (useDate == null && !tickets.isEmpty()) {
            useDate = tickets.get(0).getNgayThamQuan();
        }
        if (useDate == null) {
            useDate = LocalDate.now();
        }

        long daysBefore = ChronoUnit.DAYS.between(LocalDate.now(), useDate);
        if (daysBefore < 12) {
            String msg = daysBefore < 0
                    ? "Vé đã quá ngày tham quan, không thể hủy."
                    : "Vé chỉ còn " + daysBefore + " ngày trước ngày tham quan (< 12 ngày), không được phép hủy.";
            return TicketCancelEligibility.builder()
                    .allowed(false)
                    .daysBefore(daysBefore)
                    .policyLabel("Không được phép hủy (< 12 ngày)")
                    .reasonIfDenied(msg)
                    .build();
        }

        BigDecimal itemAmount = computeItemAmount(item);
        BigDecimal paidForItem = computePaidPortion(don, itemAmount);
        BigDecimal depositPortion = itemAmount.multiply(new BigDecimal("0.30")).setScale(0, RoundingMode.HALF_UP);

        if (daysBefore >= 15) {
            return TicketCancelEligibility.builder()
                    .allowed(true)
                    .freeCancel(true)
                    .daysBefore(daysBefore)
                    .estimatedRefund(paidForItem)
                    .estimatedForfeit(BigDecimal.ZERO)
                    .policyLabel("Cách >= 15 ngày: Hủy miễn phí (Hoàn 100% tiền đã đóng)")
                    .existingRequestStatus(!requests.isEmpty() ? requests.get(0).getTrangThaiXuLy().name() : null)
                    .existingRequestNote(!requests.isEmpty() ? requests.get(0).getGhiChuXuLy() : null)
                    .build();
        }

        // 12..14 ngày: Hủy mất cọc
        BigDecimal forfeit = paidForItem.min(depositPortion);
        BigDecimal refund = paidForItem.subtract(forfeit).max(BigDecimal.ZERO);
        return TicketCancelEligibility.builder()
                .allowed(true)
                .freeCancel(false)
                .daysBefore(daysBefore)
                .estimatedRefund(refund)
                .estimatedForfeit(forfeit)
                .policyLabel("Cách 12-14 ngày: Hủy mất cọc 30%")
                .existingRequestStatus(!requests.isEmpty() ? requests.get(0).getTrangThaiXuLy().name() : null)
                .existingRequestNote(!requests.isEmpty() ? requests.get(0).getGhiChuXuLy() : null)
                .build();
    }

    @Override
    @Transactional
    public HuyDon submitTicketCancelRequest(String maChiTiet, String lyDo) {
        ChiTietDon item = chiTietDonRepository.findByMaChiTiet(maChiTiet)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chi tiết vé: " + maChiTiet));

        TicketCancelEligibility eligibility = evaluateEligibility(item);
        if (!eligibility.isAllowed()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, eligibility.getReasonIfDenied());
        }

        HuyDon huyDon = new HuyDon();
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        huyDon.setMaHuy("HUY-" + suffix);
        huyDon.setMaNhomHuy("NH-" + suffix);
        huyDon.setChiTietDon(item);

        List<ChinhSachHuy> policies = chinhSachHuyRepository.findMatchingPolicies((int) eligibility.getDaysBefore());
        if (!policies.isEmpty()) {
            huyDon.setChinhSachHuy(policies.get(0));
        }

        huyDon.setSoNgayTruoc((int) eligibility.getDaysBefore());
        huyDon.setThoiGianYeuCau(LocalDateTime.now());
        huyDon.setLyDo(lyDo != null && !lyDo.trim().isEmpty() ? lyDo.trim() : "Khách hàng yêu cầu hủy vé");
        huyDon.setTienHoan(eligibility.getEstimatedRefund());
        huyDon.setTienGiuLai(eligibility.getEstimatedForfeit());
        huyDon.setTrangThaiXuLy(TrangThaiHuyDon.CAN_XAC_NHAN);

        HuyDon saved = huyDonRepository.save(huyDon);
        log.info("Đã tạo yêu cầu hủy vé {} cho chi tiết {} (cách {} ngày, hoàn {}, giữ {})",
                saved.getMaHuy(), maChiTiet, eligibility.getDaysBefore(),
                saved.getTienHoan(), saved.getTienGiuLai());
        return saved;
    }

    @Override
    @Transactional
    public HuyDon approveCancellation(String maHuy, String adminUserCode, String ghiChu) {
        HuyDon huyDon = huyDonRepository.findByMaHuy(maHuy)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy yêu cầu hủy: " + maHuy));

        if (huyDon.getTrangThaiXuLy() != TrangThaiHuyDon.CAN_XAC_NHAN
                && huyDon.getTrangThaiXuLy() != TrangThaiHuyDon.CHO_XU_LY) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Yêu cầu hủy [" + maHuy + "] đã được xử lý trước đó.");
        }

        NguoiDung admin = resolveStaff(adminUserCode);
        huyDon.setTrangThaiXuLy(TrangThaiHuyDon.DA_DUYET);
        huyDon.setNguoiXuLy(admin);
        huyDon.setThoiGianXuLy(LocalDateTime.now());
        huyDon.setGhiChuXuLy(ghiChu != null && !ghiChu.trim().isEmpty()
                ? ghiChu.trim()
                : "Admin đã duyệt yêu cầu hủy vé.");
        huyDonRepository.save(huyDon);

        ChiTietDon item = huyDon.getChiTietDon();
        item.setTrangThai(TrangThaiChiTiet.DA_HUY);
        chiTietDonRepository.save(item);

        // Hủy toàn bộ vé vật lý thuộc chi tiết đơn này
        List<Ve> tickets = veRepository.findByChiTietDon_MaChiTiet(item.getMaChiTiet());
        for (Ve ve : tickets) {
            if (ve.getTrangThai() != TrangThaiVe.DA_SU_DUNG) {
                ve.setTrangThai(TrangThaiVe.DA_HUY);
            }
        }
        veRepository.saveAll(tickets);

        // Hủy quyền lợi combo nếu có
        DonDat don = item.getDonDat();
        if (don != null) {
            List<QuyenLoiDon> quyenLois = quyenLoiDonRepository.findByChiTietDon_DonDat_MaDonDat(don.getMaDonDat());
            for (QuyenLoiDon ql : quyenLois) {
                if (ql.getChiTietDon() != null
                        && item.getMaChiTiet().equals(ql.getChiTietDon().getMaChiTiet())
                        && ql.getTrangThai() == TrangThaiQuyenLoi.HIEU_LUC) {
                    ql.setTrangThai(TrangThaiQuyenLoi.DA_HUY);
                    quyenLoiDonRepository.save(ql);
                }
            }
        }

        // Hoàn trả tồn kho vé nếu có trong KHO_VE
        if (item.getLoaiVe() != null && item.getNgaySuDung() != null && item.getSoLuong() != null) {
            Optional<KhoVe> khoOpt = khoVeRepository.findByLoaiVeAndNgayWithLock(
                    item.getLoaiVe().getMaLoaiVe(), item.getNgaySuDung());
            if (khoOpt.isPresent()) {
                KhoVe kho = khoOpt.get();
                int daBan = kho.getSoLuongDaBan() != null ? kho.getSoLuongDaBan() : 0;
                kho.setSoLuongDaBan(Math.max(0, daBan - item.getSoLuong()));
                khoVeRepository.save(kho);
            }
        }

        // Nếu tất cả chi tiết trong đơn đều đã hủy -> hủy đơn tổng
        if (don != null) {
            List<ChiTietDon> allItems = chiTietDonRepository.findByDonDat_MaDonDat(don.getMaDonDat());
            boolean allCancelled = !allItems.isEmpty()
                    && allItems.stream().allMatch(ct -> ct.getTrangThai() == TrangThaiChiTiet.DA_HUY);
            if (allCancelled) {
                don.setTrangThaiDon(TrangThaiDon.DA_HUY);
                donDatRepository.save(don);
                tableAssignmentService.huyDatBanTheoDon(don.getMaDonDat());
            }
        }

        // Tạo bản ghi hoàn tiền nếu có tiền hoàn > 0 và đã có giao dịch thanh toán thành công
        if (don != null && huyDon.getTienHoan() != null && huyDon.getTienHoan().compareTo(BigDecimal.ZERO) > 0) {
            List<ThanhToan> paidList = thanhToanRepository.findByDonDat_MaDonDatOrderByThoiGianTaoAsc(don.getMaDonDat())
                    .stream()
                    .filter(tt -> tt.getTrangThai() == TrangThaiThanhToan.DA_THANH_TOAN)
                    .toList();
            if (!paidList.isEmpty()) {
                HoanTien ht = new HoanTien();
                ht.setMaHoanTien("HT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                ht.setHuyDon(huyDon);
                ht.setThanhToan(paidList.get(0));
                ht.setSoTien(huyDon.getTienHoan());
                ht.setTrangThai(TrangThaiHoanTien.DA_HOAN);
                ht.setMaGiaoDichHoan("REF-" + huyDon.getMaHuy());
                ht.setThoiGianHoan(LocalDateTime.now());
                ht.setGhiChu("Hoàn tiền do duyệt hủy vé " + huyDon.getMaHuy());
                hoanTienRepository.save(ht);
            }
        }

        log.info("Admin {} đã duyệt hủy vé {}", admin != null ? admin.getMaNguoiDung() : "SYSTEM", maHuy);
        return huyDon;
    }

    @Override
    @Transactional
    public HuyDon rejectCancellation(String maHuy, String adminUserCode, String ghiChu) {
        HuyDon huyDon = huyDonRepository.findByMaHuy(maHuy)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy yêu cầu hủy: " + maHuy));

        if (huyDon.getTrangThaiXuLy() != TrangThaiHuyDon.CAN_XAC_NHAN
                && huyDon.getTrangThaiXuLy() != TrangThaiHuyDon.CHO_XU_LY) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Yêu cầu hủy [" + maHuy + "] đã được xử lý trước đó.");
        }

        NguoiDung admin = resolveStaff(adminUserCode);
        huyDon.setTrangThaiXuLy(TrangThaiHuyDon.TU_CHOI);
        huyDon.setNguoiXuLy(admin);
        huyDon.setThoiGianXuLy(LocalDateTime.now());
        huyDon.setGhiChuXuLy(ghiChu != null && !ghiChu.trim().isEmpty()
                ? ghiChu.trim()
                : "Admin từ chối yêu cầu hủy vé.");
        return huyDonRepository.save(huyDon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HuyDon> listRequests(String status, Pageable pageable) {
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                TrangThaiHuyDon st = TrangThaiHuyDon.valueOf(status.toUpperCase());
                return huyDonRepository.findByTrangThaiXuLyOrderByThoiGianYeuCauDesc(st, pageable);
            } catch (IllegalArgumentException ignored) {}
        }
        return huyDonRepository.findAllByOrderByThoiGianYeuCauDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingRequests() {
        return huyDonRepository.countByTrangThaiXuLy(TrangThaiHuyDon.CAN_XAC_NHAN);
    }

    private BigDecimal computeItemAmount(ChiTietDon item) {
        if (item.getThanhTien() != null) {
            return item.getThanhTien();
        }
        BigDecimal donGia = item.getDonGia() != null ? item.getDonGia() : BigDecimal.ZERO;
        int qty = item.getSoLuong() != null ? item.getSoLuong() : 1;
        return donGia.multiply(BigDecimal.valueOf(qty));
    }

    private BigDecimal computePaidPortion(DonDat don, BigDecimal itemAmount) {
        if (don == null || don.getTrangThaiThanhToan() == TrangThaiThanhToanDon.CHUA_THANH_TOAN) {
            return BigDecimal.ZERO;
        }
        if (don.getTrangThaiThanhToan() == TrangThaiThanhToanDon.DA_THANH_TOAN) {
            return itemAmount;
        }
        if (don.getTrangThaiThanhToan() == TrangThaiThanhToanDon.DA_DAT_COC) {
            return itemAmount.multiply(new BigDecimal("0.30")).setScale(0, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private NguoiDung resolveStaff(String adminUserCode) {
        if (adminUserCode != null && !adminUserCode.isBlank()) {
            Optional<NguoiDung> found = nguoiDungRepository.findByMaNguoiDung(adminUserCode.trim());
            if (found.isPresent()) return found.get();
        }
        return nguoiDungRepository.findAll().stream().findFirst().orElse(null);
    }
}

package com.example.skygatehub.service.order.impl;

import com.example.skygatehub.common.concurrent.AsyncTaskService;
import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.LoaiGiaoDich;
import com.example.skygatehub.common.enums.PhuongThucThanhToan;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.common.exception.BusinessException;
import com.example.skygatehub.common.exception.ErrorCode;
import com.example.skygatehub.common.util.DateRangeUtils;
import com.example.skygatehub.common.util.DayTypeResolver;
import com.example.skygatehub.common.util.OrderCodeGenerator;
import com.example.skygatehub.dto.order.BookingPipelineForm;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.dining.DichVuCaAn;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import com.example.skygatehub.entity.ticket.KhoVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.dining.DichVuCaAnRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.repository.ticket.KhoVeRepository;
import com.example.skygatehub.service.order.CheckoutService;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai nghiệp vụ đặt dịch vụ và thanh toán tập trung (Tiêu chí 2.3, Tiêu chí 5.1).
 * Quản lý toàn bộ giao dịch bằng @Transactional tại tầng ServiceImpl.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final DonDatRepository donDatRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final VeRepository veRepository;
    private final DatPhongRepository datPhongRepository;
    private final DatBanRepository datBanRepository;
    private final ThanhToanRepository thanhToanRepository;

    private final LoaiPhongRepository loaiPhongRepository;
    private final PhongRepository phongRepository;
    private final DichVuAnUongRepository dichVuAnUongRepository;
    private final DichVuCaAnRepository dichVuCaAnRepository;
    private final KhoVeRepository khoVeRepository;

    private final SanPhamVeService sanPhamVeService;
    private final TicketPricingService ticketPricingService;
    private final DayTypeResolver dayTypeResolver;
    private final AsyncTaskService asyncTaskService;

    @Override
    @Transactional
    public DonDat processPipelineBooking(BookingPipelineForm form) {
        log.info("Bắt đầu xử lý quy trình đặt dịch vụ tập trung từ ServiceImpl: {}", form);

        // 1. Kiểm tra thông tin khách hàng
        if (form.getHoTenKhach() == null || form.getHoTenKhach().trim().isEmpty() ||
            form.getSdtKhach() == null || form.getSdtKhach().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Họ tên và số điện thoại khách hàng không được để trống!");
        }

        LocalDate ngayThamQuan = form.getNgayThamQuan() != null ? form.getNgayThamQuan() : LocalDate.now();

        // 2. Khởi tạo thực thể DonDat với mã sinh chống trùng (OrderCodeGenerator)
        String maDonHang = OrderCodeGenerator.generateMaDonHang("CTDG");
        String maDonDat = OrderCodeGenerator.generateMaDonDat();

        DonDat donDat = new DonDat();
        donDat.setMaDonDat(maDonDat);
        donDat.setMaDonHang(maDonHang);
        donDat.setHoTenKhach(form.getHoTenKhach().trim());
        donDat.setSdtKhach(form.getSdtKhach().trim());
        donDat.setEmailKhach(form.getEmailKhach() != null ? form.getEmailKhach().trim() : null);
        donDat.setGhiChu(form.getGhiChu() != null ? form.getGhiChu().trim() : null);
        donDat.setThoiGianDat(LocalDateTime.now());
        donDat.setTrangThaiDon(TrangThaiDon.CHO_XAC_NHAN);
        donDat.setTrangThaiThanhToan(TrangThaiThanhToanDon.CHUA_THANH_TOAN);
        donDat.setMaQr("QR-" + maDonHang);
        donDat.setNgayCapNhat(LocalDateTime.now());

        BigDecimal tongTien = BigDecimal.ZERO;
        List<ChiTietDon> chiTietList = new ArrayList<>();
        List<Ve> veList = new ArrayList<>();
        List<DatPhong> datPhongList = new ArrayList<>();
        List<DatBan> datBanList = new ArrayList<>();

        // 3. Xử lý phân hệ Vé tham quan & Kiểm tra tồn kho KHO_VE (Pessimistic Lock)
        if (form.getTicketCode() != null && form.getTicketQty() != null) {
            for (int i = 0; i < form.getTicketCode().size(); i++) {
                String tCode = form.getTicketCode().get(i);
                Integer qty = (i < form.getTicketQty().size()) ? form.getTicketQty().get(i) : 0;
                if (tCode != null && !tCode.isBlank() && qty != null && qty > 0) {
                    LoaiVe loaiVe = sanPhamVeService.layVeTheoMa(tCode);
                    if (loaiVe != null) {
                        // Kiểm tra và cập nhật tồn kho vé nếu có quản lý trong KHO_VE
                        Optional<KhoVe> khoVeOpt = khoVeRepository.findByLoaiVeAndNgayWithLock(tCode, ngayThamQuan);
                        if (khoVeOpt.isPresent()) {
                            KhoVe khoVe = khoVeOpt.get();
                            if (khoVe.getSoLuongToiDa() != null) {
                                int daBan = khoVe.getSoLuongDaBan() != null ? khoVe.getSoLuongDaBan() : 0;
                                if (daBan + qty > khoVe.getSoLuongToiDa()) {
                                    int conLai = Math.max(0, khoVe.getSoLuongToiDa() - daBan);
                                    throw new BusinessException(ErrorCode.OVERBOOKING,
                                            "Vé [" + loaiVe.getTenLoaiVe() + "] ngày " + ngayThamQuan +
                                            " không đủ số lượng (chỉ còn lại " + conLai + " vé)!");
                                }
                                khoVe.setSoLuongDaBan(daBan + qty);
                                khoVeRepository.save(khoVe);
                            }
                        }

                        BigDecimal unitPrice = ticketPricingService.tinhGiaVe(tCode, ngayThamQuan);
                        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                            unitPrice = loaiVe.getGiaVe() != null ? loaiVe.getGiaVe() : BigDecimal.ZERO;
                        }

                        ChiTietDon ctd = new ChiTietDon();
                        ctd.setMaChiTiet(OrderCodeGenerator.generateMaChiTiet());
                        ctd.setDonDat(donDat);
                        ctd.setMaDonDat(donDat.getMaDonDat());
                        ctd.setLoaiDichVu(LoaiDichVuDon.VE);
                        ctd.setLoaiVe(loaiVe);
                        ctd.setTenSanPham(loaiVe.getTenLoaiVe());
                        ctd.setSoLuong(qty);
                        ctd.setDonGia(unitPrice);
                        ctd.setNgaySuDung(ngayThamQuan);
                        ctd.setTrangThai(TrangThaiChiTiet.HIEU_LUC);

                        BigDecimal subTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                        tongTien = tongTien.add(subTotal);
                        chiTietList.add(ctd);

                        for (int k = 1; k <= qty; k++) {
                            Ve ve = new Ve();
                            ve.setMaVe(OrderCodeGenerator.generateMaVe());
                            ve.setChiTietDon(ctd);
                            ve.setLoaiVe(loaiVe);
                            ve.setMaVeChuoi(maDonHang + "-" + tCode + "-" + k);
                            ve.setNgayThamQuan(ngayThamQuan);
                            ve.setTrangThai(TrangThaiVe.CHUA_SU_DUNG);
                            veList.add(ve);
                        }
                    }
                }
            }
        }

        // 4. Xử lý phân hệ Khách sạn: Kiểm tra phòng trống & Phụ thu +20% đêm thứ Bảy / Ngày lễ (Quy tắc D1)
        if (form.isBookHotel() && form.getRoomTypeCode() != null && form.getRoomQty() != null) {
            LocalDate checkIn = form.getNgayNhanPhong() != null ? form.getNgayNhanPhong() : LocalDate.now();
            LocalDate checkOut = form.getNgayTraPhong() != null ? form.getNgayTraPhong() : checkIn.plusDays(1);
            if (!checkOut.isAfter(checkIn)) {
                checkOut = checkIn.plusDays(1);
            }
            long nights = DateRangeUtils.calculateNights(checkIn, checkOut);
            List<LocalDate> stayNights = DateRangeUtils.getStayNights(checkIn, checkOut);

            for (int i = 0; i < form.getRoomTypeCode().size(); i++) {
                String rCode = form.getRoomTypeCode().get(i);
                Integer qty = (i < form.getRoomQty().size()) ? form.getRoomQty().get(i) : 0;
                if (rCode != null && !rCode.isBlank() && qty != null && qty > 0) {
                    Optional<LoaiPhong> lpOpt = loaiPhongRepository.findByMaLoaiPhong(rCode);
                    if (lpOpt.isPresent()) {
                        LoaiPhong lp = lpOpt.get();

                        // Kiểm tra tình trạng phòng trống trong khoảng thời gian [checkIn, checkOut)
                        List<Phong> availableRooms = phongRepository.findAvailableRooms(rCode, checkIn, checkOut);
                        if (availableRooms.size() < qty) {
                            throw new BusinessException(ErrorCode.OVERBOOKING,
                                    "Loại phòng [" + lp.getTenLoaiPhong() + "] chỉ còn " + availableRooms.size() +
                                    " phòng trống từ " + checkIn + " đến " + checkOut + "!");
                        }

                        // Tính giá phòng chi tiết từng đêm (Quy tắc D1: Thứ Bảy và Ngày lễ +20% phụ thu)
                        BigDecimal basePrice = lp.getGiaCoBan() != null ? lp.getGiaCoBan() : BigDecimal.ZERO;
                        BigDecimal totalPerRoom = BigDecimal.ZERO;
                        for (LocalDate night : stayNights) {
                            BigDecimal multiplier = dayTypeResolver.getRoomNightMultiplier(night);
                            BigDecimal nightRate = basePrice.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
                            totalPerRoom = totalPerRoom.add(nightRate);
                        }

                        ChiTietDon ctd = new ChiTietDon();
                        ctd.setMaChiTiet(OrderCodeGenerator.generateMaChiTiet());
                        ctd.setDonDat(donDat);
                        ctd.setMaDonDat(donDat.getMaDonDat());
                        ctd.setLoaiDichVu(LoaiDichVuDon.PHONG);
                        ctd.setLoaiPhong(lp);
                        ctd.setTenSanPham(lp.getTenLoaiPhong() + " (" + nights + " đêm)");
                        ctd.setSoLuong(qty);
                        ctd.setDonGia(totalPerRoom);
                        ctd.setNgaySuDung(checkIn);
                        ctd.setTrangThai(TrangThaiChiTiet.HIEU_LUC);

                        BigDecimal subTotal = totalPerRoom.multiply(BigDecimal.valueOf(qty));
                        tongTien = tongTien.add(subTotal);
                        chiTietList.add(ctd);

                        DatPhong dp = new DatPhong();
                        dp.setMaDatPhong(OrderCodeGenerator.generateMaDatPhong());
                        dp.setChiTietDon(ctd);
                        dp.setLoaiPhong(lp);
                        dp.setNgayNhanPhong(checkIn);
                        dp.setNgayTraPhong(checkOut);
                        dp.setSoLuongPhong(qty);
                        dp.setSoKhach(form.getSoKhachPhong() != null ? form.getSoKhachPhong() : (qty * 2));
                        dp.setTrangThai(TrangThaiDatPhong.CHO_XAC_NHAN);
                        datPhongList.add(dp);
                    }
                }
            }
        }

        // 5. Xử lý phân hệ Ẩm thực: Tạo ChiTietDon và tự động tạo DatBan cho bộ phận nhà hàng
        if (form.isBookDining() && form.getDiningCode() != null && form.getDiningQty() != null) {
            LocalDate ngayAn = form.getNgayAn() != null ? form.getNgayAn() : LocalDate.now();

            for (int i = 0; i < form.getDiningCode().size(); i++) {
                String dCode = form.getDiningCode().get(i);
                Integer qty = (i < form.getDiningQty().size()) ? form.getDiningQty().get(i) : 0;
                if (dCode != null && !dCode.isBlank() && qty != null && qty > 0) {
                    Optional<DichVuAnUong> dvaOpt = dichVuAnUongRepository.findByMaDichVu(dCode);
                    if (dvaOpt.isPresent()) {
                        DichVuAnUong dva = dvaOpt.get();
                        BigDecimal price = dva.getGiaDichVu() != null ? dva.getGiaDichVu() : BigDecimal.ZERO;

                        ChiTietDon ctd = new ChiTietDon();
                        ctd.setMaChiTiet(OrderCodeGenerator.generateMaChiTiet());
                        ctd.setDonDat(donDat);
                        ctd.setMaDonDat(donDat.getMaDonDat());
                        ctd.setLoaiDichVu(LoaiDichVuDon.AN_UONG);
                        ctd.setDichVuAnUong(dva);
                        ctd.setTenSanPham(dva.getTenDichVu());
                        ctd.setSoLuong(qty);
                        ctd.setDonGia(price);
                        ctd.setNgaySuDung(ngayAn);
                        ctd.setTrangThai(TrangThaiChiTiet.HIEU_LUC);

                        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(qty));
                        tongTien = tongTien.add(subTotal);
                        chiTietList.add(ctd);

                        // Tìm ca ăn để tạo DatBan vào hệ thống điều phối bàn
                        Optional<DichVuCaAn> dvcaOpt = dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn(
                                dCode, form.getMaCaAn() != null ? form.getMaCaAn() : "CA_TRUA");
                        if (dvcaOpt.isEmpty()) {
                            List<DichVuCaAn> list = dichVuCaAnRepository.findByDichVuAnUong_MaDichVu(dCode);
                            if (!list.isEmpty()) {
                                dvcaOpt = Optional.of(list.get(0));
                            }
                        }

                        if (dvcaOpt.isPresent()) {
                            DatBan db = new DatBan();
                            db.setMaDatBan(OrderCodeGenerator.generateMaDatBan());
                            db.setDonDat(donDat);
                            db.setChiTietDon(ctd);
                            db.setDichVuCaAn(dvcaOpt.get());
                            db.setNgayAn(ngayAn);
                            db.setSoKhach(form.getSoKhachAn() != null ? form.getSoKhachAn() : (qty * 2));
                            db.setTrangThai(TrangThaiDatBan.CHO_XAC_NHAN);
                            datBanList.add(db);
                        }
                    }
                }
            }
        }

        if (chiTietList.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "Quý khách chưa chọn bất kỳ dịch vụ nào (Vé tham quan, Phòng nghỉ hoặc Suất ăn).");
        }

        // 6. Tính toán tiền cọc theo cấu hình app.deposit-rate (Quy tắc D3)
        BigDecimal depositRate = dayTypeResolver.getDepositRate();
        BigDecimal tienCocYeuCau = tongTien.multiply(depositRate).setScale(0, RoundingMode.HALF_UP);
        donDat.setTongTien(tongTien);
        donDat.setTienCocYeuCau(tienCocYeuCau);

        // Lưu đơn đặt và các chi tiết liên quan
        donDatRepository.saveAndFlush(donDat);
        chiTietDonRepository.saveAllAndFlush(chiTietList);
        if (!veList.isEmpty()) {
            veRepository.saveAllAndFlush(veList);
        }
        if (!datPhongList.isEmpty()) {
            datPhongRepository.saveAllAndFlush(datPhongList);
        }
        if (!datBanList.isEmpty()) {
            datBanRepository.saveAllAndFlush(datBanList);
        }

        // 7. Tạo bản ghi giao dịch đặt cọc ThanhToan
        PhuongThucThanhToan pttt = PhuongThucThanhToan.CHUYEN_KHOAN;
        if ("TIEN_MAT".equalsIgnoreCase(form.getPhuongThucThanhToan())) {
            pttt = PhuongThucThanhToan.TIEN_MAT;
        } else if ("THE_KHAC".equalsIgnoreCase(form.getPhuongThucThanhToan())) {
            pttt = PhuongThucThanhToan.THE_KHAC;
        }

        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setMaThanhToan(OrderCodeGenerator.generateMaThanhToan());
        thanhToan.setDonDat(donDat);
        thanhToan.setLoaiGiaoDich(LoaiGiaoDich.DAT_COC);
        thanhToan.setPhuongThucThanhToan(pttt);
        thanhToan.setSoTien(tienCocYeuCau);
        thanhToan.setMaGiaoDich("TXN-" + maDonHang);
        thanhToan.setTrangThai(TrangThaiThanhToan.CHO_THANH_TOAN);
        thanhToan.setThoiGianTao(LocalDateTime.now());
        thanhToanRepository.save(thanhToan);

        log.info("Đặt dịch vụ thành công từ Service! Mã đơn: {}, Tổng tiền: {}, Cọc: {}",
                maDonHang, tongTien, tienCocYeuCau);

        // 8. Kích hoạt tác vụ bất đồng bộ @Async nền (Tiêu chí 5.1)
        try {
            asyncTaskService.generateQrCodeAsync(donDat.getMaDonDat());
            String contactTarget = (donDat.getEmailKhach() != null && !donDat.getEmailKhach().isBlank())
                    ? donDat.getEmailKhach() : donDat.getSdtKhach();
            asyncTaskService.sendNotificationAsync(
                    contactTarget,
                    "Xác nhận đặt chỗ thành công - SkyGateHub",
                    "Đơn đặt " + donDat.getMaDonHang() + " đã được tạo với số tiền cọc: " + tienCocYeuCau + " VNĐ.");
        } catch (Exception ex) {
            log.error("Không thể kích hoạt tác vụ @Async nền: {}", ex.getMessage());
        }

        return donDat;
    }
}


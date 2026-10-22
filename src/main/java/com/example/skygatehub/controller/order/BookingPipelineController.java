package com.example.skygatehub.controller.order;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.LoaiGiaoDich;
import com.example.skygatehub.common.enums.PhuongThucThanhToan;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.dto.order.BookingPipelineForm;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller xử lý luồng pipeline đặt dịch vụ riêng biệt của khách hàng:
 * Bước 1: Chọn vé tham quan
 * Bước 2: Chọn khách sạn lưu trú (hoặc bỏ qua)
 * Bước 3: Chọn ẩm thực & nhà hàng (hoặc bỏ qua)
 * Bước 4: Tóm tắt, nhập thông tin liên hệ & thanh toán tiền cọc 30%
 *
 * Lưu ý: Trang này sử dụng Funnel Header riêng biệt, KHÔNG dùng navbar chung của website.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingPipelineController {

    private final SanPhamVeService sanPhamVeService;
    private final TicketPricingService ticketPricingService;
    private final LoaiPhongRepository loaiPhongRepository;
    private final KhuLuuTruRepository khuLuuTruRepository;
    private final DichVuAnUongRepository dichVuAnUongRepository;
    private final CaAnRepository caAnRepository;
    private final DonDatRepository donDatRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final DatPhongRepository datPhongRepository;
    private final VeRepository veRepository;
    private final ThanhToanRepository thanhToanRepository;

    /**
     * Mở giao diện pipeline đặt dịch vụ
     */
    @GetMapping({"/dat-dich-vu", "/booking/pipeline"})
    public String showPipelinePage(Model model) {
        // 1. Dữ liệu vé
        List<LoaiVe> tickets = sanPhamVeService.layDanhSachVeDangBan();
        Map<String, BigDecimal> ticketPrices = new HashMap<>();
        for (LoaiVe lv : tickets) {
            BigDecimal price = ticketPricingService.tinhGiaVe(lv.getMaLoaiVe(), LocalDate.now());
            ticketPrices.put(lv.getMaLoaiVe(), price != null ? price : (lv.getGiaVe() != null ? lv.getGiaVe() : BigDecimal.ZERO));
        }

        // 2. Dữ liệu khách sạn & khu lưu trú
        List<KhuLuuTru> khuList = khuLuuTruRepository.findAllByOrderByMaKhuAsc();
        List<LoaiPhong> roomTypes = loaiPhongRepository.findByTrangThai(true);

        // 3. Dữ liệu ẩm thực & ca ăn
        List<DichVuAnUong> diningServices = dichVuAnUongRepository.findByTrangThai(true);
        List<CaAn> caAnList = caAnRepository.findByTrangThai(true);

        // Ngày mặc định
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        model.addAttribute("pageTitle", "Quy Trình Đặt Dịch Vụ — Cổng Trời Đông Giang");
        model.addAttribute("tickets", tickets);
        model.addAttribute("ticketPrices", ticketPrices);
        model.addAttribute("khuList", khuList);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("diningServices", diningServices);
        model.addAttribute("caAnList", caAnList);
        model.addAttribute("today", today.format(DateTimeFormatter.ISO_DATE));
        model.addAttribute("tomorrow", tomorrow.format(DateTimeFormatter.ISO_DATE));
        model.addAttribute("todayDisplay", today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("tomorrowDisplay", tomorrow.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("depositRatePercent", 30);

        return "order/customer/booking-pipeline";
    }

    /**
     * Xử lý gửi đơn đặt dịch vụ theo pipeline
     */
    @PostMapping("/dat-dich-vu/submit")
    @Transactional
    public String submitPipelineOrder(
            @ModelAttribute BookingPipelineForm form,
            RedirectAttributes redirectAttributes) {

        log.info("Nhận yêu cầu đặt dịch vụ từ pipeline: {}", form);

        // 1. Kiểm tra thông tin khách hàng cơ bản
        if (form.getHoTenKhach() == null || form.getHoTenKhach().trim().isEmpty() ||
            form.getSdtKhach() == null || form.getSdtKhach().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập đầy đủ Họ và tên và Số điện thoại liên hệ.");
            return "redirect:/dat-dich-vu";
        }

        LocalDate ngayThamQuan = form.getNgayThamQuan() != null ? form.getNgayThamQuan() : LocalDate.now();

        // 2. Chuẩn bị đơn đặt DonDat
        String dateSuffix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomSuffix = String.format("%04d", (int) (Math.random() * 9000) + 1000);
        String maDonHang = "CTDG-" + dateSuffix + "-" + randomSuffix;
        String maDonDat = "DD" + (System.currentTimeMillis() % 10000000);
        if (maDonDat.length() > 20) {
            maDonDat = maDonDat.substring(0, 20);
        }

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
        donDat.setMaQr("QR-" + maDonHang + "-" + UUID.randomUUID().toString().substring(0, 8));
        donDat.setNgayCapNhat(LocalDateTime.now());

        BigDecimal tongTien = BigDecimal.ZERO;
        List<ChiTietDon> chiTietList = new ArrayList<>();
        List<Ve> veList = new ArrayList<>();
        List<DatPhong> datPhongList = new ArrayList<>();

        int itemCounter = 1;

        // 3. Xử lý mục Vé tham quan (Bước 1)
        if (form.getTicketCode() != null && form.getTicketQty() != null) {
            for (int i = 0; i < form.getTicketCode().size(); i++) {
                String tCode = form.getTicketCode().get(i);
                Integer qty = (i < form.getTicketQty().size()) ? form.getTicketQty().get(i) : 0;
                if (tCode != null && !tCode.isBlank() && qty != null && qty > 0) {
                    LoaiVe loaiVe = sanPhamVeService.layVeTheoMa(tCode);
                    if (loaiVe != null) {
                        BigDecimal unitPrice = ticketPricingService.tinhGiaVe(tCode, ngayThamQuan);
                        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                            unitPrice = loaiVe.getGiaVe() != null ? loaiVe.getGiaVe() : BigDecimal.ZERO;
                        }

                        ChiTietDon ctd = new ChiTietDon();
                        String maChiTiet = "CTD" + (System.currentTimeMillis() % 1000000) + itemCounter++;
                        if (maChiTiet.length() > 20) maChiTiet = maChiTiet.substring(0, 20);
                        ctd.setMaChiTiet(maChiTiet);
                        ctd.setDonDat(donDat);
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

                        // Tạo danh sách vé quét QR vật lý cho từng cá nhân
                        for (int k = 1; k <= qty; k++) {
                            Ve ve = new Ve();
                            String maVe = "V" + (System.currentTimeMillis() % 1000000) + (k % 1000);
                            if (maVe.length() > 20) maVe = maVe.substring(0, 20);
                            ve.setMaVe(maVe);
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

        // 4. Xử lý mục Khách sạn lưu trú (Bước 2)
        if (form.isBookHotel() && form.getRoomTypeCode() != null && form.getRoomQty() != null) {
            LocalDate checkIn = form.getNgayNhanPhong() != null ? form.getNgayNhanPhong() : LocalDate.now();
            LocalDate checkOut = form.getNgayTraPhong() != null ? form.getNgayTraPhong() : checkIn.plusDays(1);
            if (!checkOut.isAfter(checkIn)) {
                checkOut = checkIn.plusDays(1);
            }
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            if (nights <= 0) nights = 1;

            for (int i = 0; i < form.getRoomTypeCode().size(); i++) {
                String rCode = form.getRoomTypeCode().get(i);
                Integer qty = (i < form.getRoomQty().size()) ? form.getRoomQty().get(i) : 0;
                if (rCode != null && !rCode.isBlank() && qty != null && qty > 0) {
                    Optional<LoaiPhong> lpOpt = loaiPhongRepository.findByMaLoaiPhong(rCode);
                    if (lpOpt.isPresent()) {
                        LoaiPhong lp = lpOpt.get();
                        BigDecimal basePrice = lp.getGiaCoBan() != null ? lp.getGiaCoBan() : BigDecimal.ZERO;
                        BigDecimal totalPerRoom = basePrice.multiply(BigDecimal.valueOf(nights));

                        ChiTietDon ctd = new ChiTietDon();
                        String maChiTiet = "CTD" + (System.currentTimeMillis() % 1000000) + itemCounter++;
                        if (maChiTiet.length() > 20) maChiTiet = maChiTiet.substring(0, 20);
                        ctd.setMaChiTiet(maChiTiet);
                        ctd.setDonDat(donDat);
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

                        // Tạo đặt phòng để lễ tân xếp phòng
                        DatPhong dp = new DatPhong();
                        String maDatPhong = "DP" + (System.currentTimeMillis() % 1000000) + (i % 1000);
                        if (maDatPhong.length() > 20) maDatPhong = maDatPhong.substring(0, 20);
                        dp.setMaDatPhong(maDatPhong);
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

        // 5. Xử lý mục Ẩm thực (Bước 3)
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
                        String maChiTiet = "CTD" + (System.currentTimeMillis() % 1000000) + itemCounter++;
                        if (maChiTiet.length() > 20) maChiTiet = maChiTiet.substring(0, 20);
                        ctd.setMaChiTiet(maChiTiet);
                        ctd.setDonDat(donDat);
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
                    }
                }
            }
        }

        // Đảm bảo có ít nhất 1 dịch vụ được chọn
        if (chiTietList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quý khách chưa chọn bất kỳ dịch vụ nào (Vé tham quan, Phòng nghỉ hoặc Suất ăn). Vui lòng chọn ít nhất 1 dịch vụ.");
            return "redirect:/dat-dich-vu";
        }

        // 6. Tính toán tiền cọc 30% theo quy định Cổng Trời Đông Giang
        BigDecimal tienCocYeuCau = tongTien.multiply(new BigDecimal("0.30")).setScale(0, RoundingMode.HALF_UP);
        donDat.setTongTien(tongTien);
        donDat.setTienCocYeuCau(tienCocYeuCau);

        // Lưu đơn đặt
        donDatRepository.save(donDat);

        // Lưu chi tiết đơn
        for (ChiTietDon ctd : chiTietList) {
            chiTietDonRepository.save(ctd);
        }

        // Lưu danh sách vé quét QR
        for (Ve ve : veList) {
            veRepository.save(ve);
        }

        // Lưu đặt phòng
        for (DatPhong dp : datPhongList) {
            datPhongRepository.save(dp);
        }

        // 7. Tạo bản ghi ThanhToan đặt cọc 30%
        PhuongThucThanhToan pttt = PhuongThucThanhToan.CHUYEN_KHOAN;
        if ("TIEN_MAT".equalsIgnoreCase(form.getPhuongThucThanhToan())) {
            pttt = PhuongThucThanhToan.TIEN_MAT;
        } else if ("THE_KHAC".equalsIgnoreCase(form.getPhuongThucThanhToan())) {
            pttt = PhuongThucThanhToan.THE_KHAC;
        }

        ThanhToan thanhToan = new ThanhToan();
        String maTT = "TT" + (System.currentTimeMillis() % 1000000);
        if (maTT.length() > 20) maTT = maTT.substring(0, 20);
        thanhToan.setMaThanhToan(maTT);
        thanhToan.setDonDat(donDat);
        thanhToan.setLoaiGiaoDich(LoaiGiaoDich.DAT_COC);
        thanhToan.setPhuongThucThanhToan(pttt);
        thanhToan.setSoTien(tienCocYeuCau);
        thanhToan.setMaGiaoDich("TXN-" + dateSuffix + "-" + randomSuffix);
        thanhToan.setTrangThai(TrangThaiThanhToan.CHO_THANH_TOAN);
        thanhToan.setThoiGianTao(LocalDateTime.now());
        thanhToanRepository.save(thanhToan);

        log.info("Đặt dịch vụ thành công! Đơn hàng: {}, Tổng tiền: {}, Cọc: {}", maDonHang, tongTien, tienCocYeuCau);

        return "redirect:/dat-dich-vu/thanh-cong?maDonHang=" + maDonHang;
    }

    /**
     * Giao diện xác nhận và hướng dẫn thanh toán cọc 30% sau khi gửi đơn thành công
     */
    @GetMapping("/dat-dich-vu/thanh-cong")
    public String showBookingSuccess(
            @RequestParam("maDonHang") String maDonHang,
            Model model) {

        Optional<DonDat> donOpt = donDatRepository.findByMaDonHang(maDonHang);
        if (donOpt.isEmpty()) {
            return "redirect:/dat-dich-vu";
        }

        DonDat don = donOpt.get();
        List<ChiTietDon> items = chiTietDonRepository.findByDonDat_MaDonDat(don.getMaDonDat());
        List<Ve> tickets = veRepository.findByChiTietDon_DonDat_MaDonDat(don.getMaDonDat());

        BigDecimal tongTien = don.getTongTien() != null ? don.getTongTien() : BigDecimal.ZERO;
        BigDecimal tienCoc = don.getTienCocYeuCau() != null ? don.getTienCocYeuCau() : tongTien.multiply(new BigDecimal("0.30")).setScale(0, RoundingMode.HALF_UP);
        BigDecimal tienConLai = tongTien.subtract(tienCoc);
        if (tienConLai.compareTo(BigDecimal.ZERO) < 0) tienConLai = BigDecimal.ZERO;

        // Chuỗi VietQR để khách hàng quét nhanh
        // Cú pháp VietQR QuickLink: https://img.vietqr.io/image/970415-113000004568-compact2.png?amount=xxx&addInfo=xxx&accountName=xxx
        String bankId = "970415"; // VietinBank
        String accountNo = "108866644888";
        String accountName = "KDL CONG TROI DONG GIANG";
        String qrImageUrl = String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%s&addInfo=%s&accountName=%s",
                bankId,
                accountNo,
                tienCoc.toPlainString(),
                don.getMaDonHang(),
                accountName.replace(" ", "%20"));

        model.addAttribute("pageTitle", "Đặt Dịch Vụ Thành Công — " + don.getMaDonHang());
        model.addAttribute("don", don);
        model.addAttribute("items", items);
        model.addAttribute("tickets", tickets);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("tienCoc", tienCoc);
        model.addAttribute("tienConLai", tienConLai);
        model.addAttribute("accountNo", accountNo);
        model.addAttribute("accountName", accountName);
        model.addAttribute("bankName", "VietinBank — CN Đà Nẵng");
        model.addAttribute("qrImageUrl", qrImageUrl);

        return "order/customer/booking-success";
    }
}

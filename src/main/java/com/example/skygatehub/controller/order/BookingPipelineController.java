package com.example.skygatehub.controller.order;

import com.example.skygatehub.dto.order.BookingPipelineForm;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.service.order.CheckoutService;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý luồng pipeline đặt dịch vụ của khách hàng:
 * Bước 1: Chọn vé tham quan
 * Bước 2: Chọn khách sạn lưu trú (tùy chọn)
 * Bước 3: Chọn ẩm thực & nhà hàng (tùy chọn)
 * Bước 4: Tóm tắt, nhập thông tin liên hệ & thanh toán tiền cọc 30%
 *
 * Toàn bộ nghiệp vụ tạo đơn, tính giá, trừ kho vé, kiểm tra phòng trống
 * được chuyển sang CheckoutService theo đúng Tiêu chí 2.3.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class BookingPipelineController {

    private final CheckoutService checkoutService;
    private final SanPhamVeService sanPhamVeService;
    private final TicketPricingService ticketPricingService;
    private final LoaiPhongRepository loaiPhongRepository;
    private final KhuLuuTruRepository khuLuuTruRepository;
    private final DichVuAnUongRepository dichVuAnUongRepository;
    private final CaAnRepository caAnRepository;
    private final DonDatRepository donDatRepository;
    private final ChiTietDonRepository chiTietDonRepository;
    private final VeRepository veRepository;

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
        if (!model.containsAttribute("bookingForm")) {
            model.addAttribute("bookingForm", new BookingPipelineForm());
        }

        return "order/customer/booking-pipeline";
    }

    /**
     * Tiếp nhận yêu cầu đặt dịch vụ và ủy thác xử lý cho CheckoutService (Tiêu chí 2.2, 2.3)
     */
    @PostMapping("/dat-dich-vu/submit")
    public String submitPipelineOrder(
            @Valid @ModelAttribute("bookingForm") BookingPipelineForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        log.info("Controller nhận yêu cầu đặt dịch vụ: {}", form);

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            redirectAttributes.addFlashAttribute("bookingForm", form);
            return "redirect:/dat-dich-vu";
        }

        try {
            DonDat donDat = checkoutService.processPipelineBooking(form);
            return "redirect:/dat-dich-vu/thanh-cong?maDonHang=" + donDat.getMaDonHang();
        } catch (Exception ex) {
            log.error("Lỗi khi xử lý đặt dịch vụ: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("bookingForm", form);
            return "redirect:/dat-dich-vu";
        }
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
        BigDecimal tienCoc = don.getTienCocYeuCau() != null ? don.getTienCocYeuCau()
                : tongTien.multiply(new BigDecimal("0.30")).setScale(0, RoundingMode.HALF_UP);
        BigDecimal tienConLai = tongTien.subtract(tienCoc);
        if (tienConLai.compareTo(BigDecimal.ZERO) < 0) tienConLai = BigDecimal.ZERO;

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

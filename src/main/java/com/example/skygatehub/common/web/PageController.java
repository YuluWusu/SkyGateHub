package com.example.skygatehub.common.web;

import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PageController {

    private final DonDatRepository donDatRepository;
    private final ChiTietDonRepository chiTietDonRepository;

    @GetMapping("/gioi-thieu")
    public String showAboutPage(Model model) {
        model.addAttribute("pageTitle", "Giới thiệu — Cổng Trời Đông Giang");
        return "pages/gioi-thieu";
    }

    @GetMapping("/diem-tham-quan")
    public String showAttractionsPage(Model model) {
        model.addAttribute("pageTitle", "Điểm tham quan nổi bật — Cổng Trời Đông Giang");
        return "pages/diem-tham-quan";
    }

    @GetMapping("/ban-do")
    public String showMapPage(Model model) {
        model.addAttribute("pageTitle", "Bản đồ toàn cảnh — Cổng Trời Đông Giang");
        return "pages/ban-do";
    }

    @GetMapping("/lien-he")
    public String showContactPage(Model model) {
        model.addAttribute("pageTitle", "Liên hệ & Hỗ trợ — Cổng Trời Đông Giang");
        return "pages/lien-he";
    }

    @GetMapping("/ve")
    public String redirectToTickets() {
        return "redirect:/tickets";
    }

    @GetMapping("/tra-cuu-don")
    public String showOrderLookupForm(@RequestParam(value = "maDonHang", required = false) String maDonHang,
                                      @RequestParam(value = "sdt", required = false) String sdt,
                                      Model model) {
        if ((maDonHang != null && !maDonHang.trim().isEmpty()) || (sdt != null && !sdt.trim().isEmpty())) {
            return processLookup(maDonHang, sdt, model);
        }
        model.addAttribute("hasSearched", false);
        return "order/customer/tra-cuu-don";
    }

    @PostMapping("/tra-cuu-don")
    public String handleOrderLookup(@RequestParam(value = "maDonHang", required = false) String maDonHang,
                                    @RequestParam(value = "sdt", required = false) String sdt,
                                    Model model) {
        return processLookup(maDonHang, sdt, model);
    }

    private String processLookup(String maDonHang, String sdt, Model model) {
        model.addAttribute("hasSearched", true);
        model.addAttribute("searchCode", maDonHang);
        model.addAttribute("searchPhone", sdt);

        List<DonDat> results = new ArrayList<>();
        if (maDonHang != null && !maDonHang.trim().isEmpty()) {
            Optional<DonDat> byCode = donDatRepository.findByMaDonHang(maDonHang.trim());
            if (byCode.isEmpty()) {
                byCode = donDatRepository.findByMaDonDat(maDonHang.trim());
            }
            byCode.ifPresent(results::add);
        } else if (sdt != null && !sdt.trim().isEmpty()) {
            results = donDatRepository.findBySdtKhachOrderByThoiGianDatDesc(sdt.trim());
        }

        model.addAttribute("orders", results);

        Map<String, List<ChiTietDon>> lineItemsMap = new HashMap<>();
        for (DonDat don : results) {
            List<ChiTietDon> items = chiTietDonRepository.findByDonDat_MaDonDat(don.getMaDonDat());
            lineItemsMap.put(don.getMaDonDat(), items);
        }
        model.addAttribute("lineItemsMap", lineItemsMap);

        return "order/customer/tra-cuu-don";
    }
}

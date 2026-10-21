package com.example.skygatehub.common.web;

import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.NgayDacBiet;
import com.example.skygatehub.service.ticket.BangGiaVeService;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final SanPhamVeService sanPhamVeService;
    private final BangGiaVeService bangGiaVeService;
    private final TicketPricingService ticketPricingService;

    public HomeController(SanPhamVeService sanPhamVeService,
                          BangGiaVeService bangGiaVeService,
                          TicketPricingService ticketPricingService) {
        this.sanPhamVeService = sanPhamVeService;
        this.bangGiaVeService = bangGiaVeService;
        this.ticketPricingService = ticketPricingService;
    }

    @GetMapping("/")
    public String index(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        model.addAttribute("appName", "Cổng Trời Đông Giang — EcoGate");
        model.addAttribute("slogan", "Kiệt tác nghỉ dưỡng & du lịch sinh thái giữa đại ngàn Tây xứ Quảng");
        model.addAttribute("currentDate", today.format(inputFormatter));
        model.addAttribute("tomorrowDate", tomorrow.format(inputFormatter));
        model.addAttribute("currentDateDisplay", today.format(displayFormatter));
        model.addAttribute("tomorrowDateDisplay", tomorrow.format(displayFormatter));

        List<LoaiVe> activeTickets = sanPhamVeService.getActiveTickets();
        model.addAttribute("activeTickets", activeTickets);

        List<LoaiVe> comboTickets = new ArrayList<>();
        List<LoaiVe> standardTickets = new ArrayList<>();
        for (LoaiVe ticket : activeTickets) {
            if (ticket.isCombo()) {
                comboTickets.add(ticket);
            } else {
                standardTickets.add(ticket);
            }
        }
        model.addAttribute("comboTickets", comboTickets);
        model.addAttribute("standardTickets", standardTickets);

        Map<String, BigDecimal> todayPrices = new HashMap<>();
        for (LoaiVe ticket : activeTickets) {
            BigDecimal price = ticketPricingService.calculateTicketPrice(ticket.getMaLoaiVe(), today);
            todayPrices.put(ticket.getMaLoaiVe(), price);
        }
        model.addAttribute("todayPrices", todayPrices);

        List<NgayDacBiet> specialDays = bangGiaVeService.getAllSpecialDays();
        model.addAttribute("specialDays", specialDays);

        return "index";
    }
}

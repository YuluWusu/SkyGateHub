package com.example.skygatehub.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "Cổng Trời Đông Giang — EcoGate");
        model.addAttribute("slogan", "Kiệt tác nghỉ dưỡng & du lịch sinh thái giữa đại ngàn Tây xứ Quảng");
        model.addAttribute("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("tomorrowDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "index";
    }
}

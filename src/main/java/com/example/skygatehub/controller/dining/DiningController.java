package com.example.skygatehub.controller.dining;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiningController {

    @GetMapping({"/am-thuc", "/dining"})
    public String showDiningPage(Model model) {
        model.addAttribute("title", "Ẩm thực & Nhà hàng — Cổng Trời Đông Giang");
        return "dining/customer/menu";
    }
}

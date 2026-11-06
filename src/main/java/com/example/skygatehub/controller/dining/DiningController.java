package com.example.skygatehub.controller.dining;

import com.example.skygatehub.service.dining.DichVuAnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DiningController {

    private final DichVuAnService dichVuAnService;

    @GetMapping({"/am-thuc", "/dining"})
    public String showDiningPage(Model model) {
        model.addAttribute("monAn", dichVuAnService.getDichVuDatDuoc());
        model.addAttribute("title", "Ẩm thực & Nhà hàng — Cổng Trời Đông Giang");
        return "dining/customer/menu";
    }
}

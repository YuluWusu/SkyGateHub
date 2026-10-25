package com.example.skygatehub.controller.order;

import com.example.skygatehub.dto.order.OrderSummaryResponse;
import com.example.skygatehub.service.order.OrderSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderSummaryController {

    private final OrderSummaryService orderSummaryService;

    @GetMapping("/{orderId}/summary")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary(@PathVariable String orderId) {
        OrderSummaryResponse summary = orderSummaryService.getOrderSummary(orderId);
        return ResponseEntity.ok(summary);
    }
}

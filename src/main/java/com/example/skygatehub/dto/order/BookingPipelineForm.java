package com.example.skygatehub.dto.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Form dữ liệu nhận từ giao diện pipeline đặt dịch vụ khách hàng:
 * Bước 1: Vé tham quan
 * Bước 2: Khách sạn (tuỳ chọn)
 * Bước 3: Ẩm thực (tuỳ chọn)
 * Bước 4: Thông tin khách hàng & Thanh toán cọc 30%
 */
@Getter
@Setter
@ToString
public class BookingPipelineForm {

    // Thông tin khách hàng
    private String hoTenKhach;
    private String sdtKhach;
    private String emailKhach;
    private String ghiChu;

    // Bước 1: Vé tham quan
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayThamQuan = LocalDate.now();
    private List<String> ticketCode = new ArrayList<>();
    private List<Integer> ticketQty = new ArrayList<>();

    // Bước 2: Khách sạn (tuỳ chọn)
    private boolean bookHotel = false;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayNhanPhong = LocalDate.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayTraPhong = LocalDate.now().plusDays(1);
    private Integer soKhachPhong = 2;
    private List<String> roomTypeCode = new ArrayList<>();
    private List<Integer> roomQty = new ArrayList<>();

    // Bước 3: Ẩm thực (tuỳ chọn)
    private boolean bookDining = false;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayAn = LocalDate.now();
    private String maCaAn = "CA_TRUA";
    private Integer soKhachAn = 2;
    private List<String> diningCode = new ArrayList<>();
    private List<Integer> diningQty = new ArrayList<>();

    // Bước 4: Phương thức thanh toán
    private String phuongThucThanhToan = "CHUYEN_KHOAN";
}

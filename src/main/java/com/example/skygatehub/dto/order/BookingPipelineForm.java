package com.example.skygatehub.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
 * Bước 2: Khách sạn (tùy chọn)
 * Bước 3: Ẩm thực (tùy chọn)
 * Bước 4: Thông tin khách hàng & Thanh toán cọc 30%
 */
@Getter
@Setter
@ToString
public class BookingPipelineForm {

    // Thông tin khách hàng
    @NotBlank(message = "Vui lòng nhập họ và tên khách hàng")
    @Size(max = 120, message = "Họ tên không được vượt quá 120 ký tự")
    private String hoTenKhach;

    @NotBlank(message = "Vui lòng nhập số điện thoại liên hệ")
    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    private String sdtKhach;

    private String emailKhach;
    private String ghiChu;

    // Bước 1: Vé tham quan
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayThamQuan = LocalDate.now();
    private List<String> ticketCode = new ArrayList<>();
    private List<Integer> ticketQty = new ArrayList<>();

    // Bước 2: Khách sạn (tùy chọn)
    private boolean bookHotel = false;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayNhanPhong = LocalDate.now();
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayTraPhong = LocalDate.now().plusDays(1);
    private Integer soKhachPhong = 2;
    private List<String> roomTypeCode = new ArrayList<>();
    private List<Integer> roomQty = new ArrayList<>();

    // Bước 3: Ẩm thực (tùy chọn)
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

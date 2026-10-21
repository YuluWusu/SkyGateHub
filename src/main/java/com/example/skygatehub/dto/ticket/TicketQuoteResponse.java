package com.example.skygatehub.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketQuoteResponse {

    private String maLoaiVe;
    private String tenLoaiVe;
    private LocalDate ngayThamQuan;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private Boolean conHang;
    private Integer soLuongConLai;
    private String dieuKienGia;

    public TicketQuoteResponse(String maLoaiVe, String tenLoaiVe, LocalDate ngayThamQuan, Integer soLuong, BigDecimal donGia, BigDecimal thanhTien, Boolean conHang, Integer soLuongConLai) {
        this.maLoaiVe = maLoaiVe;
        this.tenLoaiVe = tenLoaiVe;
        this.ngayThamQuan = ngayThamQuan;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.conHang = conHang;
        this.soLuongConLai = soLuongConLai;
    }

    public BigDecimal getUnitPrice() {
        return donGia;
    }

    public BigDecimal getTotalPrice() {
        return thanhTien;
    }

    public Boolean getAvailable() {
        return conHang;
    }

    public Integer getRemaining() {
        return soLuongConLai;
    }
}

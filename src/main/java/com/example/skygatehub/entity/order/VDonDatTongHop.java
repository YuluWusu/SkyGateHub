package com.example.skygatehub.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * Entity ánh xạ View V_DON_DAT_TONG_HOP (read-only) dùng để đối soát doanh thu.
 */
@Entity
@Table(name = "V_DON_DAT_TONG_HOP")
@Immutable
@Getter
@NoArgsConstructor
public class VDonDatTongHop {

    @Id
    @Column(name = "MA_DON_DAT", length = 20)
    private String maDonDat;

    @Column(name = "MA_DON_HANG", length = 30)
    private String maDonHang;

    @Column(name = "TONG_TIEN_DANG_LUU", precision = 18, scale = 2)
    private BigDecimal tongTienDangLuu;

    @Column(name = "TONG_CHI_TIET_HIEU_LUC", precision = 20, scale = 2)
    private BigDecimal tongChiTietHieuLuc;

    @Column(name = "DA_THU", precision = 18, scale = 2)
    private BigDecimal daThu;

    @Column(name = "DA_HOAN", precision = 18, scale = 2)
    private BigDecimal daHoan;

    @Column(name = "DA_THU_RONG", precision = 18, scale = 2)
    private BigDecimal daThuRong;

    @Column(name = "CON_PHAI_THU_THEO_SO_DANG_LUU", precision = 18, scale = 2)
    private BigDecimal conPhaiThuTheoSoDangLuu;

    @Column(name = "TRANG_THAI_THANH_TOAN_TINH", length = 30)
    private String trangThaiThanhToanTinh;
}


package com.example.skygatehub.entity.payment;

import com.example.skygatehub.common.enums.PhamViCoc;
import com.example.skygatehub.common.enums.TrangThaiChinhSachCoc;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity ánh xạ bảng CHINH_SACH_COC trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "CHINH_SACH_COC")
@Getter
@Setter
@NoArgsConstructor
public class ChinhSachCoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_CHINH_SACH_COC", length = 20, nullable = false, unique = true)
    private String maChinhSachCoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHAM_VI_AP_DUNG", length = 20, nullable = false)
    private PhamViCoc phamViApDung = PhamViCoc.TOAN_BO;

    @Column(name = "TY_LE_COC", precision = 5, scale = 2)
    private BigDecimal tyLeCoc;

    @Column(name = "TIEN_COC_CO_DINH", precision = 18, scale = 2)
    private BigDecimal tienCocCoDinh;

    @Column(name = "HIEU_LUC_TU")
    private LocalDate hieuLucTu;

    @Column(name = "HIEU_LUC_DEN")
    private LocalDate hieuLucDen;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 25, nullable = false)
    private TrangThaiChinhSachCoc trangThai = TrangThaiChinhSachCoc.CAN_XAC_NHAN;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (phamViApDung == null) phamViApDung = PhamViCoc.TOAN_BO;
        if (trangThai == null) trangThai = TrangThaiChinhSachCoc.CAN_XAC_NHAN;
    }
}


package com.example.skygatehub.repository.order;

import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.entity.order.ChiTietDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietDonRepository extends JpaRepository<ChiTietDon, Long> {

    Optional<ChiTietDon> findByMaChiTiet(String maChiTiet);

    Optional<ChiTietDon> findByMaChiTietAndDonDat_MaDonDat(String maChiTiet, String maDonDat);

    List<ChiTietDon> findByDonDat_MaDonDat(String maDonDat);

    List<ChiTietDon> findByDonDat_MaDonDatAndTrangThai(String maDonDat, TrangThaiChiTiet trangThai);

    @Query("SELECT COALESCE(SUM(c.soLuong * c.donGia), 0) FROM ChiTietDon c " +
           "WHERE c.donDat.maDonDat = :maDonDat AND c.trangThai = 'HIEU_LUC'")
    BigDecimal calculateTotalHieuLuc(@Param("maDonDat") String maDonDat);

    @Query("SELECT COALESCE(SUM(c.soLuong * c.donGia), 0) FROM ChiTietDon c " +
           "WHERE c.loaiDichVu = :loaiDichVu AND c.trangThai = com.example.skygatehub.common.enums.TrangThaiChiTiet.HIEU_LUC")
    BigDecimal sumRevenueByLoaiDichVu(@Param("loaiDichVu") com.example.skygatehub.common.enums.LoaiDichVuDon loaiDichVu);
}


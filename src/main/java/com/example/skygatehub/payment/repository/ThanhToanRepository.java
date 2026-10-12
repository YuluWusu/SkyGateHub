package com.example.skygatehub.payment.repository;

import com.example.skygatehub.payment.entity.ThanhToan;
import com.example.skygatehub.payment.enums.LoaiThanhToan;
import com.example.skygatehub.payment.enums.TrangThaiThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {

    List<ThanhToan> findByDonDat_MaDonDatOrderByThoiGianGiaoDichAsc(Integer maDonDat);

    @Query("""
        select coalesce(sum(t.soTien), 0) from ThanhToan t
        where t.donDat.maDonDat = :id
          and t.trangThai = :tt
          and t.loaiThanhToan in :loais
    """)
    BigDecimal sumByDonDatAndTrangThaiAndLoaiIn(
            @Param("id") Integer id,
            @Param("tt") TrangThaiThanhToan tt,
            @Param("loais") Collection<LoaiThanhToan> loais
    );

    boolean existsByMaGiaoDich(String maGiaoDich);
}

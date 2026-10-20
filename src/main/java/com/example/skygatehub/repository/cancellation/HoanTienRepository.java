package com.example.skygatehub.repository.cancellation;

import com.example.skygatehub.entity.cancellation.HoanTien;
import com.example.skygatehub.common.enums.TrangThaiHoanTien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoanTienRepository extends JpaRepository<HoanTien, Long> {

    Optional<HoanTien> findByMaHoanTien(String maHoanTien);

    List<HoanTien> findByThanhToan_MaThanhToan(String maThanhToan);

    List<HoanTien> findByHuyDon_MaHuy(String maHuy);

    @Query("SELECT COALESCE(SUM(h.soTien), 0) FROM HoanTien h " +
           "WHERE h.thanhToan.donDat.maDonDat = :maDonDat AND h.trangThai = 'DA_HOAN'")
    BigDecimal sumRefundedByDonDat(@Param("maDonDat") String maDonDat);

    Page<HoanTien> findByTrangThai(TrangThaiHoanTien trangThai, Pageable pageable);
}


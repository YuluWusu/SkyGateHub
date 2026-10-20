package com.example.skygatehub.repository.payment;

import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.entity.payment.ThanhToan;
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
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {

    Optional<ThanhToan> findByMaThanhToan(String maThanhToan);

    Optional<ThanhToan> findByMaGiaoDich(String maGiaoDich);

    List<ThanhToan> findByDonDat_MaDonDatOrderByThoiGianTaoAsc(String maDonDat);

    List<ThanhToan> findByDonDat_MaDonDatAndTrangThai(String maDonDat, TrangThaiThanhToan trangThai);

    @Query("SELECT COALESCE(SUM(t.soTien), 0) FROM ThanhToan t " +
           "WHERE t.donDat.maDonDat = :maDonDat AND t.trangThai = 'DA_THANH_TOAN'")
    BigDecimal sumPaidByDonDat(@Param("maDonDat") String maDonDat);

    Page<ThanhToan> findByTrangThai(TrangThaiThanhToan trangThai, Pageable pageable);
}


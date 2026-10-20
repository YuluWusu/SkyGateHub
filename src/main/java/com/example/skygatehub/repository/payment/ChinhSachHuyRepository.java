package com.example.skygatehub.repository.payment;

import com.example.skygatehub.entity.payment.ChinhSachHuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChinhSachHuyRepository extends JpaRepository<ChinhSachHuy, Long> {

    Optional<ChinhSachHuy> findByMaChinhSachHuy(String maChinhSachHuy);

    List<ChinhSachHuy> findByTrangThai(Boolean trangThai);

    /**
     * Tìm chính sách hủy khớp với số ngày báo trước
     */
    @Query("SELECT c FROM ChinhSachHuy c WHERE c.trangThai = true " +
           "AND c.soNgayTu <= :soNgay " +
           "AND (c.soNgayDen IS NULL OR c.soNgayDen >= :soNgay) " +
           "ORDER BY c.soNgayTu DESC")
    List<ChinhSachHuy> findMatchingPolicies(@Param("soNgay") Integer soNgay);
}


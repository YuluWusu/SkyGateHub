package com.example.skygatehub.repository.order;

import com.example.skygatehub.common.enums.TrangThaiQuyenLoi;
import com.example.skygatehub.entity.order.QuyenLoiDon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuyenLoiDonRepository extends JpaRepository<QuyenLoiDon, Long> {

    Optional<QuyenLoiDon> findByMaQuyenLoi(String maQuyenLoi);

    List<QuyenLoiDon> findByChiTietDon_DonDat_MaDonDat(String maDonDat);

    List<QuyenLoiDon> findByChiTietDon_DonDat_MaDonDatAndTrangThai(String maDonDat, TrangThaiQuyenLoi trangThai);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM QuyenLoiDon q WHERE q.maQuyenLoi = :maQuyenLoi")
    Optional<QuyenLoiDon> findByMaQuyenLoiWithLock(@Param("maQuyenLoi") String maQuyenLoi);
}


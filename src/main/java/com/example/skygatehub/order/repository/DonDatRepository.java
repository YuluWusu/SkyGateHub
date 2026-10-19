package com.example.skygatehub.order.repository;

import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.order.entity.DonDat;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonDatRepository extends JpaRepository<DonDat, Long> {

    Optional<DonDat> findByMaDonDat(String maDonDat);

    Optional<DonDat> findByMaDonHang(String maDonHang);

    Optional<DonDat> findByMaQr(String maQr);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DonDat d WHERE d.maDonDat = :maDonDat")
    Optional<DonDat> findByMaDonDatWithLock(@Param("maDonDat") String maDonDat);

    boolean existsByMaDonDat(String maDonDat);

    boolean existsByMaDonHang(String maDonHang);

    Page<DonDat> findByTrangThaiDonOrderByThoiGianDatDesc(TrangThaiDon trangThaiDon, Pageable pageable);

    List<DonDat> findBySdtKhachOrderByThoiGianDatDesc(String sdtKhach);

    List<DonDat> findByNguoiDung_MaNguoiDungOrderByThoiGianDatDesc(String maNguoiDung);

    /**
     * Dùng cho scheduled task quét đơn hết hạn thanh toán
     */
    List<DonDat> findByTrangThaiDonAndThoiGianDatBefore(TrangThaiDon trangThaiDon, LocalDateTime thoiGian);
}

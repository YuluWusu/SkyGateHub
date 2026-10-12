package com.example.skygatehub.order.repository;

import com.example.skygatehub.order.entity.DonDat;
import com.example.skygatehub.order.enums.TrangThaiDon;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonDatRepository extends JpaRepository<DonDat, Integer> {

    Optional<DonDat> findByMaDonCode(String maDonCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DonDat d where d.maDonDat = :id")
    Optional<DonDat> findByIdForUpdate(@Param("id") Integer id);

    boolean existsByMaDonCode(String maDonCode);

    Page<DonDat> findByKhachHang_MaNguoiDungOrderByNgayDatDesc(Integer maKhachHang, Pageable pageable);

    Page<DonDat> findByTrangThaiDonOrderByNgayDatDesc(TrangThaiDon trangThaiDon, Pageable pageable);

    List<DonDat> findByKhachHang_SoDienThoaiOrderByNgayDatDesc(String soDienThoai);
}

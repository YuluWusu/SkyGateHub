package com.example.skygatehub.checkin.repository;

import com.example.skygatehub.checkin.entity.CheckIn;
import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByMaCheckIn(String maCheckIn);

    Optional<CheckIn> findByVe_MaVe(String maVe);

    Optional<CheckIn> findByDatPhong_MaDatPhong(String maDatPhong);

    Optional<CheckIn> findByDatBan_MaDatBan(String maDatBan);

    List<CheckIn> findByDonDat_MaDonDat(String maDonDat);

    List<CheckIn> findByNhanVien_MaNguoiDung(String maNguoiDung);

    Page<CheckIn> findByLoaiCheckInAndTrangThai(LoaiCheckIn loaiCheckIn, TrangThaiCheckIn trangThai, Pageable pageable);

    boolean existsByVe_MaVe(String maVe);

    boolean existsByDatPhong_MaDatPhong(String maDatPhong);

    boolean existsByDatBan_MaDatBan(String maDatBan);
}

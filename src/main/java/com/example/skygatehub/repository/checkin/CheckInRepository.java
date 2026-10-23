package com.example.skygatehub.repository.checkin;

import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    @Query("SELECT c FROM CheckIn c WHERE c.maCheckIn = :maCheckIn")
    Optional<CheckIn> findByMaCheckIn(@Param("maCheckIn") String maCheckIn);

    Optional<CheckIn> findByVe_MaVe(String maVe);

    Optional<CheckIn> findByDatPhong_MaDatPhong(String maDatPhong);

    Optional<CheckIn> findByDatBan_MaDatBan(String maDatBan);

    List<CheckIn> findByDonDat_MaDonDat(String maDonDat);

    List<CheckIn> findByNhanVien_MaNguoiDung(String maNguoiDung);

    @Query("SELECT c FROM CheckIn c WHERE c.loaiCheckIn = :loaiCheckIn AND c.trangThai = :trangThai")
    Page<CheckIn> findByLoaiCheckInAndTrangThai(@Param("loaiCheckIn") LoaiCheckIn loaiCheckIn, @Param("trangThai") TrangThaiCheckIn trangThai, Pageable pageable);

    boolean existsByVe_MaVe(String maVe);

    boolean existsByDatPhong_MaDatPhong(String maDatPhong);

    boolean existsByDatBan_MaDatBan(String maDatBan);

    @Query("SELECT c FROM CheckIn c WHERE c.loaiCheckIn = :loaiCheckIn ORDER BY c.thoiGianCheckIn DESC")
    List<CheckIn> findRecentByLoaiCheckIn(@Param("loaiCheckIn") LoaiCheckIn loaiCheckIn, Pageable pageable);
}


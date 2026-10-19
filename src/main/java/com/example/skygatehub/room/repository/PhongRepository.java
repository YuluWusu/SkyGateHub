package com.example.skygatehub.room.repository;

import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.room.entity.Phong;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Long> {

    Optional<Phong> findByMaPhong(String maPhong);

    Optional<Phong> findBySoPhong(String soPhong);

    List<Phong> findByLoaiPhong_MaLoaiPhong(String maLoaiPhong);

    List<Phong> findByTrangThai(TrangThaiPhong trangThai);

    Page<Phong> findByLoaiPhong_MaLoaiPhong(String maLoaiPhong, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Phong p WHERE p.maPhong = :maPhong")
    Optional<Phong> findByMaPhongWithLock(@Param("maPhong") String maPhong);

    /**
     * Tìm các phòng thuộc loại phòng cho trước, trạng thái SAN_SANG,
     * chưa bị phân phòng trùng khoảng ngày (ngayNhan < ngayTraPhong && ngayTra > ngayNhanPhong).
     */
    @Query("""
        SELECT p FROM Phong p
        WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong
          AND p.trangThai = 'SAN_SANG'
          AND NOT EXISTS (
            SELECT pp FROM PhanPhong pp
            WHERE pp.phong.maPhong = p.maPhong
              AND pp.trangThai <> 'DA_HUY'
              AND pp.datPhong.ngayNhanPhong < :ngayTra
              AND pp.datPhong.ngayTraPhong > :ngayNhan
          )
    """)
    List<Phong> findAvailableRooms(
            @Param("maLoaiPhong") String maLoaiPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra);
}

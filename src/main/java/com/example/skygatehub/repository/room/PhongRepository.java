package com.example.skygatehub.repository.room;

import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.entity.room.Phong;
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

    boolean existsByMaPhong(String maPhong);

    boolean existsBySoPhong(String soPhong);

    boolean existsBySoPhongAndMaPhongNot(String soPhong, String maPhong);

    List<Phong> findByLoaiPhong_MaLoaiPhong(String maLoaiPhong);

    List<Phong> findByTrangThai(TrangThaiPhong trangThai);

    long countByTrangThai(TrangThaiPhong trangThai);

    Page<Phong> findByLoaiPhong_MaLoaiPhong(String maLoaiPhong, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Phong p WHERE p.maPhong = :maPhong")
    Optional<Phong> findByMaPhongWithLock(@Param("maPhong") String maPhong);

    @Query("SELECT COUNT(pp) FROM PhanPhong pp WHERE pp.phong.maPhong = :maPhong")
    long countPhanPhongByMaPhong(@Param("maPhong") String maPhong);

    /**
     * Tìm danh sách phòng vật lý khả dụng (SAN_SANG) của một maLoaiPhong
     * mà KHÔNG bị trùng lịch phân phòng trong khoảng ngày [ngayNhan, ngayTra) của đơn đặt phòng.
     */
    @Query("""
        SELECT p FROM Phong p
        WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong
          AND p.trangThai <> 'BAO_TRI'
          AND NOT EXISTS (
              SELECT pp FROM PhanPhong pp
              JOIN pp.datPhong dp
              WHERE pp.phong = p
                AND pp.trangThai IN ('DANG_PHAN', 'DA_NHAN_PHONG')
                AND dp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG')
                AND dp.ngayNhanPhong < :ngayTra
                AND dp.ngayTraPhong > :ngayNhan
          )
        ORDER BY p.soPhong ASC
    """)
    List<Phong> findAvailableRooms(
            @Param("maLoaiPhong") String maLoaiPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra);

    @Query(
        value = """
            SELECT p FROM Phong p
            LEFT JOIN FETCH p.loaiPhong lp
            LEFT JOIN FETCH lp.khuLuuTru klt
            WHERE (:maKhu IS NULL OR klt.maKhu = :maKhu)
              AND (:maLoaiPhong IS NULL OR :maLoaiPhong = '' OR lp.maLoaiPhong = :maLoaiPhong)
              AND (:trangThai IS NULL OR p.trangThai = :trangThai)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.soPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.maPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(lp.tenLoaiPhong) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
        countQuery = """
            SELECT COUNT(p) FROM Phong p
            LEFT JOIN p.loaiPhong lp
            LEFT JOIN lp.khuLuuTru klt
            WHERE (:maKhu IS NULL OR klt.maKhu = :maKhu)
              AND (:maLoaiPhong IS NULL OR :maLoaiPhong = '' OR lp.maLoaiPhong = :maLoaiPhong)
              AND (:trangThai IS NULL OR p.trangThai = :trangThai)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(p.soPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.maPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(lp.tenLoaiPhong) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """
    )
    Page<Phong> searchPhong(
            @Param("maKhu") Integer maKhu,
            @Param("maLoaiPhong") String maLoaiPhong,
            @Param("trangThai") TrangThaiPhong trangThai,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
        SELECT p FROM Phong p
        LEFT JOIN FETCH p.loaiPhong lp
        LEFT JOIN FETCH lp.khuLuuTru klt
        WHERE (:maKhu IS NULL OR klt.maKhu = :maKhu)
          AND (:maLoaiPhong IS NULL OR :maLoaiPhong = '' OR lp.maLoaiPhong = :maLoaiPhong)
        ORDER BY COALESCE(klt.maKhu, 999) ASC, lp.maLoaiPhong ASC, p.soPhong ASC
    """)
    List<Phong> findAllForRoomMap(
            @Param("maKhu") Integer maKhu,
            @Param("maLoaiPhong") String maLoaiPhong);
}

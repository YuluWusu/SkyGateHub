package com.example.skygatehub.repository.room;

import com.example.skygatehub.common.enums.TrangThaiPhanPhong;
import com.example.skygatehub.entity.room.PhanPhong;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhanPhongRepository extends JpaRepository<PhanPhong, Long> {

    Optional<PhanPhong> findByMaPhanPhong(String maPhanPhong);

    List<PhanPhong> findByDatPhong_MaDatPhong(String maDatPhong);

    List<PhanPhong> findByPhong_MaPhong(String maPhong);

    List<PhanPhong> findByDatPhong_MaDatPhongAndTrangThaiIn(String maDatPhong, Collection<TrangThaiPhanPhong> trangThais);

    boolean existsByPhong_MaPhongAndTrangThaiIn(String maPhong, Collection<TrangThaiPhanPhong> trangThais);

    List<PhanPhong> findByPhong_MaPhongAndTrangThaiIn(String maPhong, Collection<TrangThaiPhanPhong> trangThais);

    long countByDatPhong_MaDatPhongAndTrangThaiNot(String maDatPhong, TrangThaiPhanPhong trangThai);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT COUNT(pp) > 0 FROM PhanPhong pp
        JOIN pp.datPhong dp
        WHERE pp.phong.maPhong = :maPhong
          AND pp.trangThai IN ('DANG_PHAN', 'DA_NHAN_PHONG')
          AND dp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG')
          AND dp.ngayNhanPhong < :ngayTra
          AND dp.ngayTraPhong > :ngayNhan
    """)
    boolean existsOverlappingAssignmentWithLock(
            @Param("maPhong") String maPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra);

    @Query("""
        SELECT COUNT(pp) > 0 FROM PhanPhong pp
        JOIN pp.datPhong dp
        WHERE pp.phong.maPhong = :maPhong
          AND pp.trangThai IN ('DANG_PHAN', 'DA_NHAN_PHONG')
          AND dp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG')
          AND dp.ngayNhanPhong < :ngayTra
          AND dp.ngayTraPhong > :ngayNhan
    """)
    boolean existsOverlappingAssignment(
            @Param("maPhong") String maPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra);

    /**
     * Lấy toàn bộ các bản ghi PhanPhong đang hiệu lực trong một ngày cụ thể :targetDate.
     * Sử dụng JOIN FETCH toàn diện tránh LazyInitializationException khi vẽ Room Map.
     */
    @Query("""
        SELECT pp FROM PhanPhong pp
        JOIN FETCH pp.phong p
        JOIN FETCH p.loaiPhong lp
        JOIN FETCH lp.khuLuuTru klt
        JOIN FETCH pp.datPhong dp
        JOIN FETCH dp.chiTietDon ctd
        JOIN FETCH ctd.donDat dd
        WHERE dp.ngayNhanPhong <= :targetDate
          AND dp.ngayTraPhong > :targetDate
          AND pp.trangThai IN ('DANG_PHAN', 'DA_NHAN_PHONG')
          AND dp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG')
    """)
    List<PhanPhong> findActiveAssignmentsOnDate(@Param("targetDate") LocalDate targetDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT pp FROM PhanPhong pp WHERE pp.datPhong.maDatPhong = :maDatPhong AND pp.trangThai <> 'DA_HUY'")
    List<PhanPhong> findByDatPhongForUpdate(@Param("maDatPhong") String maDatPhong);

    @Query("SELECT pp.maPhanPhong FROM PhanPhong pp ORDER BY pp.id DESC")
    List<String> findLatestMaPhanPhong();
}

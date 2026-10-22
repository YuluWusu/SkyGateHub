package com.example.skygatehub.repository.roombooking;

import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatPhongRepository extends JpaRepository<DatPhong, Long> {

    Optional<DatPhong> findByMaDatPhong(String maDatPhong);

    List<DatPhong> findByChiTietDon_DonDat_MaDonDat(String maDonDat);

    List<DatPhong> findByTrangThai(TrangThaiDatPhong trangThai);

    @Query("""
        SELECT COALESCE(SUM(dp.soLuongPhong), 0) FROM DatPhong dp
        WHERE dp.loaiPhong.maLoaiPhong = :maLoaiPhong
          AND dp.trangThai <> 'DA_HUY'
          AND dp.chiTietDon.donDat.trangThaiDon <> 'DA_HUY'
          AND dp.ngayNhanPhong < :ngayTra
          AND dp.ngayTraPhong > :ngayNhan
    """)
    Integer sumBookedRoomsOverlapping(
            @Param("maLoaiPhong") String maLoaiPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra
    );

    @Query("""
        SELECT dp FROM DatPhong dp
        JOIN FETCH dp.loaiPhong lp
        JOIN FETCH lp.khuLuuTru klt
        JOIN FETCH dp.chiTietDon ctd
        JOIN FETCH ctd.donDat dd
        WHERE dp.trangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN')
          AND dd.trangThaiDon <> 'DA_HUY'
        ORDER BY dp.ngayNhanPhong ASC, dp.id ASC
    """)
    List<DatPhong> findAllPendingBookingsWithDetails();

    @Query("""
        SELECT dp FROM DatPhong dp
        JOIN FETCH dp.loaiPhong lp
        JOIN FETCH lp.khuLuuTru klt
        JOIN FETCH dp.chiTietDon ctd
        JOIN FETCH ctd.donDat dd
        WHERE dp.maDatPhong = :maDatPhong
    """)
    Optional<DatPhong> findByMaDatPhongWithDetails(@Param("maDatPhong") String maDatPhong);
}

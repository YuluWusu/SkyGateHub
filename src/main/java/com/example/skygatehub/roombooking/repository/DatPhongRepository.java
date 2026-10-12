package com.example.skygatehub.roombooking.repository;

import com.example.skygatehub.order.enums.TrangThaiDon;
import com.example.skygatehub.roombooking.entity.DatPhong;
import com.example.skygatehub.roombooking.enums.TrangThaiDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DatPhongRepository extends JpaRepository<DatPhong, Integer> {

    @Query("""
        select coalesce(sum(dp.soLuongPhong), 0) from DatPhong dp
        where dp.loaiPhong.maLoaiPhong = :maLoaiPhong
          and dp.trangThai <> :trangThaiLoaiTru
          and dp.donDat.trangThaiDon <> :donLoaiTru
          and dp.ngayNhanPhong < :ngayTra
          and dp.ngayTraPhong > :ngayNhan
    """)
    Integer sumBookedRoomsOverlapping(
            @Param("maLoaiPhong") Integer maLoaiPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra,
            @Param("trangThaiLoaiTru") TrangThaiDatPhong trangThaiLoaiTru,
            @Param("donLoaiTru") TrangThaiDon donLoaiTru
    );

    List<DatPhong> findByTrangThaiOrderByNgayNhanPhongAsc(TrangThaiDatPhong tt);

    List<DatPhong> findByDonDat_MaDonDat(Integer maDonDat);

    @Query("select coalesce(sum(dp.thanhTien), 0) from DatPhong dp where dp.donDat.maDonDat = :maDonDat")
    BigDecimal sumThanhTienByDonDat(@Param("maDonDat") Integer maDonDat);
}

package com.example.skygatehub.room.repository;

import com.example.skygatehub.room.entity.Phong;
import com.example.skygatehub.room.enums.TrangThaiPhong;
import com.example.skygatehub.roombooking.enums.TrangThaiDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Integer> {

    long countByLoaiPhong_MaLoaiPhongAndTrangThaiNot(Integer maLoaiPhong, TrangThaiPhong loaiTru);

    @Query("""
        select p from Phong p
        where p.loaiPhong.maLoaiPhong = :maLoaiPhong
          and p.trangThai <> :baoTri
          and not exists (
            select pp from PhanPhong pp
            where pp.phong = p
              and pp.datPhong.trangThai <> :daTra
              and pp.datPhong.ngayNhanPhong < :ngayTra
              and pp.datPhong.ngayTraPhong > :ngayNhan
          )
    """)
    List<Phong> findAvailableForAssignment(
            @Param("maLoaiPhong") Integer maLoaiPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra,
            @Param("baoTri") TrangThaiPhong baoTri,
            @Param("daTra") TrangThaiDatPhong daTra
    );

    List<Phong> findAllByOrderByTangAscSoPhongAsc();

    boolean existsBySoPhong(String soPhong);
}

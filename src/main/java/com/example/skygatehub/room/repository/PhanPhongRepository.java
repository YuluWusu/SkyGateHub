package com.example.skygatehub.room.repository;

import com.example.skygatehub.room.entity.PhanPhong;
import com.example.skygatehub.roombooking.enums.TrangThaiDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhanPhongRepository extends JpaRepository<PhanPhong, Integer> {

    @Query("""
        select count(pp) > 0 from PhanPhong pp
        where pp.phong.maPhong = :maPhong
          and pp.datPhong.trangThai <> :daTra
          and pp.datPhong.ngayNhanPhong < :ngayTra
          and pp.datPhong.ngayTraPhong > :ngayNhan
    """)
    boolean existsOverlappingAssignment(
            @Param("maPhong") Integer maPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra,
            @Param("daTra") TrangThaiDatPhong daTra
    );

    long countByDatPhong_MaDatPhong(Integer maDatPhong);

    List<PhanPhong> findByDatPhong_MaDatPhong(Integer maDatPhong);

    @Query("""
        select pp from PhanPhong pp
        where pp.datPhong.trangThai <> :daTra
          and pp.datPhong.ngayNhanPhong <= :ngay
          and pp.datPhong.ngayTraPhong > :ngay
    """)
    List<PhanPhong> findActiveOnDate(
            @Param("ngay") LocalDate ngay,
            @Param("daTra") TrangThaiDatPhong daTra
    );
}

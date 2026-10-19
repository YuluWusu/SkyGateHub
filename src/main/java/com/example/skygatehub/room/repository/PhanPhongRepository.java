package com.example.skygatehub.room.repository;

import com.example.skygatehub.common.enums.TrangThaiPhanPhong;
import com.example.skygatehub.room.entity.PhanPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhanPhongRepository extends JpaRepository<PhanPhong, Long> {

    Optional<PhanPhong> findByMaPhanPhong(String maPhanPhong);

    List<PhanPhong> findByDatPhong_MaDatPhong(String maDatPhong);

    List<PhanPhong> findByPhong_MaPhong(String maPhong);

    @Query("""
        SELECT COUNT(pp) > 0 FROM PhanPhong pp
        WHERE pp.phong.maPhong = :maPhong
          AND pp.trangThai <> 'DA_HUY'
          AND pp.datPhong.ngayNhanPhong < :ngayTra
          AND pp.datPhong.ngayTraPhong > :ngayNhan
    """)
    boolean existsOverlappingAssignment(
            @Param("maPhong") String maPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra
    );

    long countByDatPhong_MaDatPhongAndTrangThaiNot(String maDatPhong, TrangThaiPhanPhong trangThai);
}

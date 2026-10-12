package com.example.skygatehub.checkin.repository;

import com.example.skygatehub.checkin.entity.LichSuCheckIn;
import com.example.skygatehub.checkin.enums.LoaiDichVuCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LichSuCheckInRepository extends JpaRepository<LichSuCheckIn, Integer> {

    @Query("SELECT l FROM LichSuCheckIn l WHERE l.donDat.maDonDat = :maDonDat ORDER BY l.thoiGianCheckIn ASC")
    List<LichSuCheckIn> findByDonDat_MaDonDatOrderByThoiGianCheckInAsc(@Param("maDonDat") Integer maDonDat);

    @Query("""
        SELECT COUNT(l) > 0 FROM LichSuCheckIn l
        WHERE l.donDat.maDonDat = :maDonDat
          AND l.loaiDichVuCheckIn = :loaiDichVuCheckIn
          AND l.thoiGianCheckIn BETWEEN :tu AND :den
    """)
    boolean existsByDonDat_MaDonDatAndLoaiDichVuCheckInAndThoiGianCheckInBetween(
            @Param("maDonDat") Integer maDonDat,
            @Param("loaiDichVuCheckIn") LoaiDichVuCheckIn loaiDichVuCheckIn,
            @Param("tu") LocalDateTime tu,
            @Param("den") LocalDateTime den
    );

    boolean existsByDonDat_MaDonDat(Integer maDonDat);
}

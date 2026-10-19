package com.example.skygatehub.dining.repository;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.dining.entity.BanAn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BanAnRepository extends JpaRepository<BanAn, Long> {

    Optional<BanAn> findByMaBanAn(String maBanAn);

    Optional<BanAn> findBySoBan(String soBan);

    boolean existsBySoBan(String soBan);

    List<BanAn> findByKhuVuc(String khuVuc);

    List<BanAn> findByTrangThai(TrangThaiBanAn trangThai);

    Page<BanAn> findByTrangThai(TrangThaiBanAn trangThai, Pageable pageable);

    /**
     * Tìm bàn ăn trống cho ca ăn và ngày ăn cụ thể (chưa bị giữ chỗ bởi DAT_BAN chưa hủy).
     */
    @Query("""
        SELECT b FROM BanAn b
        WHERE b.sucChua >= :soKhach
          AND b.trangThai = 'SAN_SANG'
          AND NOT EXISTS (
            SELECT db FROM DatBan db
            WHERE db.banAn.maBanAn = b.maBanAn
              AND db.ngayAn = :ngayAn
              AND db.dichVuCaAn.caAn.maCaAn = :maCaAn
              AND db.trangThai <> 'DA_HUY'
          )
        ORDER BY b.sucChua ASC
    """)
    List<BanAn> findAvailableTables(
            @Param("soKhach") Integer soKhach,
            @Param("ngayAn") LocalDate ngayAn,
            @Param("maCaAn") String maCaAn
    );
}

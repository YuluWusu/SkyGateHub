package com.example.skygatehub.repository.dining;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.entity.dining.BanAn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BanAnRepository extends JpaRepository<BanAn, Long> {

    Optional<BanAn> findByMaBanAn(String maBanAn);

    Optional<BanAn> findBySoBan(String soBan);

    boolean existsBySoBan(String soBan);

    boolean existsByMaBanAn(String maBanAn);

    boolean existsBySoBanAndMaBanAnNot(String soBan, String maBanAn);

    @Query("""
        SELECT b FROM BanAn b
        WHERE (:keyword IS NULL
               OR LOWER(b.soBan) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.maBanAn) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:khuVuc IS NULL OR b.khuVuc = :khuVuc)
          AND (:trangThai IS NULL OR b.trangThai = :trangThai)
    """)
    Page<BanAn> searchBanAn(
            @Param("keyword") String keyword,
            @Param("khuVuc") String khuVuc,
            @Param("trangThai") TrangThaiBanAn trangThai,
            Pageable pageable
    );

    @Query("SELECT COUNT(d) FROM DatBan d WHERE d.banAn.maBanAn = :maBanAn")
    long countDatBanByMaBanAn(@Param("maBanAn") String maBanAn);

    /**
     * Đếm đặt bàn còn hiệu lực (trạng thái không thuộc danh sách loại trừ) từ một ngày trở đi.
     */
    @Query("""
        SELECT COUNT(d) FROM DatBan d
        WHERE d.banAn.maBanAn = :ma
          AND d.ngayAn >= :from
          AND d.trangThai NOT IN :excluded
    """)
    long countActiveDatBanFrom(
            @Param("ma") String ma,
            @Param("from") LocalDate from,
            @Param("excluded") Collection<TrangThaiDatBan> excluded
    );

    /**
     * Mã các lượt đặt bàn đang ở một trạng thái (vd DA_CHECK_IN) trên một bàn.
     */
    @Query("SELECT d.maDatBan FROM DatBan d WHERE d.banAn.maBanAn = :ma AND d.trangThai = :trangThai")
    List<String> findMaDatBanByBanAnAndTrangThai(
            @Param("ma") String ma,
            @Param("trangThai") TrangThaiDatBan trangThai
    );

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


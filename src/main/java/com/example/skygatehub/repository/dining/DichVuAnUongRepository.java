package com.example.skygatehub.repository.dining;

import com.example.skygatehub.entity.dining.DichVuAnUong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DichVuAnUongRepository extends JpaRepository<DichVuAnUong, Long> {

    Optional<DichVuAnUong> findByMaDichVu(String maDichVu);

    List<DichVuAnUong> findByTrangThai(Boolean trangThai);

    Page<DichVuAnUong> findByTrangThai(Boolean trangThai, Pageable pageable);

    boolean existsByMaDichVu(String maDichVu);

    @Query("""
            SELECT d FROM DichVuAnUong d
            WHERE (:keyword IS NULL
                   OR LOWER(d.tenDichVu) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(d.maDichVu) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:loai IS NULL OR d.loaiDichVu = :loai)
              AND (:trangThai IS NULL OR d.trangThai = :trangThai)
              AND (:maCaAn IS NULL OR EXISTS (
                    SELECT l FROM DichVuCaAn l
                    WHERE l.dichVuAnUong.maDichVu = d.maDichVu
                      AND l.caAn.maCaAn = :maCaAn
                      AND l.trangThai = true))
            """)
    Page<DichVuAnUong> searchDichVu(@Param("keyword") String keyword,
                                    @Param("loai") String loai,
                                    @Param("maCaAn") String maCaAn,
                                    @Param("trangThai") Boolean trangThai,
                                    Pageable pageable);

    @Query("SELECT COUNT(c) FROM ChiTietDon c WHERE c.dichVuAnUong.maDichVu = :maDichVu")
    long countChiTietDonByMaDichVu(@Param("maDichVu") String maDichVu);

    @Query("SELECT COUNT(t) FROM ThanhPhanDichVu t WHERE t.dichVuAnUong.maDichVu = :maDichVu")
    long countThanhPhanDichVuByMaDichVu(@Param("maDichVu") String maDichVu);

    @Query("SELECT COUNT(b) FROM DatBan b WHERE b.dichVuCaAn.dichVuAnUong.maDichVu = :maDichVu")
    long countDatBanByMaDichVu(@Param("maDichVu") String maDichVu);
}

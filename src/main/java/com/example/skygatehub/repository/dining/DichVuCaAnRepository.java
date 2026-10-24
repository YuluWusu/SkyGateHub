package com.example.skygatehub.repository.dining;

import com.example.skygatehub.entity.dining.DichVuCaAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DichVuCaAnRepository extends JpaRepository<DichVuCaAn, Long> {

    Optional<DichVuCaAn> findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn(String maDichVu, String maCaAn);

    List<DichVuCaAn> findByDichVuAnUong_MaDichVu(String maDichVu);

    List<DichVuCaAn> findByCaAn_MaCaAn(String maCaAn);

    @Query("""
            SELECT l FROM DichVuCaAn l
            JOIN FETCH l.caAn c
            JOIN FETCH l.dichVuAnUong d
            WHERE d.maDichVu IN :maDichVuList AND l.trangThai = true
            ORDER BY c.gioBatDau, c.maCaAn
            """)
    List<DichVuCaAn> findByDichVuAnUong_MaDichVuInAndTrangThaiTrue(
            @Param("maDichVuList") Collection<String> maDichVuList);
}

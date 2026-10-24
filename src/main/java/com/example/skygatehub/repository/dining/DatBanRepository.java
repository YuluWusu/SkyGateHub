package com.example.skygatehub.repository.dining;

import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.entity.dining.DatBan;
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
public interface DatBanRepository extends JpaRepository<DatBan, Long> {

    Optional<DatBan> findByMaDatBan(String maDatBan);

    List<DatBan> findByDonDat_MaDonDat(String maDonDat);

    List<DatBan> findByNgayAnAndDichVuCaAn_CaAn_MaCaAn(LocalDate ngayAn, String maCaAn);

    List<DatBan> findByNgayAn(LocalDate ngayAn);

    List<DatBan> findByNgayAnAndTrangThaiNot(LocalDate ngayAn, TrangThaiDatBan trangThai);

    Page<DatBan> findByTrangThai(TrangThaiDatBan trangThai, Pageable pageable);

    /**
     * Đếm lượt đặt khác (không hủy) đang giữ cùng bàn, cùng ngày, cùng ca ăn.
     */
    @Query("""
        SELECT COUNT(d) FROM DatBan d
        WHERE d.banAn.maBanAn = :maBanAn
          AND d.ngayAn = :ngayAn
          AND d.dichVuCaAn.caAn.maCaAn = :maCaAn
          AND d.maDatBan <> :maDatBan
          AND d.trangThai <> com.example.skygatehub.common.enums.TrangThaiDatBan.DA_HUY
    """)
    long countTrungBan(
            @Param("maBanAn") String maBanAn,
            @Param("ngayAn") LocalDate ngayAn,
            @Param("maCaAn") String maCaAn,
            @Param("maDatBan") String maDatBan
    );

    /**
     * Tìm các lượt khác trên cùng bàn đang ở một trạng thái (vd DA_CHECK_IN), bỏ qua một mã đặt bàn.
     */
    List<DatBan> findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot(String maBanAn, TrangThaiDatBan trangThai, String maDatBan);
}


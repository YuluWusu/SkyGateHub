package com.example.skygatehub.dining.repository;

import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.dining.entity.DatBan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DatBanRepository extends JpaRepository<DatBan, Long> {

    Optional<DatBan> findByMaDatBan(String maDatBan);

    List<DatBan> findByDonDat_MaDonDat(String maDonDat);

    List<DatBan> findByNgayAnAndDichVuCaAn_CaAn_MaCaAn(LocalDate ngayAn, String maCaAn);

    Page<DatBan> findByTrangThai(TrangThaiDatBan trangThai, Pageable pageable);
}

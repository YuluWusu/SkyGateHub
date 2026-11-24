package com.example.skygatehub.repository.cancellation;

import com.example.skygatehub.entity.cancellation.HuyDon;
import com.example.skygatehub.common.enums.TrangThaiHuyDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HuyDonRepository extends JpaRepository<HuyDon, Long> {

    Optional<HuyDon> findByMaHuy(String maHuy);

    List<HuyDon> findByMaNhomHuy(String maNhomHuy);

    List<HuyDon> findByChiTietDon_DonDat_MaDonDat(String maDonDat);

    List<HuyDon> findByChiTietDon_MaChiTietOrderByThoiGianYeuCauDesc(String maChiTiet);

    boolean existsByChiTietDon_MaChiTietAndTrangThaiXuLyIn(String maChiTiet, List<TrangThaiHuyDon> statuses);

    Page<HuyDon> findByTrangThaiXuLy(TrangThaiHuyDon trangThaiXuLy, Pageable pageable);

    Page<HuyDon> findAllByOrderByThoiGianYeuCauDesc(Pageable pageable);

    Page<HuyDon> findByTrangThaiXuLyOrderByThoiGianYeuCauDesc(TrangThaiHuyDon trangThaiXuLy, Pageable pageable);

    long countByTrangThaiXuLy(TrangThaiHuyDon trangThaiXuLy);
}


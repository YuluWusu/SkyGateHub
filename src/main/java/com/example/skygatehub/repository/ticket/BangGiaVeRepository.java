package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.entity.ticket.BangGiaVe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BangGiaVeRepository extends JpaRepository<BangGiaVe, Long> {

    Optional<BangGiaVe> findByMaBangGia(String maBangGia);

    List<BangGiaVe> findByLoaiVe_MaLoaiVeAndTrangThaiOrderByUuTienDesc(String maLoaiVe, Boolean trangThai);

    @Query("SELECT b FROM BangGiaVe b WHERE b.loaiVe.maLoaiVe = :maLoaiVe " +
           "AND b.trangThai = true " +
           "AND b.hieuLucTu <= :ngay " +
           "AND (b.hieuLucDen IS NULL OR b.hieuLucDen >= :ngay) " +
           "ORDER BY b.uuTien DESC")
    List<BangGiaVe> findValidPrices(@Param("maLoaiVe") String maLoaiVe, @Param("ngay") LocalDate ngay);
}


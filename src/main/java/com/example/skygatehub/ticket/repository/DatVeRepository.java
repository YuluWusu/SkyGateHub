package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.ticket.entity.DatVe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DatVeRepository extends JpaRepository<DatVe, Integer> {

    List<DatVe> findByDonDat_MaDonDat(Integer maDonDat);

    @Query("select coalesce(sum(v.thanhTien), 0) from DatVe v where v.donDat.maDonDat = :id")
    BigDecimal sumThanhTienByDonDat(@Param("id") Integer id);

    boolean existsByDonDat_MaDonDatAndNgaySuDung(Integer maDonDat, LocalDate ngaySuDung);
}

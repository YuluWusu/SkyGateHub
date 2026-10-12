package com.example.skygatehub.dining.repository;

import com.example.skygatehub.dining.entity.DatAn;
import com.example.skygatehub.dining.enums.CaAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DatAnRepository extends JpaRepository<DatAn, Integer> {

    List<DatAn> findByDonDat_MaDonDat(Integer maDonDat);

    List<DatAn> findByNgayDungBuaAndCaAnOrderByMaDatAnAsc(LocalDate ngayDungBua, CaAn caAn);

    boolean existsByBanAn_MaBanAndNgayDungBuaAndCaAnAndMaDatAnNot(
            Integer maBan, LocalDate ngayDungBua, CaAn caAn, Integer maDatAnHienTai
    );

    boolean existsByDonDat_MaDonDatAndNgayDungBua(Integer maDonDat, LocalDate ngayDungBua);

    @Query("select coalesce(sum(da.thanhTien), 0) from DatAn da where da.donDat.maDonDat = :maDonDat")
    BigDecimal sumThanhTienByDonDat(@Param("maDonDat") Integer maDonDat);
}

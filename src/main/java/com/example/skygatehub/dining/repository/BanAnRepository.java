package com.example.skygatehub.dining.repository;

import com.example.skygatehub.dining.entity.BanAn;
import com.example.skygatehub.dining.enums.CaAn;
import com.example.skygatehub.dining.enums.TrangThaiBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BanAnRepository extends JpaRepository<BanAn, Integer> {

    boolean existsBySoBan(String soBan);

    @Query("""
        select b from BanAn b
        where b.sucChua >= :soSuat
          and b.trangThai <> :baoTri
          and not exists (
            select da from DatAn da
            where da.banAn = b
              and da.ngayDungBua = :ngay
              and da.caAn = :ca
          )
    """)
    List<BanAn> findFreeTables(
            @Param("ngay") LocalDate ngay,
            @Param("ca") CaAn ca,
            @Param("soSuat") Integer soSuat,
            @Param("baoTri") TrangThaiBan baoTri
    );
}

package com.example.skygatehub.repository.dining;

import com.example.skygatehub.entity.dining.CaAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaAnRepository extends JpaRepository<CaAn, Long> {

    Optional<CaAn> findByMaCaAn(String maCaAn);

    List<CaAn> findByTrangThai(Boolean trangThai);

    boolean existsByMaCaAn(String maCaAn);

    @Query("SELECT COUNT(l) FROM DichVuCaAn l WHERE l.caAn.maCaAn = :maCaAn")
    long countLinksByMaCaAn(@Param("maCaAn") String maCaAn);

    @Query("SELECT l.caAn.maCaAn, COUNT(l) FROM DichVuCaAn l " +
            "WHERE l.trangThai = true AND l.dichVuAnUong.trangThai = true " +
            "GROUP BY l.caAn.maCaAn")
    List<Object[]> countActiveDishesGroupByCa();
}


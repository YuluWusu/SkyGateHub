package com.example.skygatehub.repository.dining;

import com.example.skygatehub.entity.dining.DichVuCaAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DichVuCaAnRepository extends JpaRepository<DichVuCaAn, Long> {

    Optional<DichVuCaAn> findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn(String maDichVu, String maCaAn);

    List<DichVuCaAn> findByDichVuAnUong_MaDichVu(String maDichVu);

    List<DichVuCaAn> findByCaAn_MaCaAn(String maCaAn);
}


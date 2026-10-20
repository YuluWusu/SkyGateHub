package com.example.skygatehub.repository.dining;

import com.example.skygatehub.entity.dining.CaAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaAnRepository extends JpaRepository<CaAn, Long> {

    Optional<CaAn> findByMaCaAn(String maCaAn);

    List<CaAn> findByTrangThai(Boolean trangThai);
}


package com.example.skygatehub.dining.repository;

import com.example.skygatehub.dining.entity.CaAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaAnRepository extends JpaRepository<CaAn, Long> {

    Optional<CaAn> findByMaCaAn(String maCaAn);

    List<CaAn> findByTrangThai(Boolean trangThai);
}

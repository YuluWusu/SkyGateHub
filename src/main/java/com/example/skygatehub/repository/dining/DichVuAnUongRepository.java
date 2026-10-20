package com.example.skygatehub.repository.dining;

import com.example.skygatehub.entity.dining.DichVuAnUong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DichVuAnUongRepository extends JpaRepository<DichVuAnUong, Long> {

    Optional<DichVuAnUong> findByMaDichVu(String maDichVu);

    List<DichVuAnUong> findByTrangThai(Boolean trangThai);

    Page<DichVuAnUong> findByTrangThai(Boolean trangThai, Pageable pageable);

    boolean existsByMaDichVu(String maDichVu);
}


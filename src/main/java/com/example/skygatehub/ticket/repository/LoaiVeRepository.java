package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.ticket.entity.LoaiVe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoaiVeRepository extends JpaRepository<LoaiVe, Long> {

    Optional<LoaiVe> findByMaLoaiVe(String maLoaiVe);

    List<LoaiVe> findByTrangThai(Boolean trangThai);

    Page<LoaiVe> findByTrangThai(Boolean trangThai, Pageable pageable);

    boolean existsByMaLoaiVe(String maLoaiVe);
}

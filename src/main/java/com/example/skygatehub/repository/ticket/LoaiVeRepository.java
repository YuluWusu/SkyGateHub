package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.entity.ticket.LoaiVe;
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

    List<LoaiVe> findByLoaiVe(LoaiVeType loaiVe);

    List<LoaiVe> findByLoaiVeAndTrangThai(LoaiVeType loaiVe, Boolean trangThai);

    Page<LoaiVe> findByTenLoaiVeContainingIgnoreCase(String keyword, Pageable pageable);

    List<LoaiVe> findByTenLoaiVeContainingIgnoreCase(String keyword);

    boolean existsByMaLoaiVe(String maLoaiVe);
}

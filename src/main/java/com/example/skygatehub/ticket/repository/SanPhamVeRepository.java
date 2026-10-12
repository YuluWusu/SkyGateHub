package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.ticket.entity.SanPhamVe;
import com.example.skygatehub.ticket.enums.TrangThaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamVeRepository extends JpaRepository<SanPhamVe, Integer> {

    List<SanPhamVe> findByTrangThai(TrangThaiSanPham tt);

    List<SanPhamVe> findByLaComboAndTrangThai(Boolean laCombo, TrangThaiSanPham tt);

    Optional<SanPhamVe> findByMaCode(String maCode);

    boolean existsByMaCode(String maCode);
}

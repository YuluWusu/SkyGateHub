package com.example.skygatehub.account.repository;

import com.example.skygatehub.account.entity.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    Optional<NguoiDung> findByMaNguoiDung(String maNguoiDung);

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);

    Optional<NguoiDung> findByEmail(String email);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsByEmail(String email);

    Page<NguoiDung> findByTrangThai(Boolean trangThai, Pageable pageable);
}

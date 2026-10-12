package com.example.skygatehub.account.repository;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.common.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    @EntityGraph(attributePaths = "vaiTros")
    @Query("select u from NguoiDung u where u.tenDangNhap = :tenDangNhap")
    Optional<NguoiDung> findWithVaiTrosByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsBySoDienThoai(String soDienThoai);

    Page<NguoiDung> findDistinctByVaiTros_TenVaiTro(RoleName role, Pageable pageable);
}

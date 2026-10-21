package com.example.skygatehub.repository.account;

import com.example.skygatehub.entity.account.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT u FROM NguoiDung u WHERE " +
           "LOWER(u.tenDangNhap) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "LOWER(u.hoTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           "u.soDienThoai LIKE CONCAT('%', :tuKhoa, '%')")
    Page<NguoiDung> timKiemNguoiDung(@Param("tuKhoa") String tuKhoa, Pageable phanTrang);

    default Page<NguoiDung> searchUsers(String keyword, Pageable pageable) {
        return timKiemNguoiDung(keyword, pageable);
    }
}

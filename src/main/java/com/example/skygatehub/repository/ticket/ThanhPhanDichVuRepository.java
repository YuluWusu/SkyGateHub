package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.common.enums.LoaiThanhPhan;
import com.example.skygatehub.entity.ticket.ThanhPhanDichVu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThanhPhanDichVuRepository extends JpaRepository<ThanhPhanDichVu, Long> {

    Optional<ThanhPhanDichVu> findByMaThanhPhan(String maThanhPhan);

    List<ThanhPhanDichVu> findByLoaiThanhPhanAndTrangThai(LoaiThanhPhan loaiThanhPhan, Boolean trangThai);

    List<ThanhPhanDichVu> findByTrangThai(Boolean trangThai);

    Page<ThanhPhanDichVu> findByTenThanhPhanContainingIgnoreCase(String keyword, Pageable pageable);
}

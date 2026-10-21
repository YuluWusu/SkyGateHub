package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.entity.ticket.NgayDacBiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NgayDacBietRepository extends JpaRepository<NgayDacBiet, Long> {

    Optional<NgayDacBiet> findByMaNgayDacBiet(String maNgayDacBiet);

    Optional<NgayDacBiet> findByNgayApDungAndTrangThai(LocalDate ngayApDung, Boolean trangThai);

    boolean existsByNgayApDungAndTrangThai(LocalDate ngayApDung, Boolean trangThai);

    List<NgayDacBiet> findByTrangThaiOrderByNgayApDungAsc(Boolean trangThai);

    Page<NgayDacBiet> findByTenNgayContainingIgnoreCase(String keyword, Pageable pageable);
}

package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.ticket.entity.NgayDacBiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface NgayDacBietRepository extends JpaRepository<NgayDacBiet, Long> {

    Optional<NgayDacBiet> findByMaNgayDacBiet(String maNgayDacBiet);

    Optional<NgayDacBiet> findByNgayApDungAndTrangThai(LocalDate ngayApDung, Boolean trangThai);

    boolean existsByNgayApDungAndTrangThai(LocalDate ngayApDung, Boolean trangThai);
}

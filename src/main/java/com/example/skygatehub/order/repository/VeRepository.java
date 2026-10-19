package com.example.skygatehub.order.repository;

import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.order.entity.Ve;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeRepository extends JpaRepository<Ve, Long> {

    Optional<Ve> findByMaVe(String maVe);

    Optional<Ve> findByMaVeChuoi(String maVeChuoi);

    /**
     * Khóa bi quan chống race condition khi quét mã QR vé đồng thời ở nhiều cổng check-in.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Ve v WHERE v.maVeChuoi = :maVeChuoi")
    Optional<Ve> findByMaVeChuoiWithLock(@Param("maVeChuoi") String maVeChuoi);

    List<Ve> findByChiTietDon_DonDat_MaDonDat(String maDonDat);

    List<Ve> findByNgayThamQuanAndTrangThai(LocalDate ngayThamQuan, TrangThaiVe trangThai);

    Page<Ve> findByTrangThai(TrangThaiVe trangThai, Pageable pageable);
}

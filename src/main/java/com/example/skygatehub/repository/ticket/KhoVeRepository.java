package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.entity.ticket.KhoVe;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface KhoVeRepository extends JpaRepository<KhoVe, Long> {

    Optional<KhoVe> findByMaKhoVe(String maKhoVe);

    Optional<KhoVe> findByLoaiVe_MaLoaiVeAndNgayThamQuan(String maLoaiVe, LocalDate ngayThamQuan);

    /**
     * Khóa bi quan (SELECT ... FOR UPDATE) chống bán vượt quá số lượng tối đa trong kho vé.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM KhoVe k WHERE k.loaiVe.maLoaiVe = :maLoaiVe AND k.ngayThamQuan = :ngayThamQuan")
    Optional<KhoVe> findByLoaiVeAndNgayWithLock(@Param("maLoaiVe") String maLoaiVe, @Param("ngayThamQuan") LocalDate ngayThamQuan);
}


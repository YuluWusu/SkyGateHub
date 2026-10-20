package com.example.skygatehub.repository.room;

import com.example.skygatehub.entity.room.LoaiPhong;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Long> {

    Optional<LoaiPhong> findByMaLoaiPhong(String maLoaiPhong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lp FROM LoaiPhong lp WHERE lp.maLoaiPhong = :maLoaiPhong")
    Optional<LoaiPhong> findByMaLoaiPhongForUpdate(@Param("maLoaiPhong") String maLoaiPhong);

    boolean existsByMaLoaiPhong(String maLoaiPhong);

    boolean existsByTenLoaiPhong(String tenLoaiPhong);

    List<LoaiPhong> findByTrangThai(Boolean trangThai);

    Page<LoaiPhong> findByTrangThai(Boolean trangThai, Pageable pageable);
}


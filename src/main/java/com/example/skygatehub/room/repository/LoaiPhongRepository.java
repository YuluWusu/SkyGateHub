package com.example.skygatehub.room.repository;

import com.example.skygatehub.room.entity.LoaiPhong;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lp from LoaiPhong lp where lp.maLoaiPhong = :id")
    Optional<LoaiPhong> findByIdForUpdate(@Param("id") Integer id);

    boolean existsByTenLoaiPhong(String tenLoaiPhong);
}

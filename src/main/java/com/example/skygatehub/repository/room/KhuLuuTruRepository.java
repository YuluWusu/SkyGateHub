package com.example.skygatehub.repository.room;

import com.example.skygatehub.entity.room.KhuLuuTru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhuLuuTruRepository extends JpaRepository<KhuLuuTru, Integer> {

    Optional<KhuLuuTru> findByTenKhu(String tenKhu);

    boolean existsByTenKhu(String tenKhu);

    boolean existsByTenKhuAndMaKhuNot(String tenKhu, Integer maKhu);

    @Query("SELECT COUNT(lp) FROM LoaiPhong lp WHERE lp.khuLuuTru.maKhu = :maKhu")
    long countLoaiPhongByMaKhu(@Param("maKhu") Integer maKhu);

    List<KhuLuuTru> findAllByOrderByMaKhuAsc();
}

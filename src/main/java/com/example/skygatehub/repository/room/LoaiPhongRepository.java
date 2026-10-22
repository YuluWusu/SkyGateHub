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

    boolean existsByTenLoaiPhongAndMaLoaiPhongNot(String tenLoaiPhong, String maLoaiPhong);

    List<LoaiPhong> findByTrangThai(Boolean trangThai);

    Page<LoaiPhong> findByTrangThai(Boolean trangThai, Pageable pageable);

    List<LoaiPhong> findByKhuLuuTru_MaKhu(Integer maKhu);

    List<LoaiPhong> findByKhuLuuTru_MaKhuAndTrangThai(Integer maKhu, Boolean trangThai);

    long countByKhuLuuTru_MaKhu(Integer maKhu);

    @Query("SELECT COUNT(p) FROM Phong p WHERE p.loaiPhong.maLoaiPhong = :maLoaiPhong")
    long countPhongByMaLoaiPhong(@Param("maLoaiPhong") String maLoaiPhong);

    @Query("SELECT COUNT(dp) FROM DatPhong dp WHERE dp.loaiPhong.maLoaiPhong = :maLoaiPhong")
    long countDatPhongByMaLoaiPhong(@Param("maLoaiPhong") String maLoaiPhong);

    @Query(
        value = """
            SELECT lp FROM LoaiPhong lp
            LEFT JOIN FETCH lp.khuLuuTru klt
            WHERE (:maKhu IS NULL OR klt.maKhu = :maKhu)
              AND (:trangThai IS NULL OR lp.trangThai = :trangThai)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(lp.tenLoaiPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(lp.maLoaiPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(klt.tenKhu) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """,
        countQuery = """
            SELECT COUNT(lp) FROM LoaiPhong lp
            LEFT JOIN lp.khuLuuTru klt
            WHERE (:maKhu IS NULL OR klt.maKhu = :maKhu)
              AND (:trangThai IS NULL OR lp.trangThai = :trangThai)
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(lp.tenLoaiPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(lp.maLoaiPhong) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(klt.tenKhu) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """
    )
    Page<LoaiPhong> searchLoaiPhong(
            @Param("maKhu") Integer maKhu,
            @Param("trangThai") Boolean trangThai,
            @Param("keyword") String keyword,
            Pageable pageable);

    List<LoaiPhong> findAllByOrderByMaLoaiPhongAsc();
}

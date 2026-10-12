package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.ticket.entity.ComboChiTiet;
import com.example.skygatehub.ticket.entity.ComboChiTietId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboChiTietRepository extends JpaRepository<ComboChiTiet, ComboChiTietId> {

    List<ComboChiTiet> findByCombo_MaSanPham(Integer maCombo);

    @Modifying
    @Query("delete from ComboChiTiet c where c.combo.maSanPham = :maCombo")
    void deleteByCombo_MaSanPham(@Param("maCombo") Integer maCombo);

    boolean existsBySanPhamCon_MaSanPham(Integer maSanPham);
}

package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.ticket.entity.ComboThanhPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboThanhPhanRepository extends JpaRepository<ComboThanhPhan, Long> {

    List<ComboThanhPhan> findByLoaiVe_MaLoaiVe(String maLoaiVe);

    List<ComboThanhPhan> findByThanhPhanDichVu_MaThanhPhan(String maThanhPhan);
}

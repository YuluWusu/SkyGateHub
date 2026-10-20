package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboThanhPhanRepository extends JpaRepository<ComboThanhPhan, Long> {

    List<ComboThanhPhan> findByLoaiVe_MaLoaiVe(String maLoaiVe);

    List<ComboThanhPhan> findByThanhPhanDichVu_MaThanhPhan(String maThanhPhan);
}


package com.example.skygatehub.repository.ticket;

import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComboThanhPhanRepository extends JpaRepository<ComboThanhPhan, Long> {

    List<ComboThanhPhan> findByLoaiVe_MaLoaiVe(String maLoaiVe);

    List<ComboThanhPhan> findByThanhPhanDichVu_MaThanhPhan(String maThanhPhan);

    Optional<ComboThanhPhan> findByLoaiVe_MaLoaiVeAndThanhPhanDichVu_MaThanhPhan(String maLoaiVe, String maThanhPhan);

    void deleteByLoaiVe_MaLoaiVe(String maLoaiVe);
}

package com.example.skygatehub.dining.repository;

import com.example.skygatehub.dining.entity.DichVuAn;
import com.example.skygatehub.dining.enums.LoaiDichVuAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DichVuAnRepository extends JpaRepository<DichVuAn, Integer> {

    List<DichVuAn> findByLoaiDichVu(LoaiDichVuAn loai);
}

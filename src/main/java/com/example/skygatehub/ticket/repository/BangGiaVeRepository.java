package com.example.skygatehub.ticket.repository;

import com.example.skygatehub.common.enums.LoaiKhach;
import com.example.skygatehub.common.enums.LoaiNgay;
import com.example.skygatehub.ticket.entity.BangGiaVe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BangGiaVeRepository extends JpaRepository<BangGiaVe, Integer> {

    Optional<BangGiaVe> findBySanPham_MaSanPhamAndLoaiKhachAndLoaiNgay(Integer maSanPham, LoaiKhach loaiKhach, LoaiNgay loaiNgay);

    List<BangGiaVe> findBySanPham_MaSanPham(Integer maSanPham);
}

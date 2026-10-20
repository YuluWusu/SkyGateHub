package com.example.skygatehub.repository.order;

import com.example.skygatehub.entity.order.VDonDatTongHop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VDonDatTongHopRepository extends JpaRepository<VDonDatTongHop, String> {

    Optional<VDonDatTongHop> findByMaDonDat(String maDonDat);

    Optional<VDonDatTongHop> findByMaDonHang(String maDonHang);

    Page<VDonDatTongHop> findByTrangThaiThanhToanTinh(String trangThaiThanhToanTinh, Pageable pageable);
}


package com.example.skygatehub.service.room;

import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.dto.room.PhongRequest;
import com.example.skygatehub.entity.room.Phong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PhongService {

    List<Phong> getAllPhong();

    Phong getPhongById(Long id);

    Phong getPhongByMa(String maPhong);

    Page<Phong> searchPhong(Integer maKhu, String maLoaiPhong, TrangThaiPhong trangThai, String keyword, Pageable pageable);

    Phong createPhong(PhongRequest request);

    Phong updatePhong(Long id, PhongRequest request);

    void deletePhong(Long id);

    void toggleMaintenance(Long id, String maNhanVien);

    long countTotalRooms();

    long countRoomsByStatus(TrangThaiPhong status);
}

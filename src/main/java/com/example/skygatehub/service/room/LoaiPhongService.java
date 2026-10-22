package com.example.skygatehub.service.room;

import com.example.skygatehub.dto.room.KhuLuuTruDto;
import com.example.skygatehub.dto.room.LoaiPhongRequest;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoaiPhongService {

    // === Khu Lưu Trú ===
    List<KhuLuuTru> getAllKhuLuuTru();

    KhuLuuTru getKhuLuuTruById(Integer maKhu);

    KhuLuuTru createKhuLuuTru(KhuLuuTruDto dto);

    KhuLuuTru updateKhuLuuTru(Integer maKhu, KhuLuuTruDto dto);

    void deleteKhuLuuTru(Integer maKhu);

    // === Loại Phòng ===
    List<LoaiPhong> getAllLoaiPhong();

    List<LoaiPhong> getActiveLoaiPhong();

    LoaiPhong getLoaiPhongById(Long id);

    LoaiPhong getLoaiPhongByMa(String maLoaiPhong);

    Page<LoaiPhong> searchLoaiPhong(Integer maKhu, Boolean trangThai, String keyword, Pageable pageable);

    LoaiPhong createLoaiPhong(LoaiPhongRequest request);

    LoaiPhong updateLoaiPhong(Long id, LoaiPhongRequest request);

    void toggleLoaiPhongStatus(Long id);

    void deleteLoaiPhong(Long id);
}

package com.example.skygatehub.service.dining;

import com.example.skygatehub.dto.dining.DichVuAnRequest;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DichVuAnService {

    Page<DichVuAnUong> search(String keyword, String loai, String maCaAn, Boolean trangThai, Pageable pageable);

    DichVuAnUong getById(Long id);

    DichVuAnUong create(DichVuAnRequest request);

    DichVuAnUong update(Long id, DichVuAnRequest request);

    void toggleTrangThai(Long id);

    void delete(Long id);

    /** Mã các ca đang phục vụ dịch vụ (link đang bật). */
    List<String> getMaCaAnDangPhucVu(String maDichVu);

    /** maDichVu -> tên các ca đang bật. */
    Map<String, List<String>> getTenCaAnTheoDichVu(Collection<String> maDichVuList);
}

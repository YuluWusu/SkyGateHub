package com.example.skygatehub.service.dining;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.dto.dining.BanAnRequest;
import com.example.skygatehub.entity.dining.BanAn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BanAnService {

    Page<BanAn> search(String keyword, String khuVuc, TrangThaiBanAn trangThai, Pageable pageable);

    BanAn getById(Long id);

    BanAn create(BanAnRequest request);

    BanAn update(Long id, BanAnRequest request);

    void toggleMaintenance(Long id, String maNhanVien);

    void releaseTable(Long id, String maNhanVien);

    void delete(Long id);
}

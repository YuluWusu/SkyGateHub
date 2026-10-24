package com.example.skygatehub.service.dining;

import com.example.skygatehub.dto.dining.CaAnRequest;
import com.example.skygatehub.entity.dining.CaAn;

import java.util.List;
import java.util.Map;

public interface CaAnService {

    List<CaAn> getAll();

    CaAn getById(Long id);

    CaAn create(CaAnRequest request);

    CaAn update(Long id, CaAnRequest request);

    void toggleTrangThai(Long id);

    void delete(Long id);

    /** Số món đang phục vụ theo mã ca. */
    Map<String, Long> countActiveDishesByCa();
}

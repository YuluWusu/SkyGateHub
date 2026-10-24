package com.example.skygatehub.service.dining;

import com.example.skygatehub.dto.dining.DichVuAnRequest;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.dining.DichVuCaAn;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.dining.DichVuCaAnRepository;
import com.example.skygatehub.service.dining.impl.DichVuAnServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DichVuAnServiceImplTest {

    @Mock
    private DichVuAnUongRepository dichVuAnUongRepository;
    @Mock
    private DichVuCaAnRepository dichVuCaAnRepository;
    @Mock
    private CaAnRepository caAnRepository;

    @InjectMocks
    private DichVuAnServiceImpl service;

    private DichVuAnRequest request(String ma, String loai) {
        DichVuAnRequest r = new DichVuAnRequest();
        r.setMaDichVu(ma);
        r.setTenDichVu("Buffet sáng");
        r.setLoaiDichVu(loai);
        return r;
    }

    private CaAn ca(String ma) {
        CaAn c = new CaAn();
        c.setMaCaAn(ma);
        c.setTenCaAn("Ca " + ma);
        return c;
    }

    @Test
    void createTrungMaThrowsIllegalArgument() {
        when(dichVuAnUongRepository.existsByMaDichVu("DV01")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(request(" dv01 ", "BUFFET")));
        verify(dichVuAnUongRepository, never()).save(any());
    }

    @Test
    void createLoaiKhongHopLeThrowsIllegalArgument() {
        when(dichVuAnUongRepository.existsByMaDichVu("DV01")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(request("DV01", "KHONG_CO")));
        verify(dichVuAnUongRepository, never()).save(any());
    }

    @Test
    void updateBoChonMotCaThiTatLinkVaKhongXoa() {
        DichVuAnUong dv = new DichVuAnUong();
        dv.setId(1L);
        dv.setMaDichVu("DV01");

        DichVuCaAn giu = new DichVuCaAn();
        giu.setDichVuAnUong(dv);
        giu.setCaAn(ca("SANG"));
        giu.setTrangThai(true);
        DichVuCaAn bo = new DichVuCaAn();
        bo.setDichVuAnUong(dv);
        bo.setCaAn(ca("TOI"));
        bo.setTrangThai(true);

        when(dichVuAnUongRepository.findById(1L)).thenReturn(Optional.of(dv));
        when(dichVuAnUongRepository.save(dv)).thenReturn(dv);
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVu("DV01")).thenReturn(List.of(giu, bo));
        when(caAnRepository.findByMaCaAn("SANG")).thenReturn(Optional.of(giu.getCaAn()));

        DichVuAnRequest r = request("DV01", "BUFFET");
        r.setMaCaAnList(new ArrayList<>(List.of("SANG")));
        service.update(1L, r);

        assertTrue(giu.getTrangThai());
        assertFalse(bo.getTrangThai());
        verify(dichVuCaAnRepository, never()).delete(any());
        verify(dichVuCaAnRepository, never()).deleteAll(any());
    }

    @Test
    void deleteConThamChieuThrowsIllegalState() {
        DichVuAnUong dv = new DichVuAnUong();
        dv.setId(1L);
        dv.setMaDichVu("DV01");
        dv.setTenDichVu("Buffet sáng");

        when(dichVuAnUongRepository.findById(1L)).thenReturn(Optional.of(dv));
        when(dichVuAnUongRepository.countChiTietDonByMaDichVu("DV01")).thenReturn(2L);
        when(dichVuAnUongRepository.countThanhPhanDichVuByMaDichVu("DV01")).thenReturn(0L);
        when(dichVuAnUongRepository.countDatBanByMaDichVu("DV01")).thenReturn(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.delete(1L));
        assertTrue(ex.getMessage().contains("3"));
        verify(dichVuAnUongRepository, never()).delete(any());
        verify(dichVuCaAnRepository, never()).deleteAll(any());
    }
}

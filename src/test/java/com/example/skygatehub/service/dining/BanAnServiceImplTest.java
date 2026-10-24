package com.example.skygatehub.service.dining;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.dto.dining.BanAnRequest;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.repository.dining.BanAnRepository;
import com.example.skygatehub.service.dining.impl.BanAnServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BanAnServiceImplTest {

    @Mock
    private BanAnRepository banAnRepository;

    @InjectMocks
    private BanAnServiceImpl service;

    private BanAn ban(TrangThaiBanAn trangThai) {
        BanAn b = new BanAn();
        b.setId(1L);
        b.setMaBanAn("BAN01");
        b.setSoBan("B01");
        b.setKhuVuc("Sảnh chính");
        b.setSucChua(4);
        b.setTrangThai(trangThai);
        return b;
    }

    @Test
    void createTrungSoBan_nemIllegalArgument() {
        BanAnRequest req = new BanAnRequest();
        req.setMaBanAn(" ban02 ");
        req.setSoBan("B01");
        req.setKhuVuc("Sảnh chính");
        req.setSucChua(4);

        when(banAnRepository.existsByMaBanAn("BAN02")).thenReturn(false);
        when(banAnRepository.existsBySoBan("B01")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
        verify(banAnRepository, never()).save(any());
    }

    @Test
    void toggleMaintenanceBanDangSuDung_nemIllegalState() {
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(ban(TrangThaiBanAn.DANG_SU_DUNG)));

        assertThrows(IllegalStateException.class, () -> service.toggleMaintenance(1L, "NV01"));
        verify(banAnRepository, never()).save(any());
    }

    @Test
    void toggleMaintenanceBanConDatBanHieuLuc_nemIllegalState() {
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(ban(TrangThaiBanAn.SAN_SANG)));
        when(banAnRepository.countActiveDatBanFrom(anyString(), any(LocalDate.class), any(Collection.class)))
                .thenReturn(2L);

        assertThrows(IllegalStateException.class, () -> service.toggleMaintenance(1L, "NV01"));
        verify(banAnRepository, never()).save(any());
    }

    @Test
    void toggleMaintenanceBanBaoTri_chuyenVeSanSang() {
        BanAn b = ban(TrangThaiBanAn.BAO_TRI);
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(b));

        service.toggleMaintenance(1L, "NV01");

        assertEquals(TrangThaiBanAn.SAN_SANG, b.getTrangThai());
        verify(banAnRepository).save(b);
    }

    @Test
    void releaseTableBanKhongDangSuDung_nemIllegalState() {
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(ban(TrangThaiBanAn.SAN_SANG)));

        assertThrows(IllegalStateException.class, () -> service.releaseTable(1L, "NV01"));
        verify(banAnRepository, never()).save(any());
    }

    @Test
    void releaseTableConKhachCheckIn_nemIllegalState() {
        BanAn b = ban(TrangThaiBanAn.DANG_SU_DUNG);
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(b));
        when(banAnRepository.findMaDatBanByBanAnAndTrangThai("BAN01", TrangThaiDatBan.DA_CHECK_IN))
                .thenReturn(List.of("DB01"));

        assertThrows(IllegalStateException.class, () -> service.releaseTable(1L, "NV01"));
        assertEquals(TrangThaiBanAn.DANG_SU_DUNG, b.getTrangThai());
        verify(banAnRepository, never()).save(any());
    }

    @Test
    void releaseTableHopLe_chuyenVeSanSang() {
        BanAn b = ban(TrangThaiBanAn.DANG_SU_DUNG);
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(b));
        when(banAnRepository.findMaDatBanByBanAnAndTrangThai("BAN01", TrangThaiDatBan.DA_CHECK_IN))
                .thenReturn(List.of());

        service.releaseTable(1L, "NV01");

        assertEquals(TrangThaiBanAn.SAN_SANG, b.getTrangThai());
        verify(banAnRepository).save(b);
    }

    @Test
    void deleteBanCoLichSuDat_nemIllegalStateVaKhongXoa() {
        BanAn b = ban(TrangThaiBanAn.SAN_SANG);
        when(banAnRepository.findById(1L)).thenReturn(Optional.of(b));
        when(banAnRepository.countDatBanByMaBanAn("BAN01")).thenReturn(3L);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
        verify(banAnRepository, never()).delete(any(BanAn.class));
    }
}

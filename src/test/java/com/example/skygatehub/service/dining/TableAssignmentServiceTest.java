package com.example.skygatehub.service.dining;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.entity.dining.DichVuCaAn;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.BanAnRepository;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.service.dining.impl.TableAssignmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableAssignmentServiceTest {

    @Mock
    private BanAnRepository banAnRepository;
    @Mock
    private DatBanRepository datBanRepository;
    @Mock
    private CaAnRepository caAnRepository;
    @Mock
    private CheckInRepository checkInRepository;
    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @InjectMocks
    private TableAssignmentServiceImpl service;

    private BanAn ban(String ma, int sucChua, TrangThaiBanAn trangThai) {
        BanAn b = new BanAn();
        b.setMaBanAn(ma);
        b.setSoBan(ma);
        b.setKhuVuc("Sảnh chính");
        b.setSucChua(sucChua);
        b.setTrangThai(trangThai);
        return b;
    }

    private DatBan datBan(String ma, TrangThaiDatBan trangThai, int soKhach, LocalDate ngay, BanAn banAn) {
        CaAn ca = new CaAn();
        ca.setMaCaAn("CA_TRUA");
        DichVuCaAn dv = new DichVuCaAn();
        dv.setCaAn(ca);

        DatBan db = new DatBan();
        db.setMaDatBan(ma);
        db.setTrangThai(trangThai);
        db.setSoKhach(soKhach);
        db.setNgayAn(ngay);
        db.setDichVuCaAn(dv);
        db.setBanAn(banAn);
        return db;
    }

    private void stubBooking(DatBan db) {
        when(datBanRepository.findByMaDatBan(db.getMaDatBan())).thenReturn(Optional.of(db));
    }

    @Test
    void assignLuotDaHuy_nemIllegalState() {
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_HUY, 2, LocalDate.now(), null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01"));
        verify(datBanRepository, never()).save(any());
    }

    @Test
    void assignBanThieuCho_nemIllegalState() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 6, LocalDate.now(), null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01"));
        verify(datBanRepository, never()).save(any());
    }

    @Test
    void assignTrungBanCungNgayCa_nemIllegalState() {
        LocalDate ngay = LocalDate.now();
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, ngay, null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));
        when(datBanRepository.countTrungBan("B1", ngay, "CA_TRUA", "DB1")).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01"));
        verify(datBanRepository, never()).save(any());
    }

    @Test
    void assignHopLe_chuyenSangDaXacNhan() {
        LocalDate ngay = LocalDate.now();
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, ngay, null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(b));
        when(datBanRepository.countTrungBan("B1", ngay, "CA_TRUA", "DB1")).thenReturn(0L);

        service.assignTableToBooking("DB1", "B1", "NV01");

        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        assertEquals(b, db.getBanAn());
        verify(datBanRepository).save(db);
    }

    @Test
    void checkInLuotNgayKhac_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now().plusDays(1), b);
        stubBooking(db);

        assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", "NV01"));
        assertEquals(TrangThaiBanAn.SAN_SANG, b.getTrangThai());
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkInBanCoKhachLuotKhac_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        DatBan dangAn = datBan("DB2", TrangThaiDatBan.DA_CHECK_IN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of(dangAn));

        assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", "NV01"));
        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkInThieuNhanVien_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", " "));
        assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", null));
        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        assertEquals(TrangThaiBanAn.SAN_SANG, b.getTrangThai());
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkInNhanVienKhongTonTai_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of());
        when(nguoiDungRepository.findByMaNguoiDung("NV99")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", "NV99"));
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkInHopLe_banDangSuDung() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of());
        when(nguoiDungRepository.findByMaNguoiDung("NV01")).thenReturn(Optional.of(new NguoiDung()));

        assertEquals("B1", service.checkInTable("DB1", "NV01"));

        assertEquals(TrangThaiDatBan.DA_CHECK_IN, db.getTrangThai());
        assertEquals(TrangThaiBanAn.DANG_SU_DUNG, b.getTrangThai());
        verify(checkInRepository).save(any());
    }

    @Test
    void completeLuotChuaCheckIn_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        stubBooking(db);

        assertThrows(IllegalStateException.class, () -> service.completeTable("DB1", "NV01"));
        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        verify(datBanRepository, never()).save(any());
    }

    @Test
    void completeBanConLuotCheckInKhac_giuDangSuDung() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_CHECK_IN, 2, LocalDate.now(), b);
        DatBan conLai = datBan("DB2", TrangThaiDatBan.DA_CHECK_IN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of(conLai));

        service.completeTable("DB1", "NV01");

        assertEquals(TrangThaiDatBan.DA_HOAN_THANH, db.getTrangThai());
        assertEquals(TrangThaiBanAn.DANG_SU_DUNG, b.getTrangThai());
        verify(banAnRepository, never()).save(any());
    }

    @Test
    void completeBinhThuong_banVeSanSang() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_CHECK_IN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of());

        assertEquals("B1", service.completeTable("DB1", "NV01"));

        assertEquals(TrangThaiDatBan.DA_HOAN_THANH, db.getTrangThai());
        assertEquals(TrangThaiBanAn.SAN_SANG, b.getTrangThai());
        verify(banAnRepository).save(b);
    }
}

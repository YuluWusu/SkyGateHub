package com.example.skygatehub.service.order;

import com.example.skygatehub.common.concurrent.AsyncTaskService;
import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.LoaiThanhPhan;
import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.common.enums.TrangThaiQuyenLoi;
import com.example.skygatehub.common.exception.BusinessException;
import com.example.skygatehub.common.exception.ErrorCode;
import com.example.skygatehub.common.util.DayTypeResolver;
import com.example.skygatehub.dto.order.BookingPipelineForm;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.dining.DichVuCaAn;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.QuyenLoiDon;
import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.ThanhPhanDichVu;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.dining.DichVuCaAnRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.QuyenLoiDonRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.repository.ticket.ComboThanhPhanRepository;
import com.example.skygatehub.repository.ticket.KhoVeRepository;
import com.example.skygatehub.service.dining.DichVuAnService;
import com.example.skygatehub.service.order.impl.CheckoutServiceImpl;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplDiningTest {

    @Mock
    private DonDatRepository donDatRepository;
    @Mock
    private ChiTietDonRepository chiTietDonRepository;
    @Mock
    private VeRepository veRepository;
    @Mock
    private DatPhongRepository datPhongRepository;
    @Mock
    private DatBanRepository datBanRepository;
    @Mock
    private ThanhToanRepository thanhToanRepository;
    @Mock
    private LoaiPhongRepository loaiPhongRepository;
    @Mock
    private PhongRepository phongRepository;
    @Mock
    private DichVuAnUongRepository dichVuAnUongRepository;
    @Mock
    private DichVuCaAnRepository dichVuCaAnRepository;
    @Mock
    private KhoVeRepository khoVeRepository;
    @Mock
    private ComboThanhPhanRepository comboThanhPhanRepository;
    @Mock
    private QuyenLoiDonRepository quyenLoiDonRepository;
    @Mock
    private SanPhamVeService sanPhamVeService;
    @Mock
    private DichVuAnService dichVuAnService;
    @Mock
    private TicketPricingService ticketPricingService;
    @Mock
    private DayTypeResolver dayTypeResolver;
    @Mock
    private AsyncTaskService asyncTaskService;

    @Captor
    private ArgumentCaptor<List<ChiTietDon>> chiTietCaptor;
    @Captor
    private ArgumentCaptor<List<DatBan>> datBanCaptor;
    @Captor
    private ArgumentCaptor<List<QuyenLoiDon>> quyenLoiCaptor;

    @InjectMocks
    private CheckoutServiceImpl service;

    private BookingPipelineForm formAnUong(int soKhach, String... maMon) {
        BookingPipelineForm form = new BookingPipelineForm();
        form.setHoTenKhach("Nguyễn Văn A");
        form.setSdtKhach("0900000000");
        form.setBookDining(true);
        form.setNgayAn(LocalDate.now());
        form.setMaCaAn("CA_TRUA");
        form.setSoKhachAn(soKhach);
        for (String ma : maMon) {
            form.getDiningCode().add(ma);
            form.getDiningQty().add(ma.equals(maMon[0]) ? 1 : 2);
        }
        return form;
    }

    private DichVuAnUong mon(String ma, String ten, boolean trangThai, String gia) {
        DichVuAnUong m = new DichVuAnUong();
        m.setMaDichVu(ma);
        m.setTenDichVu(ten);
        m.setTrangThai(trangThai);
        m.setGiaDichVu(gia == null ? null : new BigDecimal(gia));
        return m;
    }

    private DichVuCaAn link(DichVuAnUong mon, String maCa) {
        CaAn ca = new CaAn();
        ca.setMaCaAn(maCa);
        DichVuCaAn l = new DichVuCaAn();
        l.setDichVuAnUong(mon);
        l.setCaAn(ca);
        return l;
    }

    private void stubMonPhucVuCa(DichVuAnUong mon, String... maCa) {
        when(dichVuAnService.getMaCaAnDatDuoc(List.of(mon.getMaDichVu())))
                .thenReturn(Map.of(mon.getMaDichVu(), List.of(maCa)));
    }

    @Test
    void haiMonCungCa_haiChiTietMotDatBanSoKhachTuForm() {
        DichVuAnUong buffet = mon("MON1", "Buffet trưa", true, "200000");
        DichVuAnUong setMenu = mon("MON2", "Set menu", true, "150000");
        when(dichVuAnUongRepository.findByMaDichVu("MON1")).thenReturn(Optional.of(buffet));
        when(dichVuAnUongRepository.findByMaDichVu("MON2")).thenReturn(Optional.of(setMenu));
        stubMonPhucVuCa(buffet, "CA_TRUA", "CA_TOI");
        stubMonPhucVuCa(setMenu, "CA_TRUA");
        DichVuCaAn linkBuffet = link(buffet, "CA_TRUA");
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON1", "CA_TRUA"))
                .thenReturn(Optional.of(linkBuffet));
        when(dayTypeResolver.getDepositRate()).thenReturn(new BigDecimal("0.30"));
        BookingPipelineForm form = formAnUong(4, "MON1", "MON2");
        form.setKhuVucMongMuon("  Sân hồ  ");

        service.processPipelineBooking(form);

        verify(chiTietDonRepository).saveAllAndFlush(chiTietCaptor.capture());
        List<ChiTietDon> chiTiet = chiTietCaptor.getValue();
        assertEquals(2, chiTiet.size());
        assertTrue(chiTiet.stream().allMatch(c -> c.getLoaiDichVu() == LoaiDichVuDon.AN_UONG));
        assertEquals(LocalDate.now(), chiTiet.get(0).getNgaySuDung());

        verify(datBanRepository).saveAllAndFlush(datBanCaptor.capture());
        List<DatBan> datBan = datBanCaptor.getValue();
        assertEquals(1, datBan.size());
        DatBan db = datBan.get(0);
        assertEquals(4, db.getSoKhach());
        assertEquals(chiTiet.get(0), db.getChiTietDon());
        assertEquals(linkBuffet, db.getDichVuCaAn());
        assertEquals(LocalDate.now(), db.getNgayAn());
        assertEquals(TrangThaiDatBan.CHO_XAC_NHAN, db.getTrangThai());
        assertEquals("Khu vực mong muốn: Sân hồ", db.getGhiChu());
        assertNull(db.getQuyenLoiDon());
        verifyNoInteractions(quyenLoiDonRepository);
    }

    @Test
    void khongCoKhuVuc_ghiChuDatBanNull() {
        DichVuAnUong buffet = mon("MON1", "Buffet trưa", true, "200000");
        when(dichVuAnUongRepository.findByMaDichVu("MON1")).thenReturn(Optional.of(buffet));
        stubMonPhucVuCa(buffet, "CA_TRUA");
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON1", "CA_TRUA"))
                .thenReturn(Optional.of(link(buffet, "CA_TRUA")));
        when(dayTypeResolver.getDepositRate()).thenReturn(new BigDecimal("0.30"));
        BookingPipelineForm form = formAnUong(2, "MON1");
        form.setKhuVucMongMuon("   ");

        service.processPipelineBooking(form);

        verify(datBanRepository).saveAllAndFlush(datBanCaptor.capture());
        assertNull(datBanCaptor.getValue().get(0).getGhiChu());
    }

    @Test
    void monKhongPhucVuCaDaChon_nemBusinessExceptionVaKhongLuuGi() {
        DichVuAnUong buffet = mon("MON1", "Buffet trưa", true, "200000");
        when(dichVuAnUongRepository.findByMaDichVu("MON1")).thenReturn(Optional.of(buffet));
        stubMonPhucVuCa(buffet, "CA_TOI");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.processPipelineBooking(formAnUong(4, "MON1")));

        assertEquals(ErrorCode.BAD_REQUEST, ex.getErrorCode());
        assertEquals("Món [Buffet trưa] không phục vụ trong ca đã chọn", ex.getMessage());
        verifyNoInteractions(chiTietDonRepository, datBanRepository, veRepository);
        verify(donDatRepository, never()).saveAndFlush(any());
        verifyNoInteractions(dichVuCaAnRepository);
    }

    @Test
    void monDangTat_nemBusinessException() {
        DichVuAnUong buffet = mon("MON1", "Buffet trưa", false, "200000");
        when(dichVuAnUongRepository.findByMaDichVu("MON1")).thenReturn(Optional.of(buffet));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.processPipelineBooking(formAnUong(4, "MON1")));

        assertEquals("Món [Buffet trưa] hiện không nhận đặt", ex.getMessage());
        verify(donDatRepository, never()).saveAndFlush(any());
        verifyNoInteractions(chiTietDonRepository, datBanRepository);
    }

    @Test
    void monKhongCoGia_nemBusinessException() {
        DichVuAnUong buffet = mon("MON1", "Buffet trưa", true, "0");
        when(dichVuAnUongRepository.findByMaDichVu("MON1")).thenReturn(Optional.of(buffet));

        assertThrows(BusinessException.class, () -> service.processPipelineBooking(formAnUong(4, "MON1")));
        verifyNoInteractions(chiTietDonRepository, datBanRepository);
    }

    @Test
    void soKhachAnNull_nemBusinessException() {
        BookingPipelineForm form = formAnUong(4, "MON1");
        form.setSoKhachAn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.processPipelineBooking(form));

        assertEquals(ErrorCode.BAD_REQUEST, ex.getErrorCode());
        verify(donDatRepository, never()).saveAndFlush(any());
        verifyNoInteractions(chiTietDonRepository, datBanRepository, dichVuAnUongRepository);
    }

    @Test
    void soKhachAnNgoaiKhoang_nemBusinessException() {
        for (int soKhach : new int[]{0, 51}) {
            BookingPipelineForm form = formAnUong(soKhach, "MON1");
            assertThrows(BusinessException.class, () -> service.processPipelineBooking(form));
        }
        verifyNoInteractions(datBanRepository);
    }

    @Test
    void thieuCaAn_nemBusinessException() {
        BookingPipelineForm form = formAnUong(4, "MON1");
        form.setMaCaAn("  ");

        BusinessException ex = assertThrows(BusinessException.class, () -> service.processPipelineBooking(form));

        assertEquals("Vui lòng chọn ca dùng bữa", ex.getMessage());
        verifyNoInteractions(datBanRepository);
    }

    @Test
    void veCombo_taoQuyenLoiVaMotDatBanComboGomSuatAn() {
        LoaiVe combo = new LoaiVe();
        combo.setMaLoaiVe("COMBO1");
        combo.setTenLoaiVe("Combo trọn gói");
        combo.setLoaiVe(LoaiVeType.TRON_GOI);
        DichVuAnUong buffet = mon("MON1", "Buffet trưa", true, "200000");
        DichVuAnUong nuoc = mon("MON2", "Nước uống", true, "20000");
        ComboThanhPhan thamQuan = thanhPhan("TP1", "Tham quan", LoaiThanhPhan.THAM_QUAN, null, 1, true);
        ComboThanhPhan anBuffet = thanhPhan("TP2", "Buffet trưa", LoaiThanhPhan.AN_UONG, buffet, 1, true);
        ComboThanhPhan anNuoc = thanhPhan("TP3", "Nước uống", LoaiThanhPhan.AN_UONG, nuoc, 2, true);
        ComboThanhPhan daTat = thanhPhan("TP4", "Đã tắt", LoaiThanhPhan.THAM_QUAN, null, 1, false);
        anNuoc.setTheoDoiSuDung(null);
        when(sanPhamVeService.layVeTheoMa("COMBO1")).thenReturn(combo);
        when(khoVeRepository.findByLoaiVeAndNgayWithLock("COMBO1", LocalDate.now())).thenReturn(Optional.empty());
        when(ticketPricingService.tinhGiaVe("COMBO1", LocalDate.now())).thenReturn(new BigDecimal("500000"));
        when(comboThanhPhanRepository.findByLoaiVe_MaLoaiVe("COMBO1"))
                .thenReturn(List.of(thamQuan, anBuffet, anNuoc, daTat));
        stubMonPhucVuCa(buffet, "CA_TRUA");
        stubMonPhucVuCa(nuoc, "CA_TRUA", "CA_TOI");
        DichVuCaAn linkBuffet = link(buffet, "CA_TRUA");
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON1", "CA_TRUA"))
                .thenReturn(Optional.of(linkBuffet));
        when(dayTypeResolver.getDepositRate()).thenReturn(new BigDecimal("0.30"));
        BookingPipelineForm form = new BookingPipelineForm();
        form.setHoTenKhach("Nguyễn Văn A");
        form.setSdtKhach("0900000000");
        form.setNgayThamQuan(LocalDate.now());
        form.getTicketCode().add("COMBO1");
        form.getTicketQty().add(2);

        service.processPipelineBooking(form);

        verify(quyenLoiDonRepository).saveAllAndFlush(quyenLoiCaptor.capture());
        List<QuyenLoiDon> quyenLoi = quyenLoiCaptor.getValue();
        assertEquals(3, quyenLoi.size());
        assertEquals(List.of(2, 2, 4), quyenLoi.stream().map(QuyenLoiDon::getSoLuongToiDa).toList());
        assertEquals(List.of("THAM_QUAN", "AN_UONG", "AN_UONG"),
                quyenLoi.stream().map(QuyenLoiDon::getLoaiThanhPhanChup).toList());
        assertEquals(List.of("Tham quan", "Buffet trưa", "Nước uống"),
                quyenLoi.stream().map(QuyenLoiDon::getTenThanhPhanChup).toList());
        assertTrue(quyenLoi.stream().allMatch(q -> q.getTrangThai() == TrangThaiQuyenLoi.HIEU_LUC
                && q.getSoLuongDaDung() == 0 && Boolean.TRUE.equals(q.getTheoDoiSuDung())
                && q.getMaQuyenLoi() != null && q.getMaQuyenLoi().length() <= 20
                && q.getChiTietDon() != null && q.getNgayTao() != null));
        assertEquals(3, quyenLoi.stream().map(QuyenLoiDon::getMaQuyenLoi).distinct().count());

        verify(chiTietDonRepository).saveAllAndFlush(chiTietCaptor.capture());
        assertEquals(1, chiTietCaptor.getValue().size());
        assertEquals(chiTietCaptor.getValue().get(0), quyenLoi.get(0).getChiTietDon());

        verify(datBanRepository).saveAllAndFlush(datBanCaptor.capture());
        List<DatBan> datBan = datBanCaptor.getValue();
        assertEquals(1, datBan.size());
        DatBan db = datBan.get(0);
        assertEquals(6, db.getSoKhach());
        assertNull(db.getChiTietDon());
        assertEquals(quyenLoi.get(1), db.getQuyenLoiDon());
        assertEquals(linkBuffet, db.getDichVuCaAn());
        assertEquals(LocalDate.now(), db.getNgayAn());
        assertEquals(TrangThaiDatBan.CHO_XAC_NHAN, db.getTrangThai());
        assertEquals("Suất ăn từ combo", db.getGhiChu());
        assertNotNull(db.getMaDatBan());
    }

    private ComboThanhPhan thanhPhan(String ma, String ten, LoaiThanhPhan loai, DichVuAnUong mon,
                                     int soLuongMoiCombo, boolean trangThai) {
        ThanhPhanDichVu tp = new ThanhPhanDichVu();
        tp.setMaThanhPhan(ma);
        tp.setTenThanhPhan(ten);
        tp.setLoaiThanhPhan(loai);
        tp.setDichVuAnUong(mon);
        tp.setTrangThai(trangThai);
        ComboThanhPhan ctp = new ComboThanhPhan();
        ctp.setThanhPhanDichVu(tp);
        ctp.setSoLuongMoiCombo(soLuongMoiCombo);
        ctp.setTheoDoiSuDung(true);
        return ctp;
    }
}

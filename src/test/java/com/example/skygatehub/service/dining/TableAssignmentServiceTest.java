package com.example.skygatehub.service.dining;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiQuyenLoi;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.dining.DichVuCaAn;
import com.example.skygatehub.dto.dining.MealScheduleRow;
import com.example.skygatehub.dto.dining.TableMapCell;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.QuyenLoiDon;
import com.example.skygatehub.entity.ticket.ThanhPhanDichVu;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.BanAnRepository;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.dining.DichVuCaAnRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.QuyenLoiDonRepository;
import com.example.skygatehub.service.dining.impl.TableAssignmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    @Mock
    private QuyenLoiDonRepository quyenLoiDonRepository;
    @Mock
    private DichVuCaAnRepository dichVuCaAnRepository;
    @Mock
    private ChiTietDonRepository chiTietDonRepository;

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
        return datBan(ma, trangThai, soKhach, ngay, banAn, "CA_TRUA");
    }

    private DatBan datBan(String ma, TrangThaiDatBan trangThai, int soKhach, LocalDate ngay, BanAn banAn, String maCaAn) {
        CaAn ca = new CaAn();
        ca.setMaCaAn(maCaAn);
        DichVuCaAn dv = new DichVuCaAn();
        dv.setCaAn(ca);

        DatBan db = new DatBan();
        db.setMaDatBan(ma);
        db.setTrangThai(trangThai);
        db.setSoKhach(soKhach);
        db.setNgayAn(ngay);
        db.setDichVuCaAn(dv);
        db.setBanAn(banAn);
        db.setDonDat(donDat(TrangThaiDon.DA_XAC_NHAN, TrangThaiThanhToanDon.DA_DAT_COC));
        return db;
    }

    private DonDat donDat(TrangThaiDon trangThaiDon, TrangThaiThanhToanDon trangThaiThanhToan) {
        DonDat don = new DonDat();
        don.setMaDonDat("DD1");
        don.setMaDonHang("DH1");
        don.setTrangThaiDon(trangThaiDon);
        don.setTrangThaiThanhToan(trangThaiThanhToan);
        return don;
    }

    private QuyenLoiDon quyenLoi(String loai, String maMon, boolean theoDoi) {
        QuyenLoiDon ql = new QuyenLoiDon();
        ql.setLoaiThanhPhanChup(loai);
        ql.setSoLuongToiDa(3);
        ql.setSoLuongDaDung(0);
        ql.setTheoDoiSuDung(theoDoi);
        ql.setTrangThai(TrangThaiQuyenLoi.HIEU_LUC);
        ThanhPhanDichVu tp = new ThanhPhanDichVu();
        if (maMon != null) {
            DichVuAnUong mon = new DichVuAnUong();
            mon.setMaDichVu(maMon);
            tp.setDichVuAnUong(mon);
        }
        ql.setThanhPhanDichVu(tp);
        return ql;
    }

    private void stubBooking(DatBan db) {
        when(datBanRepository.findByMaDatBan(db.getMaDatBan())).thenReturn(Optional.of(db));
    }

    @Test
    void assignLuotDaHuy_nemIllegalState() {
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_HUY, 2, LocalDate.now(), null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01", null));
        verify(datBanRepository, never()).saveAndFlush(any());
    }

    @Test
    void assignDonDaHuy_nemIllegalState() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, LocalDate.now(), null);
        db.setDonDat(donDat(TrangThaiDon.DA_HUY, TrangThaiThanhToanDon.DA_DAT_COC));
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01", null));
        assertEquals(TrangThaiDatBan.CHO_XAC_NHAN, db.getTrangThai());
        verify(datBanRepository, never()).saveAndFlush(any());
    }

    @Test
    void assignDonChuaCoc_vanXepDuoc() {
        LocalDate ngay = LocalDate.now();
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, ngay, null);
        db.setDonDat(donDat(TrangThaiDon.CHO_XAC_NHAN, TrangThaiThanhToanDon.CHUA_THANH_TOAN));
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));
        when(datBanRepository.countTrungBan("B1", ngay, "CA_TRUA", "DB1")).thenReturn(0L);

        service.assignTableToBooking("DB1", "B1", "NV01", null);

        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
    }

    @Test
    void assignBanThieuCho_nemIllegalState() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 6, LocalDate.now(), null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01", null));
        verify(datBanRepository, never()).saveAndFlush(any());
    }

    @Test
    void assignTrungBanCungNgayCa_nemIllegalState() {
        LocalDate ngay = LocalDate.now();
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, ngay, null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));
        when(datBanRepository.countTrungBan("B1", ngay, "CA_TRUA", "DB1")).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> service.assignTableToBooking("DB1", "B1", "NV01", null));
        verify(datBanRepository, never()).saveAndFlush(any());
    }

    @Test
    void assignHopLe_chuyenSangDaXacNhan() {
        LocalDate ngay = LocalDate.now();
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, ngay, null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(b));
        when(datBanRepository.countTrungBan("B1", ngay, "CA_TRUA", "DB1")).thenReturn(0L);

        service.assignTableToBooking("DB1", "B1", "NV01", null);

        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        assertEquals(b, db.getBanAn());
        verify(datBanRepository).saveAndFlush(db);
    }

    @Test
    void assignGhiChuMoi_duocTrimVaLuu() {
        LocalDate ngay = LocalDate.now();
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, ngay, null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        service.assignTableToBooking("DB1", "B1", "NV01", "  Cần ghế trẻ em  ");

        assertEquals("Cần ghế trẻ em", db.getGhiChu());
    }

    @Test
    void assignGhiChuRong_giuNguyenGhiChuCu() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, LocalDate.now(), null);
        db.setGhiChu("Đoàn 2 người");
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        service.assignTableToBooking("DB1", "B1", "NV01", "   ");

        assertEquals("Đoàn 2 người", db.getGhiChu());
    }

    @Test
    void assignGhiChuNoiVaoGhiChuCu() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, LocalDate.now(), null);
        db.setGhiChu("Đoàn 2 người");
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        service.assignTableToBooking("DB1", "B1", "NV01", "Bàn cạnh cửa sổ");

        assertEquals("Đoàn 2 người | Bàn cạnh cửa sổ", db.getGhiChu());
    }

    @Test
    void assignGhiChuGopQua500_catCho500KyTu() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, LocalDate.now(), null);
        db.setGhiChu("a".repeat(480));
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));

        service.assignTableToBooking("DB1", "B1", "NV01", "b".repeat(100));

        assertEquals(500, db.getGhiChu().length());
        assertEquals("a".repeat(480) + " | " + "b".repeat(17), db.getGhiChu());
    }

    @Test
    void assignVaChamUniqueKhiGhi_nemIllegalStateCoThongBao() {
        DatBan db = datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, LocalDate.now(), null);
        stubBooking(db);
        when(banAnRepository.findByMaBanAn("B1")).thenReturn(Optional.of(ban("B1", 4, TrangThaiBanAn.SAN_SANG)));
        when(datBanRepository.saveAndFlush(db)).thenThrow(new DataIntegrityViolationException("duplicate"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.assignTableToBooking("DB1", "B1", "NV01", null));
        assertEquals("Bàn B1 vừa được xếp cho lượt khác, vui lòng chọn bàn khác.", ex.getMessage());
    }

    @Test
    void sodoBan_banDangSuDungHomNayNhungDonNgayMai_hienDaXepChoKhach() {
        LocalDate ngayMai = LocalDate.now().plusDays(1);
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, ngayMai, b);
        when(banAnRepository.findAll()).thenReturn(List.of(b));
        when(datBanRepository.findByNgayAn(ngayMai)).thenReturn(List.of(db));

        List<TableMapCell> cells = service.buildTableMap(ngayMai, null, null, null);

        assertEquals(1, cells.size());
        assertEquals("DA_XEP_CHO_KHACH", cells.get(0).getTrangThaiHienThi());
        assertEquals("DB1", cells.get(0).getMaDatBan());
    }

    @Test
    void sodoBan_banDangSuDungKhongBookingHomNay_canGiaiPhong() {
        LocalDate homNay = LocalDate.now();
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        b.setId(7L);
        when(banAnRepository.findAll()).thenReturn(List.of(b));
        when(datBanRepository.findByNgayAn(homNay)).thenReturn(List.of());
        when(datBanRepository.findByTrangThai(TrangThaiDatBan.DA_CHECK_IN)).thenReturn(List.of());

        TableMapCell cell = service.buildTableMap(homNay, null, null, null).get(0);

        assertEquals("CAN_GIAI_PHONG", cell.getTrangThaiHienThi());
        assertEquals("bg-dark", cell.getCssBadge());
        assertEquals("Cần giải phóng", cell.getBadgeText());
        assertEquals(7L, cell.getId());
        assertEquals(true, cell.isCanGiaiPhong());
    }

    @Test
    void sodoBan_banDangSuDungKhongBookingNgayKhac_hienTrong() {
        LocalDate ngayMai = LocalDate.now().plusDays(1);
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        when(banAnRepository.findAll()).thenReturn(List.of(b));
        when(datBanRepository.findByNgayAn(ngayMai)).thenReturn(List.of());

        TableMapCell cell = service.buildTableMap(ngayMai, null, null, null).get(0);

        assertEquals("TRONG", cell.getTrangThaiHienThi());
    }

    @Test
    void sodoBan_luotCheckInCuaCaKhac_hienDangAnKhiLocCa() {
        LocalDate homNay = LocalDate.now();
        BanAn b = ban("B1", 4, TrangThaiBanAn.DANG_SU_DUNG);
        DatBan dangAn = datBan("DB1", TrangThaiDatBan.DA_CHECK_IN, 2, homNay, b, "CA_TRUA");
        when(banAnRepository.findAll()).thenReturn(List.of(b));
        when(datBanRepository.findByNgayAnAndDichVuCaAn_CaAn_MaCaAn(homNay, "CA_TOI")).thenReturn(List.of());
        when(datBanRepository.findByTrangThai(TrangThaiDatBan.DA_CHECK_IN)).thenReturn(List.of(dangAn));

        TableMapCell cell = service.buildTableMap(homNay, "CA_TOI", null, null).get(0);

        assertEquals("DANG_AN", cell.getTrangThaiHienThi());
        assertEquals("DB1", cell.getMaDatBan());
    }

    @Test
    void sodoBan_luotHoanThanhCuaBanDaTra_hienTrong() {
        LocalDate homNay = LocalDate.now();
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan xong = datBan("DB1", TrangThaiDatBan.DA_HOAN_THANH, 2, homNay, b);
        when(banAnRepository.findAll()).thenReturn(List.of(b));
        when(datBanRepository.findByNgayAn(homNay)).thenReturn(List.of(xong));
        when(datBanRepository.findByTrangThai(TrangThaiDatBan.DA_CHECK_IN)).thenReturn(List.of());

        assertEquals("TRONG", service.buildTableMap(homNay, null, null, null).get(0).getTrangThaiHienThi());
    }

    @Test
    void pendingBookings_boDaCheckInDaHoanThanhVaDaHuy() {
        LocalDate homNay = LocalDate.now();
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        when(datBanRepository.findByNgayAn(homNay)).thenReturn(List.of(
                datBan("DB4", TrangThaiDatBan.DA_CHECK_IN, 2, homNay, b),
                datBan("DB2", TrangThaiDatBan.DA_XAC_NHAN, 2, homNay, b),
                datBan("DB3", TrangThaiDatBan.DA_HOAN_THANH, 2, homNay, b),
                datBan("DB5", TrangThaiDatBan.DA_HUY, 2, homNay, null),
                datBan("DB1", TrangThaiDatBan.CHO_XAC_NHAN, 2, homNay, null)));

        List<DatBan> pending = service.getPendingBookings(homNay, null);

        assertEquals(List.of("DB1", "DB2"), pending.stream().map(DatBan::getMaDatBan).toList());
    }

    private CaAn caAn(String ma, LocalTime gioBatDau) {
        CaAn ca = new CaAn();
        ca.setMaCaAn(ma);
        ca.setTenCaAn(ma);
        ca.setGioBatDau(gioBatDau);
        return ca;
    }

    private DichVuAnUong mon(String ma) {
        DichVuAnUong m = new DichVuAnUong();
        m.setMaDichVu(ma);
        return m;
    }

    private ChiTietDon chiTiet(LoaiDichVuDon loai, String tenSanPham, int soLuong, LocalDate ngay, DichVuAnUong mon) {
        ChiTietDon ct = new ChiTietDon();
        ct.setLoaiDichVu(loai);
        ct.setTenSanPham(tenSanPham);
        ct.setSoLuong(soLuong);
        ct.setNgaySuDung(ngay);
        ct.setDichVuAnUong(mon);
        ct.setTrangThai(TrangThaiChiTiet.HIEU_LUC);
        return ct;
    }

    @Test
    void lichBep_liet3MonCuaMotDatBan() {
        LocalDate homNay = LocalDate.now();
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 4, homNay, null);
        CaAn caTrua = caAn("CA_TRUA", LocalTime.of(11, 0));
        when(caAnRepository.findAll()).thenReturn(List.of(caTrua));
        when(datBanRepository.findByNgayAn(homNay)).thenReturn(List.of(db));

        ChiTietDon lau = chiTiet(LoaiDichVuDon.AN_UONG, "Lẩu gà", 1, homNay, mon("MON1"));
        ChiTietDon nuoc = chiTiet(LoaiDichVuDon.AN_UONG, "Nước ép", 2, homNay, mon("MON2"));
        ChiTietDon monCaKhac = chiTiet(LoaiDichVuDon.AN_UONG, "Súp tối", 1, homNay, mon("MON3"));
        ChiTietDon monNgayKhac = chiTiet(LoaiDichVuDon.AN_UONG, "Cá nướng", 1, homNay.plusDays(1), mon("MON1"));
        ChiTietDon ve = chiTiet(LoaiDichVuDon.VE, "Vé vào cổng", 4, homNay, null);
        ChiTietDon daHuy = chiTiet(LoaiDichVuDon.AN_UONG, "Món đã hủy", 1, homNay, mon("MON1"));
        daHuy.setTrangThai(TrangThaiChiTiet.DA_HUY);
        when(chiTietDonRepository.findByDonDat_MaDonDat("DD1"))
                .thenReturn(List.of(lau, nuoc, monCaKhac, monNgayKhac, ve, daHuy));

        QuyenLoiDon buffet = quyenLoi("AN_UONG", "MON4", true);
        buffet.setTenThanhPhanChup("Buffet trưa");
        buffet.setChiTietDon(chiTiet(LoaiDichVuDon.VE, "Combo", 1, homNay, null));
        QuyenLoiDon quyenLoiHuy = quyenLoi("AN_UONG", "MON4", true);
        quyenLoiHuy.setTrangThai(TrangThaiQuyenLoi.DA_HUY);
        quyenLoiHuy.setChiTietDon(buffet.getChiTietDon());
        QuyenLoiDon quyenLoiVe = quyenLoi("VE", null, true);
        quyenLoiVe.setChiTietDon(buffet.getChiTietDon());
        when(quyenLoiDonRepository.findByChiTietDon_DonDat_MaDonDat("DD1"))
                .thenReturn(List.of(buffet, quyenLoiHuy, quyenLoiVe));

        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON1", "CA_TRUA"))
                .thenReturn(Optional.of(new DichVuCaAn()));
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON2", "CA_TRUA"))
                .thenReturn(Optional.of(new DichVuCaAn()));
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON3", "CA_TRUA"))
                .thenReturn(Optional.empty());
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON4", "CA_TRUA"))
                .thenReturn(Optional.of(new DichVuCaAn()));

        List<MealScheduleRow> schedule = service.getMealSchedule(homNay);

        assertEquals(1, schedule.size());
        assertEquals(List.of("Lẩu gà x 1", "Nước ép x 2", "Buffet trưa x 3"),
                schedule.get(0).getMonTheoDatBan().get("DB1"));
    }

    @Test
    void lichBep_datBanKhongCoMon_khongCoKeyDeTemplateRoiVeTenMonCu() {
        LocalDate homNay = LocalDate.now();
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 4, homNay, null);
        when(caAnRepository.findAll()).thenReturn(List.of(caAn("CA_TRUA", LocalTime.of(11, 0))));
        when(datBanRepository.findByNgayAn(homNay)).thenReturn(List.of(db));

        MealScheduleRow row = service.getMealSchedule(homNay).get(0);

        assertNull(row.getMonTheoDatBan().get("DB1"));
        assertEquals(1, row.getDanhSachDatBan().size());
    }

    @Test
    void caAn_sapTheoGioBatDauCaKhongGioXuongCuoi() {
        CaAn toi = caAn("CA_TOI", LocalTime.of(18, 0));
        CaAn khongGio = caAn("CA_CA_NGAY", null);
        CaAn sang = caAn("CA_SANG", LocalTime.of(6, 30));
        when(caAnRepository.findAll()).thenReturn(List.of(toi, khongGio, sang));
        when(datBanRepository.findByNgayAn(any())).thenReturn(List.of());

        assertEquals(List.of("CA_SANG", "CA_TOI", "CA_CA_NGAY"),
                service.getAllCaAn().stream().map(CaAn::getMaCaAn).toList());
        assertEquals(List.of("CA_SANG", "CA_TOI", "CA_CA_NGAY"),
                service.getMealSchedule(LocalDate.now()).stream().map(MealScheduleRow::getMaCaAn).toList());
    }

    @Test
    void checkInDonDaHuy_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        db.setDonDat(donDat(TrangThaiDon.DA_HUY, TrangThaiThanhToanDon.DA_DAT_COC));
        stubBooking(db);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", "NV01"));
        assertEquals("Đơn [DH1] đã bị hủy, không thể Check-in.", ex.getMessage());
        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        assertEquals(TrangThaiBanAn.SAN_SANG, b.getTrangThai());
        verify(checkInRepository, never()).save(any());
    }

    @Test
    void checkInDonChuaCoc_nemIllegalState() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        db.setDonDat(donDat(TrangThaiDon.CHO_XAC_NHAN, TrangThaiThanhToanDon.CHUA_THANH_TOAN));
        stubBooking(db);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", "NV01"));
        assertEquals("Đơn [DH1] chưa đặt cọc, không thể Check-in.", ex.getMessage());

        db.getDonDat().setTrangThaiThanhToan(TrangThaiThanhToanDon.DA_HOAN_TIEN);
        assertThrows(IllegalStateException.class, () -> service.checkInTable("DB1", "NV01"));

        assertEquals(TrangThaiDatBan.DA_XAC_NHAN, db.getTrangThai());
        verify(checkInRepository, never()).save(any());
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
    void checkInDatBanKhongCombo_khongDungQuyenLoiRepo() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 2, LocalDate.now(), b);
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of());
        when(nguoiDungRepository.findByMaNguoiDung("NV01")).thenReturn(Optional.of(new NguoiDung()));

        service.checkInTable("DB1", "NV01");

        verifyNoInteractions(quyenLoiDonRepository);
    }

    @Test
    void checkInDatBanCombo_chuyenQuyenLoiAnUongSangDaDungHet() {
        BanAn b = ban("B1", 4, TrangThaiBanAn.SAN_SANG);
        DatBan db = datBan("DB1", TrangThaiDatBan.DA_XAC_NHAN, 6, LocalDate.now(), b);
        db.setQuyenLoiDon(new QuyenLoiDon());
        stubBooking(db);
        when(datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot("B1", TrangThaiDatBan.DA_CHECK_IN, "DB1"))
                .thenReturn(List.of());
        when(nguoiDungRepository.findByMaNguoiDung("NV01")).thenReturn(Optional.of(new NguoiDung()));

        QuyenLoiDon buffetTrua = quyenLoi("AN_UONG", "MON1", true);
        QuyenLoiDon buffetCaKhac = quyenLoi("AN_UONG", "MON2", true);
        QuyenLoiDon khongTheoDoi = quyenLoi("AN_UONG", "MON1", false);
        QuyenLoiDon quyenLoiVe = quyenLoi("VE", null, true);
        QuyenLoiDon khongCoMon = quyenLoi("AN_UONG", null, true);
        when(quyenLoiDonRepository.findByChiTietDon_DonDat_MaDonDatAndTrangThai("DD1", TrangThaiQuyenLoi.HIEU_LUC))
                .thenReturn(List.of(buffetTrua, buffetCaKhac, khongTheoDoi, quyenLoiVe, khongCoMon));
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON1", "CA_TRUA"))
                .thenReturn(Optional.of(new DichVuCaAn()));
        when(dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn("MON2", "CA_TRUA"))
                .thenReturn(Optional.empty());

        service.checkInTable("DB1", "NV01");

        assertEquals(TrangThaiQuyenLoi.DA_DUNG_HET, buffetTrua.getTrangThai());
        assertEquals(3, buffetTrua.getSoLuongDaDung());
        verify(quyenLoiDonRepository).save(buffetTrua);

        // Món không có trong ca này, quyền lợi không theo dõi, loại khác vé: giữ nguyên
        for (QuyenLoiDon ql : List.of(buffetCaKhac, khongTheoDoi, quyenLoiVe, khongCoMon)) {
            assertEquals(TrangThaiQuyenLoi.HIEU_LUC, ql.getTrangThai());
            assertEquals(0, ql.getSoLuongDaDung());
        }
        verify(quyenLoiDonRepository, never()).save(buffetCaKhac);
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

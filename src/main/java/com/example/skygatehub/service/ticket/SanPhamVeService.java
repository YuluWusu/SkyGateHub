package com.example.skygatehub.service.ticket;

import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.dto.ticket.ComboItemRequest;
import com.example.skygatehub.dto.ticket.SanPhamVeRequest;
import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.ThanhPhanDichVu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SanPhamVeService {

    Page<LoaiVe> layTatCaLoaiVe(Pageable phanTrang);

    default Page<LoaiVe> getAllTickets(Pageable pageable) {
        return layTatCaLoaiVe(pageable);
    }

    Page<LoaiVe> timKiemLoaiVe(String tuKhoa, Pageable phanTrang);

    default Page<LoaiVe> searchTickets(String keyword, Pageable pageable) {
        return timKiemLoaiVe(keyword, pageable);
    }

    List<LoaiVe> layDanhSachVeDangBan();

    default List<LoaiVe> getActiveTickets() {
        return layDanhSachVeDangBan();
    }

    List<LoaiVe> timKiemVeDangBan(String tuKhoa);

    default List<LoaiVe> searchActiveTickets(String keyword) {
        return timKiemVeDangBan(keyword);
    }

    List<LoaiVe> layVeTheoPhanLoai(LoaiVeType loaiVe);

    default List<LoaiVe> getTicketsByType(LoaiVeType loaiVe) {
        return layVeTheoPhanLoai(loaiVe);
    }

    LoaiVe layVeTheoId(Long id);

    default LoaiVe getTicketById(Long id) {
        return layVeTheoId(id);
    }

    LoaiVe layVeTheoMa(String maLoaiVe);

    default LoaiVe getTicketByMa(String maLoaiVe) {
        return layVeTheoMa(maLoaiVe);
    }

    LoaiVe taoLoaiVe(SanPhamVeRequest yeuCau);

    default LoaiVe createTicket(SanPhamVeRequest request) {
        return taoLoaiVe(request);
    }

    LoaiVe capNhatLoaiVe(Long id, SanPhamVeRequest yeuCau);

    default LoaiVe updateTicket(Long id, SanPhamVeRequest request) {
        return capNhatLoaiVe(id, request);
    }

    void xoaLoaiVe(Long id);

    default void deleteTicket(Long id) {
        xoaLoaiVe(id);
    }

    void chuyenTrangThaiVe(Long id);

    default void toggleTicketStatus(Long id) {
        chuyenTrangThaiVe(id);
    }

    List<ComboThanhPhan> layThanhPhanCombo(String maLoaiVe);

    default List<ComboThanhPhan> getComboItems(String maLoaiVe) {
        return layThanhPhanCombo(maLoaiVe);
    }

    void themThanhPhanCombo(String maLoaiVe, ComboItemRequest yeuCau);

    default void addComboItem(String maLoaiVe, ComboItemRequest request) {
        themThanhPhanCombo(maLoaiVe, request);
    }

    void xoaThanhPhanCombo(Long idCombo);

    default void removeComboItem(Long comboId) {
        xoaThanhPhanCombo(comboId);
    }

    List<ThanhPhanDichVu> layTatCaDichVuThanhPhan();

    default List<ThanhPhanDichVu> getAllServiceComponents() {
        return layTatCaDichVuThanhPhan();
    }

    String generateNextMaLoaiVe();
}

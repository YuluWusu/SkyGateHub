package com.example.skygatehub.service.ticket;

import com.example.skygatehub.dto.ticket.BangGiaVeRequest;
import com.example.skygatehub.entity.ticket.BangGiaVe;
import com.example.skygatehub.entity.ticket.NgayDacBiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BangGiaVeService {

    Page<BangGiaVe> layTatCaBangGia(Pageable phanTrang);

    default Page<BangGiaVe> getAllPrices(Pageable pageable) {
        return layTatCaBangGia(pageable);
    }

    Page<BangGiaVe> timKiemBangGia(String tuKhoa, Pageable phanTrang);

    default Page<BangGiaVe> searchPrices(String keyword, Pageable pageable) {
        return timKiemBangGia(keyword, pageable);
    }

    List<BangGiaVe> layBangGiaTheoVe(String maLoaiVe);

    default List<BangGiaVe> getPricesByTicket(String maLoaiVe) {
        return layBangGiaTheoVe(maLoaiVe);
    }

    BangGiaVe layBangGiaTheoId(Long id);

    default BangGiaVe getPriceById(Long id) {
        return layBangGiaTheoId(id);
    }

    BangGiaVe taoBangGia(BangGiaVeRequest yeuCau);

    default BangGiaVe createPrice(BangGiaVeRequest request) {
        return taoBangGia(request);
    }

    BangGiaVe capNhatBangGia(Long id, BangGiaVeRequest yeuCau);

    default BangGiaVe updatePrice(Long id, BangGiaVeRequest request) {
        return capNhatBangGia(id, request);
    }

    void xoaBangGia(Long id);

    default void deletePrice(Long id) {
        xoaBangGia(id);
    }

    List<NgayDacBiet> layTatCaNgayDacBiet();

    default List<NgayDacBiet> getAllSpecialDays() {
        return layTatCaNgayDacBiet();
    }

    NgayDacBiet taoNgayDacBiet(NgayDacBiet ngayDacBiet);

    default NgayDacBiet createSpecialDay(NgayDacBiet ngayDacBiet) {
        return taoNgayDacBiet(ngayDacBiet);
    }

    void xoaNgayDacBiet(Long id);

    default void deleteSpecialDay(Long id) {
        xoaNgayDacBiet(id);
    }
}

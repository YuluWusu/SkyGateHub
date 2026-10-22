package com.example.skygatehub.service.room;

import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RoomDataAndPipelineTest {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @Autowired
    private KhuLuuTruRepository khuLuuTruRepository;

    @Autowired
    private DichVuAnUongRepository dichVuAnUongRepository;

    @Autowired
    private SanPhamVeService sanPhamVeService;

    @Autowired
    private RoomOperationService roomOperationService;

    @Test
    @DisplayName("Kiểm tra danh sách phòng vật lý tải đầy đủ quan hệ LoaiPhong và KhuLuuTru không bị LazyInitializationException")
    void testSearchPhongWithRelations() {
        Page<Phong> page = phongRepository.searchPhong(null, null, null, null, PageRequest.of(0, 10));
        assertNotNull(page);
        assertFalse(page.isEmpty(), "Danh sách phòng vật lý không được rỗng");
        
        for (Phong p : page.getContent()) {
            assertNotNull(p.getSoPhong());
            if (p.getLoaiPhong() != null) {
                assertNotNull(p.getLoaiPhong().getTenLoaiPhong());
                if (p.getLoaiPhong().getKhuLuuTru() != null) {
                    assertNotNull(p.getLoaiPhong().getKhuLuuTru().getTenKhu());
                }
            }
        }
    }

    @Test
    @DisplayName("Kiểm tra danh sách hạng phòng tải đầy đủ KhuLuuTru không bị LazyInitializationException")
    void testSearchLoaiPhongWithRelations() {
        Page<LoaiPhong> page = loaiPhongRepository.searchLoaiPhong(null, null, null, PageRequest.of(0, 10));
        assertNotNull(page);
        assertFalse(page.isEmpty(), "Danh sách hạng phòng không được rỗng");

        for (LoaiPhong lp : page.getContent()) {
            assertNotNull(lp.getTenLoaiPhong());
            if (lp.getKhuLuuTru() != null) {
                assertNotNull(lp.getKhuLuuTru().getTenKhu());
            }
        }
    }

    @Test
    @DisplayName("Kiểm tra dữ liệu Sơ đồ phòng Room Map hoạt động trơn tru")
    void testRoomMapBuilding() {
        var mapCells = roomOperationService.buildRoomMap(null, null, null, null);
        assertNotNull(mapCells);
        assertFalse(mapCells.isEmpty(), "Sơ đồ phòng phải có ít nhất 1 phòng");
    }

    @Test
    @DisplayName("Kiểm tra dữ liệu sẵn sàng cho Pipeline đặt dịch vụ (Vé, Khách sạn, Ẩm thực)")
    void testPipelineDataAvailability() {
        List<LoaiVe> tickets = sanPhamVeService.layDanhSachVeDangBan();
        assertNotNull(tickets);
        assertFalse(tickets.isEmpty(), "Dịch vụ vé phải có dữ liệu");

        List<KhuLuuTru> khuList = khuLuuTruRepository.findAllByOrderByMaKhuAsc();
        assertNotNull(khuList);
        assertFalse(khuList.isEmpty(), "Khu lưu trú phải có dữ liệu");

        List<DichVuAnUong> diningList = dichVuAnUongRepository.findByTrangThai(true);
        assertNotNull(diningList);
        assertFalse(diningList.isEmpty(), "Dịch vụ ẩm thực phải có dữ liệu");
    }
}

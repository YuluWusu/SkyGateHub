package com.example.skygatehub.service.dining;

import com.example.skygatehub.dto.dining.MealScheduleRow;
import com.example.skygatehub.dto.dining.TableMapCell;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;

import java.time.LocalDate;
import java.util.List;

public interface TableAssignmentService {

    /**
     * Xây dựng sơ đồ bàn ăn trực quan theo ngày, ca ăn và khu vực.
     */
    List<TableMapCell> buildTableMap(LocalDate targetDate, String maCaAn, String khuVuc, String statusFilter);

    /**
     * Lấy danh sách các đơn đặt bàn chưa được xếp bàn hoặc đang chờ xử lý theo ngày và ca ăn.
     */
    List<DatBan> getPendingBookings(LocalDate targetDate, String maCaAn);

    /**
     * Lấy danh sách các bàn ăn vật lý phù hợp (sức chứa đủ, chưa bị đặt trùng ca ăn).
     */
    List<BanAn> getAvailableTablesForBooking(String maDatBan);

    /**
     * Thực hiện gán bàn ăn vật lý cho đơn đặt bàn.
     */
    void assignTableToBooking(String maDatBan, String maBanAn, String maNhanVien);

    /**
     * Hủy xếp bàn đã gán cho đơn đặt bàn.
     */
    void unassignTable(String maDatBan);

    /**
     * Check-in nhận bàn khi khách đến nhà hàng.
     */
    String checkInTable(String maDatBan, String maNhanVien);

    /**
     * Trả bàn / Hoàn tất bữa ăn, đưa bàn về trạng thái SẴN SÀNG đón lượt sau.
     */
    String completeTable(String maDatBan, String maNhanVien);

    /**
     * Thống kê lịch phục vụ ca ăn theo ngày cho nhà bếp và quản lý.
     */
    List<MealScheduleRow> getMealSchedule(LocalDate targetDate);

    /**
     * Lấy danh sách tất cả ca ăn (Sáng, Trưa, Tối).
     */
    List<CaAn> getAllCaAn();

    /**
     * Lấy danh sách tất cả các khu vực bàn ăn (Khu Sân Hồ, Tầng 1, Tầng 2, VIP...).
     */
    List<String> getAllKhuVuc();
}

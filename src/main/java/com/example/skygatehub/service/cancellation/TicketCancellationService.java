package com.example.skygatehub.service.cancellation;

import com.example.skygatehub.dto.cancellation.TicketCancelEligibility;
import com.example.skygatehub.entity.cancellation.HuyDon;
import com.example.skygatehub.entity.order.ChiTietDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketCancellationService {

    TicketCancelEligibility evaluateEligibility(ChiTietDon item);

    HuyDon submitTicketCancelRequest(String maChiTiet, String lyDo);

    HuyDon approveCancellation(String maHuy, String adminUserCode, String ghiChu);

    HuyDon rejectCancellation(String maHuy, String adminUserCode, String ghiChu);

    Page<HuyDon> listRequests(String status, Pageable pageable);

    long countPendingRequests();
}

package com.example.skygatehub.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Danh mục mã lỗi hệ thống SkyGateHub.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST("400", "Dữ liệu yêu cầu không hợp lệ."),
    UNAUTHORIZED("401", "Quý khách chưa đăng nhập hoặc phiên làm việc đã hết hạn."),
    FORBIDDEN("403", "Bạn không có quyền thực hiện thao tác này."),
    NOT_FOUND("404", "Không tìm thấy dữ liệu yêu cầu."),
    DATA_INTEGRITY("409", "Không thể xóa hoặc sửa do dữ liệu đang được liên kết hoặc bị trùng khóa."),
    OVERBOOKING("1001", "Đã hết chỗ hoặc không đủ số lượng khả dụng."),
    INTERNAL_ERROR("500", "Hệ thống gặp sự cố trong quá trình xử lý, vui lòng thử lại sau.");

    private final String code;
    private final String message;
}


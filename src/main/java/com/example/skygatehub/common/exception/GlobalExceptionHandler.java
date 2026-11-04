package com.example.skygatehub.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Bộ xử lý ngoại lệ tập trung toàn hệ thống (@ControllerAdvice).
 * Đáp ứng Tiêu chí 2.5 (Bắt lỗi thân thiện) và Tiêu chí 4.5 (Xử lý ràng buộc CSDL).
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("Không tìm thấy tài nguyên: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        log.warn("Lỗi nghiệp vụ tại [{}]: {}", request.getRequestURI(), ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:" + referer;
        }
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String handleIllegalArgState(RuntimeException ex, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        log.warn("Lỗi đối số / trạng thái không hợp lệ tại [{}]: {}", request.getRequestURI(), ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:" + referer;
        }
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/500";
    }

    /**
     * Bắt lỗi ràng buộc toàn vẹn CSDL (Tiêu chí 4.5: không xóa bản ghi đang tham chiếu, trùng khóa)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) {
        log.error("Lỗi vi phạm ràng buộc dữ liệu tại [{}]: {}", request.getRequestURI(), ex.getMessage());
        String userFriendlyMsg = "Không thể thực hiện thao tác do dữ liệu đang được sử dụng ở bảng khác (ràng buộc khóa ngoại) hoặc bị trùng lặp mã dữ liệu!";
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", userFriendlyMsg);
            return "redirect:" + referer;
        }
        model.addAttribute("errorMessage", userFriendlyMsg);
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, HttpServletRequest request, Model model) {
        log.error("Lỗi hệ thống ngoài dự kiến tại [{}]: ", request.getRequestURI(), ex);
        model.addAttribute("errorMessage", "Đã xảy ra sự cố trong quá trình xử lý: " + ex.getMessage());
        return "error/500";
    }
}


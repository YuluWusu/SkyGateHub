package com.example.skygatehub.common.security;

import com.example.skygatehub.common.constant.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        if (requireRole == null) {
            return true;
        }

        HttpSession session = request.getSession(false);
        CurrentUser user = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + request.getRequestURI());
            return false;
        }

        if (Boolean.TRUE.equals(user.getCanDoiMatKhau())) {
            response.sendRedirect(request.getContextPath() + "/doi-mat-khau-lan-dau");
            return false;
        }

        String[] allowedRoles = requireRole.value();
        if (allowedRoles.length == 0) {
            return true;
        }

        for (String role : allowedRoles) {
            if (user.hasRole(role)) {
                return true;
            }
        }

        response.sendRedirect(request.getContextPath() + "/403");
        return false;
    }
}

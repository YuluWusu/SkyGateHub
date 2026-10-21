package com.example.skygatehub.common.web;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("currentUser")
    public CurrentUser populateCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER);
    }
}

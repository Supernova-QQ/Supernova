package com.hanshin.supernova.view_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserInfoViewController {

    // 비밀번호 찾기 페이지로 이동
    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "auth/reset_password"; // reset_password.html로 매핑
    }
}

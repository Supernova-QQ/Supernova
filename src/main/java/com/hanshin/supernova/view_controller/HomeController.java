package com.hanshin.supernova.view_controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

//@Controller
//public class HomeController {
//
//    @GetMapping("/")
//    public String home() {
//        return "home";
//    }
//}
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        // 사용자가 로그인했는지 확인
        boolean isLoggedIn = request.getAttribute("userEmail") != null;  // 예를 들어, userEmail 속성이 있다면 로그인 상태
        model.addAttribute("isLoggedIn", isLoggedIn);  // 모델에 isLoggedIn 추가

        return "home";  // home.html 템플릿으로 이동
    }

    @GetMapping("/auth/login")
    public String showLoginPage() {
        return "auth/signIn"; // signIn.html 템플릿을 반환
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "user/signUp"; // signIn.html 템플릿을 반환
    }

}

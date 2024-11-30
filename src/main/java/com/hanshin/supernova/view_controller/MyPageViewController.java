package com.hanshin.supernova.view_controller;

import com.hanshin.supernova.answer.application.AnswerService;
import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.domain.Question;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPageViewController {

    @GetMapping
    public String getMyPage() {
        return "my/my"; // my.html을 렌더링
    }

    @GetMapping("/profile")
    public String getMyProfile() {return "my/profile";} // profile.html을 렌더링

    @GetMapping("/community")
    public String manageMyCommunity() {return "my/community_management";} // community_management.html을 렌더링

    @GetMapping("/change-password")
    public String changePassword() {return "my/change_password";} // change_password.html을 렌더링


    @GetMapping("/change-nickname")
    public String changeNickname() {return "my/change_nickname";} // change_password.html을 렌더링
}

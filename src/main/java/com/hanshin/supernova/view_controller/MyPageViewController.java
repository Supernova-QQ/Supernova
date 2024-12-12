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

    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping
    public String getMyPage() {
        return "my/my"; // my.html을 렌더링
    }
}

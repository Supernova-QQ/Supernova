package com.hanshin.supernova.view_controller;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@Controller
@RequestMapping("/questions")
public class QuestionViewController {

    @GetMapping("/info/{id}")
    public String communityInfo(@PathVariable("id") Long id, Model model) {
        model.addAttribute("questionId", id);
        return "question/question_info";
    }

    @GetMapping("/create")
    public String createQuestion(@RequestParam(name = "communityId", required = false) Long communityId, Model model) {
        if (communityId != null) {
            model.addAttribute("communityId", communityId);
        }
        return "question/question_create";
    }

    @GetMapping("/edit/{id}")
    public String editQuestion(@PathVariable("id") Long id, @RequestParam(name = "communityId", required = false) Long communityId, Model model) {
        if (communityId != null) {
            model.addAttribute("communityId", communityId);
        }
        model.addAttribute("questionId", id);
        return "question/question_edit";
    }
}

package com.hanshin.supernova.view_controller;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.community.application.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@Controller
@RequestMapping("/communities")
@RequiredArgsConstructor
public class CommunityViewController {

    private final CommunityService communityService;

    @GetMapping("/main")
    public String communityMain() {
        return "community/community_main";
    }

    @GetMapping("/create")
    public String communityCreate() {
        return "community/community_create";
    }

    @GetMapping("/create-success/{id}")
    public String createCommunitySuccess(@PathVariable("id") Long id, Model model) {
        var communityInfo = communityService.getCommunityInfo(id);
        model.addAttribute("community", communityInfo);
        return "community/community_create_after";
    }

    @GetMapping("/info/{id}")
    public String communityInfo(@PathVariable("id") Long id, Model model) {
        model.addAttribute("communityId", id);
        return "community/community_info";
    }

    @GetMapping("/update/{id}")
    public String communityUpdate(@PathVariable("id") Long id, Model model) {
        var communityInfo = communityService.getCommunityInfo(id);
        model.addAttribute("communityId", id);
        model.addAttribute("community", communityInfo);
        return "community/community_update";
    }

    @GetMapping("/{communityId}/unanswered-questions")
    public String unansweredQuestions(@PathVariable(name = "communityId") Long communityId, Model model) {
        model.addAttribute("communityId", communityId);
        return "community/unanswered_question_list";
    }

    @GetMapping("/{communityId}/all-questions")
    public String allQuestions(@PathVariable(name = "communityId") Long communityId, Model model) {
        model.addAttribute("communityId", communityId);
        return "community/all_question_list";
    }
}

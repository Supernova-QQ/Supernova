package com.hanshin.supernova.view_controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/search")
public class SearchViewController {

    @GetMapping
    public String search() {
        return "search/search";
    }
}

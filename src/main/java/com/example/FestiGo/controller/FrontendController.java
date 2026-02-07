package com.example.FestiGo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({ "/", "/login", "/signup" })
    public String index() {
        return "forward:/DBMS-mini-main/index.html";
    }
}

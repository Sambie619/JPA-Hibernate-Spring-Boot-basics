package com.sabin.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html
    }

    @GetMapping("/public/info")
    public String publicInfo() {
        return "public-info"; // templates/public-info.html
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "user-dashboard"; // templates/user-dashboard.html
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());
        return "admin-dashboard"; // templates/admin-dashboard.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }
}


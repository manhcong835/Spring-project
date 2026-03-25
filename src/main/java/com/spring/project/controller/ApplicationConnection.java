package com.spring.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationConnection {
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/signin")
    public String signin() {
        return "client/signin";
    }

    @GetMapping("/signup")
    public String signup() {
        return "client/signup";
    }

    @GetMapping("/admin/signin")
    public String adminSignin() {
        return "admin/signin";
    }
}

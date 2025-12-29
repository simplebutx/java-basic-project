package com.company.java_basic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HealthController {

    @GetMapping("/health")
    @ResponseBody
    public String home() {
        return "ok";
    }
}
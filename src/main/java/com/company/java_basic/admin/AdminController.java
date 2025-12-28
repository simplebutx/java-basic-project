package com.company.java_basic.admin;

import com.company.java_basic.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/admin")
    private String adminPage(Model model)
    {
        var userlist = userRepository.findAll();
        model.addAttribute("userlist", userlist);
        return "admin/admin";
    }
}

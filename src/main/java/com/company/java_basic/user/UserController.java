package com.company.java_basic.user;

import com.company.java_basic.Post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    @GetMapping("/signup")
    String signup() {
        return "users/signup";
    }

    @PostMapping("/signup")
    String postsignup(String username, String password, String name, Model model, RedirectAttributes redirectAttributes) {
        User user = new User();

        if(userRepository.existsByUsername(username))
        {
            model.addAttribute("error", "이미 사용 중인 아이디입니다.");
            return "users/signup";
        }
        user.setUsername(username);
        var hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        user.setName(name);
        user.setRole("ROLE_USER");
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("toast", "회원가입 완료");
        return "redirect:/posts";
    }

    @GetMapping("/login")
    String login() {
        return "users/login";
    }

    @GetMapping("/mypage")
    String mypage(Authentication auth, Model model) {    // auth 안에 로그인한 유저 정보가 들어있음 (MyUserDetailService에서 만든것)
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?required=true";
        }
        var result = postRepository.findByAuthor(auth.getName());
        model.addAttribute("posts", result);
        return "users/mypage";
    }
}

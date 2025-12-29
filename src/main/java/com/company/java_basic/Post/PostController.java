package com.company.java_basic.Post;

import com.company.java_basic.Post.dto.PostCreateRequest;
import com.company.java_basic.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor   // final 필드를 파라미터로 받는 생성자를 Lombok이 자동 생성
public class PostController {
    private final PostRepository postRepository;    // PostController가 사용할 PostRepository를 저장해두는 변수(참조)
    private final PostService postService;
    private final S3UploadService s3UploadService;

    @GetMapping("/")
    String mainpage(){
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    String list(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "posts/list";
    }

    @GetMapping("/posts/new")
    String newPostForm() {
        return "posts/new";
    }

    @PostMapping("/posts")
    String create(@RequestParam String title,
                  @RequestParam String content,
                  @RequestParam(required = false) MultipartFile imageFile,
                  Principal principal, RedirectAttributes redirectAttributes) {

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(principal.getName());
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        String imageUrl = s3UploadService.upload(imageFile, "posts");
        post.setImageUrl(imageUrl);
        postRepository.save(post);
        redirectAttributes.addAttribute("toast", "글이 등록되었습니다");
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    String detail(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/posts/{id}/edit")
    String editpage(@PathVariable Long id, Model model, Principal principal) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));

        if(!post.getAuthor().equals(principal.getName()))
        {
            throw new AccessDeniedException("작성자만 접근 가능합니다.");
        }
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    String edit(@PathVariable Long id, String title, String content, Principal principal, RedirectAttributes redirectAttributes) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));

        if(!post.getAuthor().equals(principal.getName()))
        {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }
        post.setTitle(title);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        redirectAttributes.addFlashAttribute("toast", "글이 수정되었습니다");
        return "redirect:/posts";
    }

    @PostMapping("/posts/{id}/delete")
    String delete(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + id));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isAuthor = post.getAuthor().equals(auth.getName());

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        postRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("toast", "글이 삭제되었습니다");
        return "redirect:/posts";
    }
}

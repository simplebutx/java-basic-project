package com.company.java_basic.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;


    @PostMapping("/posts/{id}/comments")
    String postComment(Principal principal, String content, @PathVariable Long id) {
        Comment comment = new Comment();
        comment.setAuthor(principal.getName());
        comment.setContent(content);
        comment.setParentPostId(id);    // 게시물 아이디
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/comments/{commentId}/edit")
    String editComment(@PathVariable Long id, @PathVariable Long commentId, String content, Authentication auth, RedirectAttributes redirectAttributes) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + commentId));
        if(!comment.getAuthor().equals(auth.getName()))
        {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다,");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        redirectAttributes.addFlashAttribute("toast", "댓글이 수정되었습니다");
        return "redirect:/posts/"+ id;
    }

    @PostMapping("/posts/{id}/comments/{commentId}/delete")
    String deleteComment(@PathVariable Long id, @PathVariable Long commentId, Authentication auth, RedirectAttributes redirectAttributes) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + commentId));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isAuthor = comment.getAuthor().equals(auth.getName());

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }
        commentRepository.deleteById(commentId);
        redirectAttributes.addFlashAttribute("toast", "댓글이 삭제되었습니다");
        return "redirect:/posts/" + id;
    }
}

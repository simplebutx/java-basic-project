package com.company.java_basic.Post;

import com.company.java_basic.Post.dto.PostCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public void savePost(PostCreateRequest req, String username) {

        Post post = new Post();
        post.setAuthor(username);      // ✅ 로그인 유저 아이디
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }
}


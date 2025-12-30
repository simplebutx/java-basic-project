package com.company.java_basic.comment;

import com.company.java_basic.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByParentPostId(Long parentPostId);
}

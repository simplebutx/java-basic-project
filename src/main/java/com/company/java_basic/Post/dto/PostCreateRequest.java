package com.company.java_basic.Post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {  // 클라이언트가 보낸 값들을 한 덩어리로 담는다
    private String author;
    private String title;
    private String content;
}

//클라이언트 -> PostCreateRequest -> Controller -> Service -> Post(Entity) -> DB
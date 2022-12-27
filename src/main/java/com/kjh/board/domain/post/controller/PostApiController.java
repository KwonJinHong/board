package com.kjh.board.domain.post.controller;

import com.kjh.board.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;


}

package com.kjh.board.domain.post.controller;

import com.kjh.board.domain.post.condition.PostSearchCondition;
import com.kjh.board.domain.post.dto.PostInfoDto;
import com.kjh.board.domain.post.dto.PostPagingDto;
import com.kjh.board.domain.post.dto.PostSaveDto;
import com.kjh.board.domain.post.dto.PostUpdateDto;
import com.kjh.board.domain.post.service.PostService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Tag(name = "posts", description = "게시물 API")
public class PostApiController {

    private final PostService postService;

    /**
     * Create - 게시글 저장
     * */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/post")
    @ApiOperation(tags = "posts", value = "게시글 저장")
    public void save(@Valid @RequestBody PostSaveDto postSaveDto) {
        postService.save(postSaveDto);
    }

    /**
     * Read - 게시글 조회
     * */
    @GetMapping("/post/{postId}")
    @ApiOperation(tags = "posts", value = "게시글 조회")
    public ResponseEntity<PostInfoDto> getInfo(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(postService.getPostInfo(postId));
    }

    /**
     * Update - 게시글 수정
     * */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/post/{postId}")
    @ApiOperation(tags = "posts", value = "게시글 수정")
    public void update(@PathVariable("postId") Long postId, @RequestBody PostUpdateDto postUpdateDto) {
        postService.update(postId, postUpdateDto);
    }

    /**
     * Delete - 게시글 삭제
     * */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/post/{postId}")
    @ApiOperation(tags = "posts", value = "게시글 삭제")
    public void delete(@PathVariable("postId") Long postId) {
        postService.delete(postId);
    }

    /**
     * 게시글 검색
     * */
    @GetMapping("/post")
    @ApiOperation(tags = "posts", value = "게시글 검색")
    public ResponseEntity<PostPagingDto> search(@ApiIgnore Pageable pageable, @RequestBody PostSearchCondition postSearchCondition) {
        return ResponseEntity.ok(postService.getPostList(pageable, postSearchCondition));
    }

}

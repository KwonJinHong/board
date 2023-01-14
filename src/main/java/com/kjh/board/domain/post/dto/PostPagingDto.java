package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Schema(description = "제목이나 검색으로 찾아진 게시글 정보 DTO")
public class PostPagingDto {

    @Schema(description = "검색으로 찾아진 총 페이지 수")
    private int totalPageCount;//총 몇페이지가 존재하는지
    @Schema(description = "현재 페이지")
    private int currentPageNum;//현재 몇 페이지인지
    @Schema(description = "검색으로 찾아진 게시글의 총 개수")
    private long totalElementCount; //존재하는 게시글의 총 개수
    @Schema(description = "현재 페이지에 존재하는 게시글 수")
    private int currentPageElementCount; //현재 페이지에 존재하는 게시글 수
    @Schema(description = "간략한 게시글 정보")
    private List<SimplePostInfo> simpleLectureDtoList;


    public PostPagingDto(Page<Post> searchResults) {
        this.totalPageCount = searchResults.getTotalPages();
        this.currentPageNum = searchResults.getNumber();
        this.totalElementCount = searchResults.getTotalElements();
        this.currentPageElementCount = searchResults.getNumberOfElements();
        this.simpleLectureDtoList = searchResults.getContent().stream().map(SimplePostInfo::new).collect(Collectors.toList());
    }
}

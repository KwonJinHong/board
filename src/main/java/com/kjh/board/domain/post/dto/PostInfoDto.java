package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.comment.dto.CommentInfoDto;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Schema(description = "게시글 조회 정보 DTO")
public class PostInfoDto {

    @Schema(description = "게시글 ID")
    private Long postId;
    @Schema(description = "게시글 제목")
    private String title;
    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 작성자에 대한 정보")
    private UserInfoDto userDto;//작성자에 대한 정보
    @Schema(description = "게시글에 달린 댓글들 정보")
    private List<CommentInfoDto> commentInfoDtoList;//댓글 정보들

    public PostInfoDto(Post post) {

        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();

        this.userDto = new UserInfoDto(post.getUser());

        /**
         * 댓글과 대댓글을 그룹짓기
         * post.getComments()는 댓글과 대댓글이 모두 조회된다.
         */

        Map<Comment, List<Comment>> commentListMap = post.getComments().stream()

                // 댓글이 아닌 대댓글인 것만 가져온다.
                .filter(comment -> comment.getParent() != null)

                //필터링 된 것들은 모두 대댓글이고, 대댓글의 Parent(댓글)를 통해 그룹핑합니다. 이렇게 되면 Map에는 <댓글, List<해당 댓글에 달린 대댓글>>의 형식으로 그룹핑됩니다.
                .collect(Collectors.groupingBy(Comment::getParent));


        /**
         * 댓글과 대댓글을 통해 CommentInfoDto 생성
         */
        // 그룹지은 것들 중 keySet , 즉 댓글들을 가지고 옵니다.
        commentInfoDtoList = commentListMap.keySet().stream()

                // 댓글들을 CommentInfoDto로 변환시켜줍니다. 이때 CommentInfoDto의 생성자로 댓글과 해당 댓글에 달린 대댓글들을 인자로 넣어줍니다.
                .map(comment -> new CommentInfoDto(comment, commentListMap.get(comment)))
                .collect(Collectors.toList());

    }
}

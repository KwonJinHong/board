package com.kjh.board.domain.comment.dto;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.user.dto.UserInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@Schema(description = "대댓글 정보 DTO")
public class ReCommentInfoDto {

    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다.";

    @Schema(description = "대댓글이 달린 게시글의 ID")
    private Long postId;//대댓글이 달린 게시글의 ID
    @Schema(description = "해당 대댓글의 부모 댓글 ID")
    private Long parentId;//해당 대댓글이 달린 부모 댓글의 ID
    @Schema(description = "대댓글 ID")
    private Long reCommentId; // 해당 대댓글의 아이디

    @Schema(description = "대댓글 내용")
    private String content;//내용 (삭제되었다면 "삭제된 댓글입니다 출력")
    @Schema(description = "대댓글 삭제 유무")
    private boolean isRemoved;//삭제되었는지?

    @Schema(description = "대댓글 작성자 정보")
    private UserInfoDto userInfoDto;//댓글 작성자에 대한 정보

    public ReCommentInfoDto(Comment reComment) {
        this.postId = reComment.getPost().getId();
        this.parentId = reComment.getParent().getId();
        this.reCommentId = reComment.getId();
        this.content = reComment.getContent();

        /**
         * 댓글의 삭제 여부 판별
         * 댓글이 삭제되었을 경우, 내용이 "삭제된 댓글입니다."로 바뀌게 됨
         * */
        if(reComment.isRemoved()){
            this.content = DEFAULT_DELETE_MESSAGE;
        }

        this.isRemoved = reComment.isRemoved();
        this.userInfoDto = new UserInfoDto(reComment.getUser());
    }

}

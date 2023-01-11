package com.kjh.board.domain.comment.dto;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.user.dto.UserInfoDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CommentInfoDto {

    private final static String DEFAULT_DELETE_MESSAGE = "삭제된 댓글입니다.";

    private Long commentId;//해당 댓글의 ID

    private Long postId;//댓글이 달린 게시글의 ID

    private String content;//내용 (삭제되었다면 "삭제된 댓글입니다 출력")
    private boolean isRemoved;//삭제되었는지?


    private UserInfoDto userInfoDto;//댓글 작성자에 대한 정보

    private List<ReCommentInfoDto> reCommentListDtoList;//대댓글에 대한 정보들


    public CommentInfoDto(Comment comment, List<Comment> reCommentList) {

        this.postId = comment.getPost().getId();
        this.commentId = comment.getId();


        this.content = comment.getContent();

        /**
         * 댓글의 삭제 여부 판별
         * 댓글이 삭제되었을 경우, 내용이 "삭제된 댓글입니다."로 바뀌게 됨
         * */
        if(comment.isRemoved()){
            this.content = DEFAULT_DELETE_MESSAGE;
        }

        this.isRemoved = comment.isRemoved();



        this.userInfoDto = new UserInfoDto(comment.getUser());

        this.reCommentListDtoList = reCommentList.stream().map(ReCommentInfoDto::new).collect(Collectors.toList());

    }
}

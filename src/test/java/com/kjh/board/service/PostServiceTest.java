package com.kjh.board.service;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.comment.dto.CommentInfoDto;
import com.kjh.board.domain.comment.repository.CommentRepository;
import com.kjh.board.domain.comment.service.CommentService;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.condition.PostSearchCondition;
import com.kjh.board.domain.post.dto.PostInfoDto;
import com.kjh.board.domain.post.dto.PostPagingDto;
import com.kjh.board.domain.post.dto.PostSaveDto;
import com.kjh.board.domain.post.dto.PostUpdateDto;
import com.kjh.board.domain.post.exception.PostException;
import com.kjh.board.domain.post.exception.PostExceptionType;
import com.kjh.board.domain.post.service.PostService;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired CommentService commentService;
    @Autowired UserService userService;

    PostException postException;

    @Autowired UserRepository userRepository;
    @PersistenceContext EntityManager em;

    private static final String USERNAME = "kjh1234";
    private static final String PASSWORD = "1q2w3e4r!!";


    private void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void joinAndSetAuthentication() throws Exception {
        userService.join(new UserJoinDto(USERNAME,PASSWORD,"zmfmfm","zmfmfm23@zmzmz.com","000-1111-1111"));

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void setAnotherAuthentication() throws Exception {
        userService.join(new UserJoinDto(USERNAME+"hjk321",PASSWORD,"dngngn","zlglgl@zmzmz.com","000-2222-1111"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME+"hjk321")
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }



    @Test
    public void 게시글_저장_및_ID로_게시글_조회() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        //when
        postService.save(postSaveDto);
        clear();

        Long id = postRepository.findAll().get(0).getId();

        //then
        PostInfoDto findPost = postService.getPostInfo(id);
        assertThat(findPost.getPostId()).isEqualTo(id);
        assertThat(findPost.getTitle()).isEqualTo(title);
        assertThat(findPost.getContent()).isEqualTo(content);

    }

    @Test
    public void 게시글_저장_실패_제목이나_내용없음() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";

        PostSaveDto postSaveDto1 = new PostSaveDto(title, null);
        PostSaveDto postSaveDto2 = new PostSaveDto(null, content);

        int result = postRepository.findAll().size();

        //when, then
        assertThat(result).isEqualTo(0);


    }

    @Test
    public void 게시글_수정() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        String changeTitle = "안녕못해";
        String changeContent = "안먹었어";

        Long id = postRepository.findAll().get(0).getId();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.of(changeTitle), Optional.of(changeContent));
        postService.update(id, postUpdateDto);

        //then
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));
        assertThat(post.getTitle()).isEqualTo(changeTitle);
        assertThat(post.getContent()).isEqualTo(changeContent);

    }

    @Test
    public void 게시글_제목만_수정() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        String changeTitle = "안녕못해";

        Long id = postRepository.findAll().get(0).getId();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.of(changeTitle), Optional.empty());
        postService.update(id, postUpdateDto);

        //then
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));
        assertThat(post.getTitle()).isEqualTo(changeTitle);
        assertThat(post.getContent()).isEqualTo(content);

    }

    @Test
    public void 게시글_내용만_수정() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        String changeContent = "안먹었어";

        Long id = postRepository.findAll().get(0).getId();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.empty(), Optional.of(changeContent));
        postService.update(id, postUpdateDto);

        //then
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(changeContent);

    }

    @Test
    public void 게시글_삭제() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        Long id = postRepository.findAll().get(0).getId();
        postService.delete(id);

        //then
        int result = postRepository.findAll().size();
        assertThat(result).isEqualTo(0);

    }

    @Test
    public void 게시글_삭제실패_권한이없음() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        Long id = postRepository.findAll().get(0).getId();

        //then
        setAnotherAuthentication();

        assertThrows(PostException.class, ()-> postService.delete(id));

    }

    @Test
    public void 게시글_조회() throws Exception {
        com.kjh.board.domain.user.User user1 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy1").password("1q2w3e4r!!1").nickname("주함파1호").email("dlgl1@dlgl.com").phoneNumber("010-1111-2222").role(Role.USER).build());
        com.kjh.board.domain.user.User user2 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy2").password("1q2w3e4r!!2").nickname("주함파2호").email("dlgl2@dlgl.com").phoneNumber("010-1112-2222").role(Role.USER).build());
        com.kjh.board.domain.user.User user3 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy3").password("1q2w3e4r!!3").nickname("주함파3호").email("dlgl3@dlgl.com").phoneNumber("010-1113-2222").role(Role.USER).build());
        com.kjh.board.domain.user.User user4 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy4").password("1q2w3e4r!!4").nickname("주함파4호").email("dlgl4@dlgl.com").phoneNumber("010-1114-2222").role(Role.USER).build());
        com.kjh.board.domain.user.User user5 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy5").password("1q2w3e4r!!5").nickname("주함파5호").email("dlgl5@dlgl.com").phoneNumber("010-1115-2222").role(Role.USER).build());


        Map<Integer, Long> userIdMap = new HashMap<>();
        userIdMap.put(1,user1.getId());
        userIdMap.put(2,user2.getId());
        userIdMap.put(3,user3.getId());
        userIdMap.put(4,user4.getId());
        userIdMap.put(5,user5.getId());


        /**
         * Post 생성
         */

        Post post = Post.builder().title("히히").content("호호").build();
        post.confirmWriter(user1);
        postRepository.save(post);
        em.flush();


        /**
         * Comment 생성(댓글)
         */

        final int COMMENT_COUNT = 10;

        for(int i = 1; i<=COMMENT_COUNT; i++ ){
            Comment comment = Comment.builder().content("댓글" + i).build();
            comment.confirmWriter(userRepository.findById(userIdMap.get(i % 3 + 1)).orElse(null));
            comment.confirmPost(post);
            commentRepository.save(comment);
        }


        /**
         * ReComment 생성(대댓글)
         */
        final int COMMENT_PER_RECOMMENT_COUNT = 20;
        commentRepository.findAll().stream().forEach(comment -> {

            for(int i = 1; i<=20; i++ ){
                Comment recomment = Comment.builder().content("대댓글" + i).build();
                recomment.confirmWriter(userRepository.findById(userIdMap.get(i % 3 + 1)).orElse(null));

                recomment.confirmPost(comment.getPost());
                recomment.confirmParent(comment);
                commentRepository.save(recomment);
            }

        });

        clear();


        //when
        PostInfoDto postInfo = postService.getPostInfo(post.getId());


        //then
        assertThat(postInfo.getPostId()).isEqualTo(post.getId());
        assertThat(postInfo.getContent()).isEqualTo(post.getContent());
        assertThat(postInfo.getUserDto().getUsername()).isEqualTo(post.getUser().getUsername());


        int recommentCount = 0;
        for (CommentInfoDto commentInfoDto : postInfo.getCommentInfoDtoList()) {
            recommentCount += commentInfoDto.getReCommentListDtoList().size();
        }

        assertThat(postInfo.getCommentInfoDtoList().size()).isEqualTo(COMMENT_COUNT);
        assertThat(recommentCount).isEqualTo(COMMENT_PER_RECOMMENT_COUNT * COMMENT_COUNT);

    }

    @Test
    public void 게시글_검색_조건없음() throws Exception {
        //given

        /**
         * User 저장
         */
        com.kjh.board.domain.user.User user1 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy1").password("1q2w3e4r!!1").nickname("주함파1호").email("dlgl1@dlgl.com").phoneNumber("010-1111-2222").role(Role.USER).build());


        /**
         * Post 생성
         */
        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("게시글"+ i).content("내용"+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        clear();


        //when
        final int PAGE = 0;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();

        PostPagingDto postList = postService.getPostList(pageRequest, postSearchCondition);


        //then
        assertThat(postList.getTotalElementCount()).isEqualTo(POST_COUNT);

        assertThat(postList.getTotalPageCount()).isEqualTo((POST_COUNT % SIZE == 0)
                ? POST_COUNT/SIZE
                : POST_COUNT/SIZE + 1);

        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }

    @Test
    public void 게시글_검색_제목일치() throws Exception {
        //given

        /**
         * User 저장
         */
        com.kjh.board.domain.user.User user1 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy1").password("1q2w3e4r!!1").nickname("주함파1호").email("dlgl1@dlgl.com").phoneNumber("010-1111-2222").role(Role.USER).build());


        /**
         * 일반 Post 생성
         */
        final int DEFAULT_POST_COUNT  = 100;
        for(int i = 1; i<= DEFAULT_POST_COUNT; i++ ){
            Post post = Post.builder().title("게시글"+ i).content("내용"+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        /**
         * 제목에 SSS가 들어간 POST 생성
         */
        final String SEARCH_TITLE_STR = "SSS";

        final int COND_POST_COUNT = 100;

        for(int i = 1; i<=COND_POST_COUNT; i++ ){
            Post post = Post.builder().title(SEARCH_TITLE_STR+ i).content("내용"+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        clear();


        //when
        final int PAGE = 2;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        postSearchCondition.setTitle(SEARCH_TITLE_STR);

        PostPagingDto postList = postService.getPostList(pageRequest, postSearchCondition);


        //then
        assertThat(postList.getTotalElementCount()).isEqualTo(COND_POST_COUNT);

        assertThat(postList.getTotalPageCount()).isEqualTo((COND_POST_COUNT % SIZE == 0)
                ? COND_POST_COUNT/SIZE
                : COND_POST_COUNT/SIZE + 1);

        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }

    @Test
    public void 게시글_검색_내용일치() throws Exception {
        //given

        /**
         * User 저장
         */
        com.kjh.board.domain.user.User user1 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy1").password("1q2w3e4r!!1").nickname("주함파1호").email("dlgl1@dlgl.com").phoneNumber("010-1111-2222").role(Role.USER).build());


        /**
         * Post 생성
         */
        final int DEFAULT_POST_COUNT  = 100;
        for(int i = 1; i<= DEFAULT_POST_COUNT; i++ ){
            Post post = Post.builder().title("게시글"+ i).content("내용"+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        /**
         * 내용에 SSS가 들어간 POST 생성
         */
        final String SEARCH_CONTENT_STR = "SSS";

        final int COND_POST_COUNT = 100;

        for(int i = 1; i<=COND_POST_COUNT; i++ ){
            Post post = Post.builder().title("제목"+ i).content(SEARCH_CONTENT_STR+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        clear();


        //when
        final int PAGE = 2;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        postSearchCondition.setContent(SEARCH_CONTENT_STR);

        PostPagingDto postList = postService.getPostList(pageRequest, postSearchCondition);


        //then
        assertThat(postList.getTotalElementCount()).isEqualTo(COND_POST_COUNT);

        assertThat(postList.getTotalPageCount()).isEqualTo((COND_POST_COUNT % SIZE == 0)
                ? COND_POST_COUNT/SIZE
                : COND_POST_COUNT/SIZE + 1);

        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }

    @Test
    public void 게시글_검색_제목과_내용일치() throws Exception {
        //given

        /**
         * User 저장
         */
        com.kjh.board.domain.user.User user1 = userRepository.save(com.kjh.board.domain.user.User.builder().username("userDummy1").password("1q2w3e4r!!1").nickname("주함파1호").email("dlgl1@dlgl.com").phoneNumber("010-1111-2222").role(Role.USER).build());


        /**
         * Post 생성
         */
        final int DEFAULT_POST_COUNT  = 100;
        for(int i = 1; i<= DEFAULT_POST_COUNT; i++ ){
            Post post = Post.builder().title("게시글"+ i).content("내용"+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        /**
         * 제목엔 SSS 내용엔 UR가 들어간 POST 생성
         */
        final String SEARCH_TITLE_STR = "SSS";
        final String SEARCH_CONTENT_STR = "UR";

        final int COND_POST_COUNT = 100;

        for(int i = 1; i<=COND_POST_COUNT; i++ ){
            Post post = Post.builder().title(SEARCH_TITLE_STR + i).content(SEARCH_CONTENT_STR+i).build();
            post.confirmWriter(user1);
            postRepository.save(post);
        }

        clear();


        //when
        final int PAGE = 2;
        final int SIZE = 20;
        PageRequest pageRequest = PageRequest.of(PAGE, SIZE);

        PostSearchCondition postSearchCondition = new PostSearchCondition();
        postSearchCondition.setTitle(SEARCH_TITLE_STR);
        postSearchCondition.setContent(SEARCH_CONTENT_STR);

        PostPagingDto postList = postService.getPostList(pageRequest, postSearchCondition);


        //then
        assertThat(postList.getTotalElementCount()).isEqualTo(COND_POST_COUNT);

        assertThat(postList.getTotalPageCount()).isEqualTo((COND_POST_COUNT % SIZE == 0)
                ? COND_POST_COUNT/SIZE
                : COND_POST_COUNT/SIZE + 1);

        assertThat(postList.getCurrentPageNum()).isEqualTo(PAGE);
        assertThat(postList.getCurrentPageElementCount()).isEqualTo(SIZE);
    }


}
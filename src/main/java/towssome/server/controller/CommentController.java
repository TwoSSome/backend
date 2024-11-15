package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Comment;
import towssome.server.entity.Member;
import towssome.server.service.CommentLikeService;
import towssome.server.service.CommentService;
import towssome.server.service.MemberService;

import java.io.IOException;

@Tag(name = "댓글")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class CommentController {
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final MemberAdvice memberAdvice;
    private static final int DEFAULT_SIZE = 20;
    private final MemberService memberService;

    @Operation(summary = "댓글 생성 API", parameters = {
            @Parameter(name = "reviewId", description = "댓글이 생성될 리뷰글 id"),
            @Parameter(name = "req", description = "body : 댓글 본문 ")
    })
    @PostMapping("/{reviewId}/create")
    public ResponseEntity<CreateRes> createComment(@PathVariable Long reviewId, @RequestBody CommentReq req) throws IOException{
        log.info("commentDTO = {}", req);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Comment comment = commentService.createComment(reviewId, req.body(), username);
        return new ResponseEntity<>(new CreateRes(comment.getId()), HttpStatus.OK);
    }

    @Operation(summary = "댓글 업데이트 API", parameters = {
            @Parameter(name = "commentId", description = "업데이트할 댓글 id"),
            @Parameter(name = "req", description = "body : 업데이트 본문")
    })
    @PostMapping("/{reviewId}/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateReq req, @PathVariable String reviewId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commentService.getComment(commentId).getMember().getUsername().equals(username)){
            return new ResponseEntity<>("You are not the author of this comment", HttpStatus.FORBIDDEN);
        }
        commentService.updateComment(commentId, req);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제 API", parameters = {
            @Parameter(name = "commentId", description = "삭제할 댓글 id")
    })
    @PostMapping("/{reviewId}/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long reviewId, @PathVariable Long commentId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commentService.getComment(commentId).getMember().getUsername().equals(username)){
            return new ResponseEntity<>("You are not the author of this comment", HttpStatus.FORBIDDEN);
        }
        Comment comment = commentService.getComment(commentId);
        log.info(String.valueOf(comment));
        commentService.deleteComment(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "댓글 조회 API", description = "선택한 리뷰글의 댓글을 조회", parameters = {
            @Parameter(name = "reviewId", description = "리뷰 id")
    })
    @GetMapping("/{reviewId}/comments")
    public CursorResult<CommentRes> getComments(@PathVariable Long reviewId,
                                                @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                @RequestParam(value = "sort", defaultValue = "asc", required = false) String sort,
                                                @RequestParam(value = "size", required = false) Integer size){
        if(size == null) size = DEFAULT_SIZE;
        return commentService.getCommentPageByReviewId(reviewId, cursorId, sort, PageRequest.of(0,size));
    }

    @Operation(summary = "댓글 좋아요/취소 API", description = "선택한 리뷰글의 댓글을 조회", parameters = {
            @Parameter(name = "commentId", description = "좋아요/취소할 댓글의 id")
    })
    @PostMapping("/{reviewId}/commentLike/{commentId}")
    public ResponseEntity<ChangeLikeRes> changeLike(@PathVariable Long reviewId, @PathVariable Long commentId){
        Member user = memberAdvice.findJwtMember();
        Comment comment = commentService.getComment(commentId);
        Boolean likeStatus = commentLikeService.likeProcess(user, comment);
        ChangeLikeRes changeLikeRes;
        changeLikeRes = new ChangeLikeRes(likeStatus);
        return new ResponseEntity<>(changeLikeRes,HttpStatus.OK);
    }

    @Operation(summary = "고정 댓글 조회 API", description = "선택한 리뷰글의 고정 댓글을 조회", parameters = {
            @Parameter(name = "reviewId", description = "조회할 리뷰 id")
    })
    @GetMapping("{reviewId}/fixedComment")
    public ResponseEntity<CommentRes> getFixedComment(@PathVariable Long reviewId){
        Comment comment = commentService.getFixedCommentByReviewId(reviewId);
        Member commentedMember = comment.getMember();
        ProfileSimpleRes profileSimpleRes;
        CommentRes commentRes;
        if(comment == null) commentRes = null;
        else{
            profileSimpleRes = new ProfileSimpleRes(
                    commentedMember.getNickName(),
                    commentedMember.getProfilePhoto() == null ? null : commentedMember.getProfilePhoto().getS3Path(),
                    commentedMember.getId()
            );
            commentRes = new CommentRes(
                    comment.getId(),
                    comment.getBody(),
                    comment.getCreateDate(),
                    comment.getLastModifiedDate(),
                    comment.getReviewPost().getId(),
                    commentLikeService.isLikedComment(memberAdvice.findJwtMember(),comment),
                    commentLikeService.countLike(comment),
                    comment.getFixFlag(),
                    profileSimpleRes
            );
        }
        return new ResponseEntity<>(commentRes, HttpStatus.OK);
    }

    @PostMapping("{reviewId}/fix/{commentId}")
    public ResponseEntity<?> changeFixedComment(@PathVariable Long reviewId, @PathVariable Long commentId){
        commentService.changeFixedComment(reviewId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("{reviewId}/unpin")
    public ResponseEntity<?> unpinComment(@PathVariable Long reviewId){
        commentService.unpinComment(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

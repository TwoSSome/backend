package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class CommentController {
    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final MemberAdvice memberAdvice;
    private static final int DEFAULT_SIZE = 20;

    @PostMapping("/{reviewId}/create")
    public ResponseEntity<?> createComment(@PathVariable Long reviewId, @RequestBody CommentReq req) throws IOException{
        log.info("commentDTO = {}", req);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        commentService.createComment(req, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{reviewId}/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long reviewId, @PathVariable Long commentId, @RequestBody CommentUpdateReq req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commentService.getComment(commentId).getMember().getUsername().equals(username)){
            return new ResponseEntity<>("You are not the author of this comment", HttpStatus.FORBIDDEN);
        }
        commentService.updateComment(commentId, req);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{reviewId}/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long reviewId, @PathVariable Long commentId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commentService.getComment(commentId).getMember().getUsername().equals(username)){
            return new ResponseEntity<>("You are not the author of this comment", HttpStatus.FORBIDDEN);
        }
        Comment comment = commentService.findComment(commentId);
        log.info(String.valueOf(comment));
        commentService.deleteComment(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{reviewId}/comments")
    public CursorResult<CommentRes> getComments(@PathVariable Long reviewId,
                                                @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                @RequestParam(value = "sort", defaultValue = "asc", required = false) String sort,
                                                @RequestParam(value = "size", required = false) Integer size){
        if(size == null) size = DEFAULT_SIZE;
        return commentService.getCommentPageByReviewId(reviewId, cursorId, sort, PageRequest.of(0,size));
    }

    @PostMapping("/{reviewId}/commentLike/{commentId}")
    public ResponseEntity<?> changeLike(@PathVariable Long reviewId, @PathVariable Long commentId){
        Member user = memberAdvice.findJwtMember();
        Comment comment = commentService.getComment(commentId);
        commentLikeService.likeProcess(user, comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

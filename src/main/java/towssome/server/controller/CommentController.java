package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CommentReq;
import towssome.server.dto.CommentListRes;
import towssome.server.dto.CommentUpdateReq;
import towssome.server.entity.Comment;
import towssome.server.service.CommentService;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class CommentController {
    private final CommentService commentService;
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
    public PageResult<CommentListRes> getCommentList(@PathVariable Long reviewId, @RequestParam String sort, @RequestParam int page){
        Page<Comment> commentList = commentService.getComments(sort, reviewId, page-1, DEFAULT_SIZE);
        ArrayList<CommentListRes> commentListRes = new ArrayList<>();
        for(Comment comment: commentList.getContent()){
            commentListRes.add(new CommentListRes(
                    comment.getId(),
                    comment.getBody(),
                    comment.getCreateDate(),
                    comment.getLastModifiedDate(),
                    comment.getMember().getId(),
                    comment.getReviewPost().getId()
            ));
        }
        return new PageResult<>(
                commentListRes,
                commentList.getTotalElements(),
                commentList.getTotalPages(),
                commentList.getNumber()+1,
                DEFAULT_SIZE
        );
    }
}

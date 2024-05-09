package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CommentReq;
import towssome.server.dto.CommentRes;
import towssome.server.dto.CommentUpdateReq;
import towssome.server.entity.Comment;
import towssome.server.repository.CommentRepository;
import towssome.server.service.CommentService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class CommentController {
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    @PostMapping("/{reviewId}/create")
    public ResponseEntity<?> createComment(@PathVariable Long reviewId, @RequestBody CommentReq req) throws IOException{ // 세션 추가 되야 함
        log.info("commentDTO = {}", req);
        commentService.createComment(req);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{reviewId}/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long reviewId, @PathVariable Long commentId,@RequestBody CommentUpdateReq req) {
        commentService.updateComment(req);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{reviewId}/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long reviewId, @PathVariable Long commentId){
        Comment comment = commentService.findComment(commentId);
        log.info(String.valueOf(comment));
        commentService.deleteComment(comment);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{reviewId}/comments")
    public Page<CommentRes> getCommentsByReviewId(Long reviewId, Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findAllByReviewPostId(reviewId, pageable);
        return commentsPage.map(comment -> new CommentRes(
                comment.getId(),
                comment.getBody(),
                comment.getCreateDate(),
                comment.getLatsModifiedDate(),
                comment.getMember().getId(),
                comment.getReviewPost().getId()
        ));
    }
}

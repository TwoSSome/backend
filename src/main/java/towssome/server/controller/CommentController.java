package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CommentDTO;
import towssome.server.service.CommentService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/{reviewId}/create")
    public ResponseEntity<?> createComment(@PathVariable Long reviewId, @RequestBody CommentDTO commentDTO){ // 세션 추가 되야 함
        Long memberId = 1L; // 세션 구현되면 삭제
        log.info("commentDTO = {}", commentDTO);
        commentService.createComment(memberId,reviewId,commentDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

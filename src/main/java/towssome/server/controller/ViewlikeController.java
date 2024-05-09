package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.ViewLikeReq;
import towssome.server.service.ViewlikeService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ViewlikeController {
    private final ViewlikeService viewlikeService;

    @PostMapping("/like")
    public ResponseEntity<?> like(@RequestBody ViewLikeReq req) {
        viewlikeService.like(req);
        log.info("like");
        return ResponseEntity.ok("like success");
    }

//    @GetMapping("/view/{memberId}")
//    public ResponseEntity<?> viewCount(@PathVariable Long memberId) {
//        return ResponseEntity.ok(viewlikeService.viewCount(memberId));
//    }
//
//    @GetMapping("/like/{memberId}")
//    public ResponseEntity<?> likeCount(@PathVariable Long memberId) {
//        return ResponseEntity.ok(viewlikeService.likeCount(memberId));
//    }
}

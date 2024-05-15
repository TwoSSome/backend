package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.HashtagDeleteReq;
import towssome.server.service.HashtagClassificationService;
import towssome.server.service.HashtagService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hashtag")
public class HashtagController {
    private final HashtagService hashtagService;
    private final HashtagClassificationService hashtagClassificationService;

    @PostMapping("/delete")
    public ResponseEntity<?> deleteHashtag(@RequestPart(value = "req") HashtagDeleteReq req) {
        hashtagService.deleteHashtag(req.reviewId(), req.hashtagId());
        return new ResponseEntity<>("Hashtag Delete Complete", HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getHashtags(@PathVariable Long reviewId) {
        return new ResponseEntity<>(hashtagClassificationService.getHashtags(reviewId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllHashtags() {
        return new ResponseEntity<>(hashtagClassificationService.getAllHashtags(), HttpStatus.OK);
    }
}

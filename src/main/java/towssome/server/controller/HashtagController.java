package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.CursorResult;
import towssome.server.dto.HashtagDeleteReq;
import towssome.server.dto.HashtagRes;
import towssome.server.dto.VirtualRes;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;
import towssome.server.exception.PageException;
import towssome.server.service.HashtagClassificationService;
import towssome.server.service.HashtagService;
import towssome.server.service.MemberService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "해시태그")
@RestController
@RequiredArgsConstructor
@RequestMapping("/hashtag")
public class HashtagController {
    private final HashtagService hashtagService;
    private final HashtagClassificationService hashtagClassificationService;
    private final MemberAdvice memberAdvice;
    private final MemberService memberService;

    @Operation(summary = "리뷰글 해시태그 삭제 API", description = "리뷰글에서 해시태그를 삭제합니다")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteHashtag(@RequestPart(value = "req") HashtagDeleteReq req) {
        hashtagService.deleteHashtag(req.reviewId(), req.hashtagId());
        return new ResponseEntity<>("Hashtag Delete Complete", HttpStatus.OK);
    }

    @Operation(summary = "리뷰글의 해시태그 API", description = "해당 리뷰글이 가지고 있는 해시태그를 가져옵니다")
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getHashtags(@PathVariable Long reviewId) {
        return new ResponseEntity<>(hashtagClassificationService.getHashtags(reviewId), HttpStatus.OK);
    }

    @Operation(summary = "모든 해시태그 API", description = "존재하는 모든 해시태그를 가져옵니다")
    @GetMapping
    public ResponseEntity<?> getAllHashtags() {
        return new ResponseEntity<>(hashtagClassificationService.getAllHashtags(), HttpStatus.OK);
    }



    @GetMapping("/search")
    public CursorResult<HashtagRes> searchHashtag(
            @RequestParam String hashtagName,
            @RequestParam int page){

        if (page <= 0) {
            throw new PageException("페이지 번호는 0보다 커야 합니다");
        }

        CursorResult<HashTag> result = hashtagService.searchHashtag(hashtagName, page, 20);

        ArrayList<HashtagRes> hashtagRes = new ArrayList<>();
        for (HashTag res : result.values()) {
            hashtagRes.add(new HashtagRes(
                    res.getId(),
                    res.getName()
            ));
        }

        return new CursorResult<>(
                hashtagRes,
                result.nextPageId(),
                result.hasNext()
        );
    }

    @Operation(summary = "해시태그 사용 순위 API", description = "가장 많이 사용된 해시태그를 15개까지 가져옵니다")
    @GetMapping("/rank")
    public List<HashtagRes> getHashtagRank(){

        return hashtagService.getHashtagRank();
    }
}

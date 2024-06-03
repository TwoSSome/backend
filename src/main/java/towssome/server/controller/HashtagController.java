package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.CursorResult;
import towssome.server.dto.HashtagDeleteReq;
import towssome.server.dto.HashtagRes;
import towssome.server.entity.HashTag;
import towssome.server.entity.Member;
import towssome.server.exception.PageException;
import towssome.server.service.HashtagClassificationService;
import towssome.server.service.HashtagService;
import towssome.server.service.MemberService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hashtag")
public class HashtagController {
    private final HashtagService hashtagService;
    private final HashtagClassificationService hashtagClassificationService;
    private final MemberAdvice memberAdvice;
    private final MemberService memberService;

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

    @GetMapping("/virtual")
    public List<HashtagRes> virtualHashtag(){

        Member jwtMember = memberAdvice.findJwtMember();
        List<HashTag> virtualTag = memberService.getVirtualTag(jwtMember);

        ArrayList<HashtagRes> hashtagRes = new ArrayList<>();
        for (HashTag hashTag : virtualTag) {
            hashtagRes.add(new HashtagRes(
                    hashTag.getId(),
                    hashTag.getName()
            ));
        }

        return hashtagRes;
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
                result.cursorId(),
                result.hasNext()
        );
    }

    @GetMapping("/rank")
    public List<HashtagRes> getHashtagRank(){

        return hashtagService.getHashtagRank();
    }
}

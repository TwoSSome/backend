package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import towssome.server.dto.CursorResult;
import towssome.server.dto.QuickRecommendReq;
import towssome.server.dto.ReviewSimpleRes;
import towssome.server.service.HashtagClassificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recommend")
public class QuickRecommendController {
    private static final int DEFAULT_SIZE = 5;
    private final HashtagClassificationService hashtagClassificationService;

    @PostMapping("/result")
    public CursorResult<ReviewSimpleRes> QuickRecommend(@RequestBody QuickRecommendReq req,
                                                        @RequestParam(value = "cursorId", required = false) Long cursorId,
                                                        @RequestParam(value = "size", required = false) Integer size){
        if (size == null) size = DEFAULT_SIZE;
        return hashtagClassificationService.getRecommendPageByHashtag(req, cursorId, "desc", PageRequest.of(0,size));
    }
}

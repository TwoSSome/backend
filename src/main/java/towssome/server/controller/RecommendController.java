package towssome.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.exception.PageException;
import towssome.server.service.RecommendService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recommend")
@Tag(name = "추천")
public class RecommendController {
    private final MemberAdvice memberAdvice;
    private final RecommendService recommendService;

    @GetMapping("/profile")
    public CursorResult<ProfileRes> getRecommendProfile(
        @RequestParam int cursorId,
        @RequestParam(required = false) Integer size){


        if (cursorId < 1) {
            throw new PageException("페이지는 1보다 작을 수 없습니다!!");
        }

        if(size == null) size = 5;
        Member jwtMember = memberAdvice.findJwtMember();
        return recommendService.getRecommendProfilePage(jwtMember, cursorId -1, size);
    }
}

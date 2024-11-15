package towssome.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.exception.PageException;
import towssome.server.service.MemberService;
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
        @RequestParam(value = "cursorId") int cursorId,
        @RequestParam(value = "size" ,required = false) Integer size){


        if (cursorId < 1) {
            throw new PageException("페이지는 1보다 작을 수 없습니다!!");
        }

        if(size == null) size = 5;
        Member jwtMember = memberAdvice.findJwtMember();
        return recommendService.getRecommendProfilePage(jwtMember, cursorId -1, size);
    }

    @GetMapping("/review")
    public CursorResult<ReviewSimpleRes> getRecommendedReviews(@RequestParam(value = "cursorId", defaultValue = "1") int cursorId, @RequestParam(value = "size", defaultValue = "10") Integer size){

        if (cursorId <= 0) {
            throw new PageException("페이지 번호는 0보다 커야 합니다");
        }
        Member jwtMember = memberAdvice.findJwtMember();
        return recommendService.getRecommendedReview(jwtMember, cursorId, size);
    }


}

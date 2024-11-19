package towssome.server.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.exception.PageException;
import towssome.server.service.RecommendService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/recommend")
@Tag(name = "추천")
public class RecommendController {
    private final MemberAdvice memberAdvice;
    private final RecommendService recommendService;

    @GetMapping("/profile")
    public CursorResult<ProfileRes_> getRecommendProfile(
        @RequestParam(value = "cursorId",defaultValue = "1") int cursorId,
        @RequestParam(value = "size" ,required = false, defaultValue = "5") Integer size){


        if (cursorId < 1) {
            throw new PageException("페이지는 1보다 작을 수 없습니다!!");
        }

        Member jwtMember = memberAdvice.findJwtMember();
        return recommendService.getRecommendProfilePage(jwtMember, cursorId -1, size);
    }

    @GetMapping("/review")
    public CursorResult<ReviewSimpleRes> getRecommendedReviews(@RequestParam(value = "cursorId", defaultValue = "1") int cursorId,
                                                               @RequestParam(value = "size", defaultValue = "10") Integer size){

        if (cursorId <= 0) {
            throw new PageException("페이지 번호는 0보다 커야 합니다");
        }
        Member jwtMember = memberAdvice.findJwtMember();
        return recommendService.getRecommendedReview(jwtMember, cursorId, size);
    }

    @GetMapping("/searchtag")
    public ListResultRes<List<String>> keywordSearch(@RequestParam(value = "size", required = false, defaultValue = "15")Integer size, HttpServletRequest request){
        String accessToken = request.getHeader("access");
        return recommendService.getSearchRecommendTags(size, accessToken);
    }

    @GetMapping("/failsearchtag")
    public RecommendTagRes keywordSearchFail(HttpServletRequest request){
        String accessToken = request.getHeader("access");
        return recommendService.getFailSearchRecommendTag(accessToken);
    }

}

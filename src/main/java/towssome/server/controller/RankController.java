package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.dto.ProfileRes;
import towssome.server.dto.RankerRes;
import towssome.server.repository.MemberRepository;
import towssome.server.service.MemberService;

import java.util.List;

@Tag(name = "랭킹", description = "월요일 오전 1시마다 랭킹 초기화")
@RestController
@RequiredArgsConstructor
public class RankController {

    private final MemberService memberService;

    @Operation(summary = "랭커 조회 API", description = "랭킹 포인트가 가장 높은 10개 계정 반환")
    @GetMapping("/rank")
    public List<RankerRes> getRank(){

        return memberService.getRanker();
    }

}

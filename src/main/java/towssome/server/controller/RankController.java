package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.dto.ProfileRes;
import towssome.server.dto.RankerRes;
import towssome.server.repository.MemberRepository;
import towssome.server.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RankController {

    private final MemberService memberService;

    @GetMapping("/rank")
    public List<RankerRes> getRank(){

        return memberService.getRanker();
    }

}

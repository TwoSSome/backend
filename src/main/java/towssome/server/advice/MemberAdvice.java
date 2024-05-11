package towssome.server.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import towssome.server.entity.Member;
import towssome.server.service.MemberService;

@RequiredArgsConstructor
@Component
public class MemberAdvice {

    private final MemberService memberService;

    public Member findJwtMember() {
        return memberService.getMemberUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }


}

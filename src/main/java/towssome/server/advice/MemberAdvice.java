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

    /**
     * 로그인한 멤버를 찾아주는 편의 메소드
     * 비회원일 경우 null 반환
     */
    public Member findJwtMember() {

        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return null;
        }else {
            return memberService.getMember(
                    SecurityContextHolder.getContext().getAuthentication().getName());
        }

    }


}

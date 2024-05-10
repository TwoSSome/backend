package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.entity.Member;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

    public Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

}

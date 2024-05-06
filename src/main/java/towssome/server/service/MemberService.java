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

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

}

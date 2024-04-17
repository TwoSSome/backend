package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.JoinReq;
import towssome.server.entity.Member;
import towssome.server.exception.DuplicateIdException;
import towssome.server.exception.NotFoundMemberException;
import towssome.server.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(JoinReq joinReq) {

        if (memberRepository.existsMemberByMemberUsingId(joinReq.memberUsingId())) {
            throw new DuplicateIdException("중복된 ID");
        }

        Member member = new Member(
                joinReq.memberUsingId(),
                joinReq.passwd(),
                joinReq.nickName(),
                0,
                null);

        memberRepository.save(member);

    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new NotFoundMemberException("해당 멤버가 없습니다"));
    }

}

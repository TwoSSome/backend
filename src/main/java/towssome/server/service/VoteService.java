package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.*;
import towssome.server.entity.*;
import towssome.server.repository.VoteAttributeMemberRepository;
import towssome.server.repository.VoteAttributeRepository;
import towssome.server.repository.VoteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteAttributeRepository voteAttributeRepository;
    private final VoteAttributeMemberRepository voteAttributeMemberRepository;

    public Vote createVote(VoteSaveDTO dto) throws IOException {
        Vote vote = new Vote(
                dto.title(),
                dto.communityPost()
        );
        voteRepository.save(vote);
        for (VoteAttributeDTO req : dto.voteAttributeReqs()) {
            voteAttributeRepository.save(
                    new VoteAttribute(
                            req.title(),
                            vote,
                            req.photo()
                    )
            );
        }
        return vote;
    }

    public VoteRes getVoteRes(CommunityPost communityPost) {
        Vote vote = communityPost.getVote().orElse(null);
        if (vote == null) {
            return null;
        }
        List<VoteAttribute> voteAttributes = vote.getVoteAttributes();
        ArrayList<VoteAttributeRes> voteAttributeRes = new ArrayList<>();
        for (VoteAttribute voteAttribute : voteAttributes) {
            List<VoteAttributeMember> voteAttributeMembers = voteAttribute.getVoteAttributeMembers();
            voteAttributeRes.add(new VoteAttributeRes(
                    voteAttribute.getTitle(),
                    getVoteMembers(voteAttributeMembers),
                    voteAttribute.getPhoto().getS3Path(),
                    voteAttribute.getCount()
                    ));
        }
        return new VoteRes(vote.getTitle(), voteAttributeRes);
    }

    /**
     * 요소에 투표하면, 해당 멤버가 voteAttributeMember 가 되고, voteAttribute 의 카운트 1 증가
     * @param member
     * @param voteAttribute
     */
    @Transactional
    public void doVote(Member member, VoteAttribute voteAttribute) {
        VoteAttributeMember voteAttributeMember = new VoteAttributeMember(
                voteAttribute,
                member
        );
        voteAttribute.changeCount(1L);
        voteAttributeMemberRepository.save(voteAttributeMember);
    }

    /**
     * 투표 취소, 해당 VoteAttributeMember 삭제 , voteAttribute 의 카운트 1 감소
     * @param voteAttribute
     */
    @Transactional
    public void cancelVote(Member member,VoteAttribute voteAttribute) {
        VoteAttributeMember voteMember = voteAttributeMemberRepository.findByMember(member);
        voteAttributeMemberRepository.delete(voteMember);
        voteAttribute.changeCount(-1L);
    }

    public Vote getVote(CommunityPost communityPost) {
        return voteRepository.findByCommunityPost(communityPost).orElse(null);
    }

    public void deleteAttribute(VoteAttribute voteAttribute) {
        voteAttributeRepository.delete(voteAttribute);
    }

    public VoteAttribute getVoteAttribute(Long id) {
        return voteAttributeRepository.findById(id).orElseThrow();
    }

    private List<VoteMember> getVoteMembers(List<VoteAttributeMember> voteAttributeMembers) {
        List<VoteMember> voteMembers = new ArrayList<>();
        for (VoteAttributeMember voteAttributeMember : voteAttributeMembers) {
            voteMembers.add(new VoteMember(voteAttributeMember.getId(), voteAttributeMember.getMember().getNickName()));
        }
        return voteMembers;
    }


}

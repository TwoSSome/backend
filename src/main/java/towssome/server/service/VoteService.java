package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                dto.title()
        );
        vote.changeCommunityPost(dto.communityPost());
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

    public VoteRes getVote(CommunityPost communityPost) {
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
                    voteAttribute.getPhoto().getS3Path()));
        }
        return new VoteRes(vote.getTitle(), voteAttributeRes);
    }

    /**
     * 요소에 투표
     * @param member
     * @param voteAttribute
     */
    public void doVote(Member member, VoteAttribute voteAttribute) {

    }

    /**
     * 이미 투표되어있으면 취소
     * @param member
     * @param voteAttribute
     */
    public void cancelVote(Member member, VoteAttribute voteAttribute) {

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

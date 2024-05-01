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

    public VoteRes getVote(CommunityPost communityPost) {
        Vote vote = voteRepository.findByCommunityPost(communityPost).orElse(null);
        if (vote == null) {
            return null;
        }
        List<VoteAttribute> voteAttributes = voteAttributeRepository.findAllByVote(vote);
        ArrayList<VoteAttributeRes> voteAttributeRes = new ArrayList<>();
        for (VoteAttribute voteAttribute : voteAttributes) {
            List<VoteAttributeMember> list = voteAttributeMemberRepository.findAllByVoteAttribute(voteAttribute);
            voteAttributeRes.add(new VoteAttributeRes(voteAttribute.getTitle(), getVoteMembers(list), voteAttribute.getPhoto().getS3Path()));
        }
        return new VoteRes(vote.getTitle(),voteAttributeRes);
    }

    private List<VoteMember> getVoteMembers(List<VoteAttributeMember> voteAttributeMembers) {
        List<VoteMember> voteMembers = new ArrayList<>();
        for (VoteAttributeMember voteAttributeMember : voteAttributeMembers) {
            voteMembers.add(new VoteMember(voteAttributeMember.getId(), voteAttributeMember.getMember().getNickName()));
        }
        return voteMembers;
    }


}

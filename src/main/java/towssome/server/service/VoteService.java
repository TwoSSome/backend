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
        Vote vote = voteRepository.findByCommunityPost(communityPost).orElseThrow();
        List<VoteAttribute> voteAttributes = voteAttributeRepository.findAllByVote(vote);
        ArrayList<VoteMember> voteMembers = new ArrayList<>();
        for (VoteAttribute voteAttribute : voteAttributes) {
            List<VoteAttributeMember> list = voteAttributeMemberRepository.findAllByVoteAttribute(voteAttribute);

        }
    }

    private VoteMember getVoteMember(VoteAttributeMember voteAttributeMember) {
        return new VoteMember(
                voteAttributeMember.getMember().getId(),
                voteAttributeMember.getMember().getNickName()
        );
    }


}

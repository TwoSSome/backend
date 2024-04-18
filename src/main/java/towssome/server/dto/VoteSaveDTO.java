package towssome.server.dto;

import towssome.server.entity.CommunityPost;

import java.util.List;

public record VoteSaveDTO(
        String title,
        CommunityPost communityPost,
        List<VoteAttributeDTO> voteAttributeReqs
) {
}

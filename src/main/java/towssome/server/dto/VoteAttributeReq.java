package towssome.server.dto;

import towssome.server.entity.Member;

import java.util.List;

public record VoteAttributeReq(
        String title,
        List<VoteMember> voteMembers,
        String s3PhotoPath
) {
}

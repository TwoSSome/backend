package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.*;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;
import towssome.server.repository.ProfileTagRepository;
import towssome.server.repository.member.MemberRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileTagService {

    private final ProfileTagRepository profileTagRepository;
    private final MemberRepository memberRepository;

    public Map<Long, AllMemberProfileTagRes> getAllMembersWithTags() {
        Map<Long, AllMemberProfileTagRes> memberTagsMap = new HashMap<>();

        List<Member> allMembers = memberRepository.findAll();

        for (Member member : allMembers) {
            List<ProfileTag> tags = profileTagRepository.findAllByMember(member);

            List<String> tagNames = tags.stream()
                    .map(tag -> tag.getHashTag().getName())
                    .collect(Collectors.toList());

            AllMemberProfileTagRes response = new AllMemberProfileTagRes(tagNames);
            memberTagsMap.put(member.getId(), response);
        }

        return memberTagsMap;
    }

}
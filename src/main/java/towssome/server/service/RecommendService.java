package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.CursorResult;
import towssome.server.dto.ProfileRes;
import towssome.server.entity.Cluster;
import towssome.server.entity.Member;
import towssome.server.entity.ProfileTag;
import towssome.server.repository.ClusterRepository;
import towssome.server.repository.ProfileTagRepository;
import towssome.server.repository.member.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {
    private final MemberRepository memberRepository;
    private final ClusterRepository clusterRepository;
    private final ProfileTagRepository profileTagRepository;

    @Transactional
    public CursorResult<ProfileRes> getRecommendProfilePage(Member jwtMember, int page, int size) {
        Cluster memberCluster = clusterRepository.findByMemberId(jwtMember.getId());
        List<Member> clusteredMembers = getMembersByClusterNum(memberCluster.getClusterNum());

        List<Long> memberIds = clusteredMembers.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        Iterable<Long> iterableMemberIds = memberIds;

        List<Member> membersPage = memberRepository.findAllById(iterableMemberIds)
                .stream()
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());

        List<ProfileRes> profileResList = new ArrayList<>();

        for (Member member : membersPage) {
            String profilePhotoPath = member.getProfilePhoto() == null ? null : member.getProfilePhoto().getS3Path();

            List<ProfileTag> tags = profileTagRepository.findAllByMember(member);
            List<String> hashtags = new ArrayList<>();

            for (ProfileTag profileTag : tags) {
                hashtags.add(profileTag.getHashTag().getName());
            }

            profileResList.add(new ProfileRes(
                    profilePhotoPath,
                    member.getNickName(),
                    hashtags,
                    member.getId()
            ));
        }

        return new CursorResult<>(
                profileResList,
                (long) page + 2,
                membersPage.size() == size
        );
    }


    public List<Member> getMembersByClusterNum(Long clusterNum) {
        List<Cluster> clusters = clusterRepository.findByClusterNum(clusterNum);
        return clusters.stream()
                .map(Cluster::getMember)
                .collect(Collectors.toList());
    }
}

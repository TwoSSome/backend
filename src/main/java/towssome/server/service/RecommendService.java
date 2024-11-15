package towssome.server.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.*;
import towssome.server.entity.*;
import towssome.server.repository.cluster.ClusterRepository;
import towssome.server.repository.ProfileTagRepository;
import towssome.server.repository.member.MemberRepository;
import towssome.server.repository.reviewpost.ReviewPostRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendService {
    private final MemberRepository memberRepository;
    private final ClusterRepository clusterRepository;
    private final ProfileTagRepository profileTagRepository;
    private final ReviewPostRepository reviewPostRepository;
    private final HashtagClassificationService hashtagClassificationService;
    private final ViewlikeService viewlikeService;
    private final PhotoAdvice photoAdvice;

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

    @Transactional
    public CursorResult<ReviewSimpleRes> getRecommendedReview(Member member, int page, int size) {
        CursorResult<ReviewPost> recommendedReviewList = reviewPostRepository.getRecommendedReviewsPage(member, PageRequest.of(page-1, size));

        ArrayList<ReviewSimpleRes> reviewSimpleRes = new ArrayList<>();
        for (ReviewPost value : recommendedReviewList.values()) {
            String profilePhotoPath = value.getMember().getProfilePhoto() != null ?
                    value.getMember().getProfilePhoto().getS3Path() :
                    null;

            List<PhotoInPost> photoS3Path = photoAdvice.getPhotoS3Path(value);
            String bodyPhoto = photoS3Path.isEmpty() ? null : photoS3Path.get(0).photoPath();

            List<HashtagRes> hashtags = new ArrayList<>();
            for(Tuple tuple : hashtagClassificationService.getHashtags(value.getId())) {
                hashtags.add(new HashtagRes(
                        tuple.get(0, Long.class),
                        tuple.get(1, String.class)
                ));
            }
            reviewSimpleRes.add(new ReviewSimpleRes(
                    value.getId(),
                    value.getBody(),
                    profilePhotoPath,
                    value.getMember().getNickName(),
                    bodyPhoto,
                    value.getReviewType(),
                    hashtags,
                    viewlikeService.getLikeAmountInReviewPost(value.getId())
            ));
        }

        return new CursorResult<>(
                reviewSimpleRes,
                (long) page +1,
                recommendedReviewList.hasNext()
        );
    }
}
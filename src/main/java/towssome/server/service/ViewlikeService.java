package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.ViewLikeReq;
import towssome.server.entity.Member;
import towssome.server.entity.ViewLike;
import towssome.server.repository.MemberRepository;
import towssome.server.repository.ReviewPostRepository;
import towssome.server.repository.ViewLikeRepository;

@Service
@RequiredArgsConstructor
public class ViewlikeService {
    private final ViewLikeRepository viewLikeRepository;
    private final MemberRepository memberRepository;
    private final ReviewPostRepository reviewPostRepository;

    // 조회 기록 저장(최초 조회 시)
    public void view(ViewLikeReq req) {
        Member member = memberRepository.findById(req.memberId()).orElseThrow();
        ViewLike viewLike = new ViewLike(
                reviewPostRepository.findById(req.reviewId()).orElseThrow(),
                member,
                true,
                false
        );
        viewLikeRepository.save(viewLike);
    }

    public void like(ViewLikeReq req) {
        Member member = memberRepository.findById(req.memberId()).orElseThrow();
        ViewLike viewLike = viewLikeRepository.findByReviewPostIdAndMemberId(req.reviewId(), req.memberId());
        if(viewLike == null) {
            viewLike = new ViewLike(
                    reviewPostRepository.findById(req.reviewId()).orElseThrow(),
                    member,
                    true,
                    true
            );
        }
        else {
            viewLike.setLike();
        }
        viewLikeRepository.save(viewLike);
    }
}

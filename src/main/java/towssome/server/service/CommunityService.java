package towssome.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.advice.PhotoAdvice;
import towssome.server.dto.*;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Photo;
import towssome.server.entity.Vote;
import towssome.server.entity.VoteAttribute;
import towssome.server.exception.NotFoundCommunityPostException;
import towssome.server.repository.community_post.CommunityPostRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final PhotoAdvice photoAdvice;
    private final VoteService voteService;

    public Long create(CommunityPostSaveDTO dto) {
        CommunityPost communityPost = new CommunityPost(
                dto.title(),
                dto.body(),
                dto.isAnonymous(),
                dto.member(),
                dto.reviewPost()
        );
        communityPostRepository.save(communityPost);
        return communityPost.getId();
    }

    public CommunityPost findPost(Long id) {
        return communityPostRepository.findById(id).orElseThrow(() ->
                new NotFoundCommunityPostException("해당 커뮤니티 글이 존재하지 않습니다"));
    }

    @Transactional
    public void updatePost(CommunityPostUpdateDto dto) {
        CommunityPost post = findPost(dto.id());
        post.update(
                dto.title(),
                dto.body(),
                dto.quotation()
        );
    }

    @Transactional
    public void deletePost(CommunityPost post) {
        ArrayList<Photo> deletePhotoList = new ArrayList<>();
        Vote vote = voteService.getVote(post);
        if (vote != null) {
            List<VoteAttribute> voteAttributes = vote.getVoteAttributes();
            for (VoteAttribute voteAttribute : voteAttributes) {
                if (voteAttribute.getPhoto() != null)
                    deletePhotoList.add(voteAttribute.getPhoto());
            }
        }
        photoAdvice.deletePhotos(post);
        communityPostRepository.delete(post);
        photoAdvice.deletePhotos(deletePhotoList);
    }


    public Page<CommunityPost> getSearchCommunity(CommunitySearchCondition cond, int page, int size) {
        return communityPostRepository.pagingCommunityPostSearch(
                cond, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"createDate")));
    }

}

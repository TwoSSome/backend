package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.CommunityPostUpdateReq;
import towssome.server.entity.CommunityPost;
import towssome.server.repository.CommunityPostRepository;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;

    public Long create(CommunityPostSaveDTO dto) {
        CommunityPost communityPost = new CommunityPost(
                dto.title(),
                dto.body(),
                dto.member(),
                dto.reviewPost()
        );
        communityPostRepository.save(communityPost);
        return communityPost.getId();
    }

    public CommunityPost findPost(Long id) {
        return communityPostRepository.findById(id).orElseThrow();
    }

    public void updatePost(CommunityPostUpdateReq dto) {

    }

}

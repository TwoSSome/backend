package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.*;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Photo;
import towssome.server.repository.ReviewPostRepository;
import towssome.server.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final Long testMemberId = 1L;

    private final CommunityService communityService;
    private final ReviewPostRepository reviewPostRepository; //나중에 ReviewService로 수정 필요
    private final MemberService memberService;
    private final PhotoService photoService;
    private final VoteService voteService;

    /**
     * 커뮤니티글 생성
     * @param CommunityPostSaveReq
     * @return 200 code or NotFoundCommunityPostException
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody CommunityPostSaveReq req) throws IOException {

        Long createdPost = communityService.create(
                new CommunityPostSaveDTO(
                        req.title(),
                        req.body(),
                        reviewPostRepository.findById(req.reviewPostId()).orElseThrow(),
                        memberService.findMember(testMemberId)
                )
        );
        CommunityPost post = communityService.findPost(createdPost);
        //커뮤니티글의 사진 저장
        photoService.saveCommunityPhoto(req.files(),post);

        //커뮤니티글의 투표 저장, 투표 내의 속성의 사진들도 저장
        ArrayList<VoteAttributeDTO> voteAttributeDTOS = new ArrayList<>();
        for (VoteAttributeReq vbr : req.voteSaveReq().voteAttributeReqs()) {
            Photo photo = photoService.savePhoto(vbr.file());
            VoteAttributeDTO voteAttributeDTO = new VoteAttributeDTO(
                    vbr.title(),
                    photo
            );
            voteAttributeDTOS.add(voteAttributeDTO);
        }
        voteService.createVote(new VoteSaveDTO(
                req.voteSaveReq().title(),
                post,
                voteAttributeDTOS
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 업데이트 할때 사용, 인용글이 바뀌었는지, 사진이 바뀌었는지 모두 확인해야함
     * @param id
     * @param CommunityPostUpdateReq
     * @return 200code or NotFoundCommunityPostException
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePost (@PathVariable Long id, @RequestBody CommunityPostUpdateReq req) throws IOException {
        List<Long> deletedPhotoIds = req.deletedPhotoId();
        for (Long deletedPhotoId : deletedPhotoIds) {
            photoService.deletePhoto(deletedPhotoId);
        }
        List<MultipartFile> multipartFiles = req.newPhotos();
        CommunityPost post = communityService.findPost(id);
        photoService.saveCommunityPhoto(multipartFiles, post);
        CommunityPostUpdateDto dto = new CommunityPostUpdateDto(
                id,
                req.title(),
                req.body(),
                reviewPostRepository.findById(req.reviewPostId()).orElseThrow() //Service에서 가져오는 것으로 수정 필요
        );
        communityService.updatePost(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 커뮤니티글 삭제
     * @param id
     * @return 200code or NotFoundCommunityPostException
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id){
        CommunityPost post = communityService.findPost(id);
        photoService.deletePhotos(post);
        communityService.deletePost(post);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * post 조회
     * @param id
     * @return CommunityPostRes
     */
    @GetMapping("/post/{id}")
    public CommunityPostRes getPost(@PathVariable Long id){
        CommunityPost post = communityService.findPost(id);
        List<PhotoInPost> photoS3Paths = photoService.getPhotoS3Path(post);
        return new CommunityPostRes(
                post.getTitle(),
                post.getBody(),
                post.getCreateDate(),
                post.getLatsModifiedDate(),
                photoS3Paths,
                post.getQuotation().getId(),
                null
        );
    }

}

package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.CommunityPost;
import towssome.server.entity.Photo;
import towssome.server.entity.ReviewPost;
import towssome.server.service.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    private final ReviewPostService reviewPostService;
    private final MemberService memberService;
    private final PhotoService photoService;
    private final VoteService voteService;
    private final MemberAdvice memberAdvice;
    private static final int DEFAULT_SIZE = 10;

    /**
     * 커뮤니티글 리스트 조회 (검색 가능)
     * @param title
     * @param nickname
     * @param page >= 1
     * @return
     */
    @GetMapping
    public PageResult<CommunityListRes> getCommunityList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String nickname,
            @RequestParam int page){
        Page<CommunityPost> result = communityService.getSearchCommunity(
                new CommunitySearchCondition(title,nickname), page-1, DEFAULT_SIZE);
        ArrayList<CommunityListRes> communityListRes = new ArrayList<>();
        for (CommunityPost communityPost : result.getContent()) {
            communityListRes.add(new CommunityListRes(
                    communityPost.getTitle(),
                    communityPost.getBody(),
                    communityPost.getId()
            ));
        }

        return new PageResult<>(
                communityListRes, // content
                result.getTotalElements(), // 총 게시글 수
                result.getTotalPages(), // 총 페이지
                result.getNumber()+1, // 현재 페이지 (1부터 시작)
                DEFAULT_SIZE // 페이지당 게시글 수
        );
    }

    /**
     * 커뮤니티글 생성
     * @param CommunityPostSaveReq
     * @return 200 code or NotFoundCommunityPostException
     * @RequestPart 멀티파트파일과 json 을 동시에 받으려면 사용해야함
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestPart CommunityPostSaveReq req, @RequestPart(required = false) List<MultipartFile> files) throws IOException {

        ReviewPost quotation = null;
        if (req.reviewPostId() != null) {
            quotation = reviewPostService.getReview(req.reviewPostId());
        }

        Long createdPost = communityService.create(
                new CommunityPostSaveDTO(
                        req.title(),
                        req.body(),
                        quotation,
                        memberAdvice.findJwtMember()
                )
        );
        CommunityPost post = communityService.findPost(createdPost);
        //커뮤니티글의 사진 저장
        photoService.saveCommunityPhoto(files,post);

        //커뮤니티글의 투표 저장, 투표 내의 속성의 사진들도 저장
        if (req.voteSaveReq() != null) { // 투표가 없는 커뮤니티글이 있을수도 있으니까 null 체크
            ArrayList<VoteAttributeDTO> voteAttributeDTOS = new ArrayList<>();
            for (VoteAttributeReq vbr : req.voteSaveReq().voteAttributeReqs()) {
                Photo photo = photoService.saveVotePhoto(vbr.file());
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
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 업데이트 할때 사용, 인용글이 바뀌었는지, 사진이 바뀌었는지 모두 확인해야함, 투표는 수정 불가능이 좋음
     * @param id
     * @param CommunityPostUpdateReq
     * @return 200code or NotFoundCommunityPostException
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePost (
            @PathVariable Long id,
            @RequestPart CommunityPostUpdateReq req,
            @RequestPart List<MultipartFile> newFiles) throws IOException {
        List<Long> deletedPhotoIds = req.deletedPhotoId();
        for (Long deletedPhotoId : deletedPhotoIds) {
            photoService.deletePhoto(deletedPhotoId);
        }
        CommunityPost post = communityService.findPost(id);
        photoService.saveCommunityPhoto(newFiles, post);

        ReviewPost quotation = null;
        if (req.reviewPostId() != null) {
            quotation = reviewPostService.getReview(req.reviewPostId());
        }

        CommunityPostUpdateDto dto = new CommunityPostUpdateDto(
                id,
                req.title(),
                req.body(),
                quotation
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
        VoteRes voteRes = voteService.getVote(post);
        ReviewPost quotation = post.getQuotation().orElse(null);
        Long quotationId = null;
        if (quotation != null) {
            quotationId = quotation.getId();
        }

        List<PhotoInPost> photoS3Paths = photoService.getPhotoS3Path(post);
        return new CommunityPostRes(
                post.getTitle(),
                post.getBody(),
                post.getCreateDate(),
                post.getLatsModifiedDate(),
                photoS3Paths,
                quotationId,
                voteRes
        );
    }

}

package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.BookMark;
import towssome.server.entity.Category;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.service.BookMarkService;
import towssome.server.service.MemberService;
import towssome.server.service.PhotoService;
import towssome.server.service.ReviewPostService;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "북마크")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmark")
public class BookMarkController {

    private final BookMarkService bookMarkService;
    private final MemberService memberService;
    private final ReviewPostService reviewPostService;
    private final PhotoService photoService;

    /**
     * 북마크 추가
     * @param req
     * @return
     */
    @Operation(summary = "카테고리에 북마크 추가 API", parameters = @Parameter(name = "req", description = "추가할 카테고리 / 추가할 북마크"))
    @PostMapping("/add")
    public ResponseEntity<?> addBookmark(@RequestBody AddBookmarkReq req){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMember(name);
        ReviewPost review = reviewPostService.getReview(req.reviewPostId());
        Category category = bookMarkService.getCategory(req.categoryId());

        bookMarkService.addBookMark(review, category);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 북마크 삭제
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long id){

        bookMarkService.deleteBookMark(bookMarkService.getBookMark(id));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 카테고리별 북마크 조회
     * @param id
     * @param page
     * @param size
     * @return
     */
    @Operation(summary = "카테고리별 북마크 조회 API", parameters = {
            @Parameter(name = "id", description = "조회할 카테고리"),
            @Parameter(name = "page", description = "조회 페이지, >= 1")})
    @GetMapping("/{id}")
    public CursorResult<BookmarkReq> getBookmark(
            @PathVariable Long id,
            @RequestParam int page){
        ArrayList<BookmarkReq> bookmarkReqs = new ArrayList<>();
        Slice<BookMark> bookMarks = bookMarkService.getCategoryBookMarks(bookMarkService.getCategory(id), page, 20);
        for (BookMark bookMark : bookMarks) {
            List<PhotoInPost> photos = photoService.getPhotoS3Path(bookMark.getReviewPost());
            String photoPath = (photos != null && !photos.isEmpty()) ? photos.get(0).photoPath() : null;
            bookmarkReqs.add(new BookmarkReq(
                    bookMark.getId(),
                    bookMark.getReviewPost().getId(),
                    bookMark.getReviewPost().getBody(),
                    photoPath
            ));
        }
        return new CursorResult<>(
                bookmarkReqs, null,
                bookMarks.hasNext()
        );
    }

}

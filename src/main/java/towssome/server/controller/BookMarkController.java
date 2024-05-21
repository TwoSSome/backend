package towssome.server.controller;

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
    @GetMapping("/{id}")
    public CursorResult<BookmarkReq> getBookmark(@PathVariable Long id, int page, int size){
        ArrayList<BookmarkReq> bookmarkReqs = new ArrayList<>();
        Slice<BookMark> bookMarks = bookMarkService.getCategoryBookMarks(bookMarkService.getCategory(id), page, size);
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
                bookMarkService.getCategoryBookMarks(bookMarkService.getCategory(id), page, size).hasNext()
        );
    }

}

package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import towssome.server.dto.AddBookmarkReq;
import towssome.server.dto.CreateCategoryDTO;
import towssome.server.dto.UpdateCategoryDTO;
import towssome.server.dto.UpdateCategoryReq;
import towssome.server.entity.BookMark;
import towssome.server.entity.Category;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.service.BookMarkService;
import towssome.server.service.MemberService;
import towssome.server.service.ReviewPostService;

@RestController
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;
    private final MemberService memberService;
    private final ReviewPostService reviewPostService;

    @PostMapping("/category/create")
    public ResponseEntity<?> createCategory(@RequestBody String categoryName){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMember(name);

        bookMarkService.createCategory(new CreateCategoryDTO(
                member,
                categoryName
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/category/delete")
    public ResponseEntity<?> deleteCategory(@RequestBody Long id){
        Category category = bookMarkService.getCategory(id);
        bookMarkService.deleteCategory(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/category/update")
    public ResponseEntity<?> updateCategory(@RequestBody UpdateCategoryReq req){

        bookMarkService.updateCategory(new UpdateCategoryDTO(
                bookMarkService.getCategory(req.id()),
                req.updateName()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/bookmark/add")
    public ResponseEntity<?> addBookmark(@RequestBody AddBookmarkReq req){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberService.getMember(name);
        ReviewPost review = reviewPostService.getReview(req.reviewPostId());
        Category category = bookMarkService.getCategory(req.categoryId());

        bookMarkService.addBookMark(review, category);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/bookmark/delete")
    public ResponseEntity<?> deleteBookmark(@RequestBody Long bookmarkId){

        bookMarkService.deleteBookMark(bookMarkService.getBookMark(bookmarkId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

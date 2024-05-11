package towssome.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import towssome.server.advice.MemberAdvice;
import towssome.server.dto.*;
import towssome.server.entity.Category;
import towssome.server.entity.Member;
import towssome.server.service.BookMarkService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final BookMarkService bookMarkService;
    private final MemberAdvice memberAdvice;

    /**
     * 멤버 카테고리 조회
     * @return
     */
    @GetMapping
    public List<CategoryRes_> getCategoryBookmark(){
        Member jwtMember = memberAdvice.findJwtMember();
        List<Category> memberCategories = bookMarkService.getMemberCategories(jwtMember);
        ArrayList<CategoryRes_> result = new ArrayList<>();
        for (Category memberCategory : memberCategories) {
            result.add(new CategoryRes_(
                    memberCategory.getId(),
                    memberCategory.getName()
            ));
        }
        return result;
    }

    /**
     * 카테고리 생성
     * @param req
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryReq req ){

        Member member = memberAdvice.findJwtMember();

        bookMarkService.createCategory(new CreateCategoryDTO(
                member,
                req.categoryName()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 카테고리 삭제
     * @param id
     * @return
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id){
        Category category = bookMarkService.getCategory(id);
        bookMarkService.deleteCategory(category);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 카테고리 수정
     * @param req
     * @return
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateCategory(@RequestBody UpdateCategoryReq req){

        bookMarkService.updateCategory(new UpdateCategoryDTO(
                bookMarkService.getCategory(req.categoryId()),
                req.updateName()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }


}

package towssome.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "카테고리")
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
    @Operation(summary = "내 카테고리 조회 API")
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
    @Operation(summary = "카테고리 생성 API")
    @PostMapping("/create")
    public ResponseEntity<CreateRes> createCategory(@RequestBody CreateCategoryReq req ){

        Member member = memberAdvice.findJwtMember();

        Category category = bookMarkService.createCategory(new CreateCategoryDTO(
                member,
                req.categoryName()
        ));

        return new ResponseEntity<>(new CreateRes(
                category.getId()
        ),HttpStatus.OK);
    }

    /**
     * 카테고리 삭제
     * @param id
     * @return
     */
    @Operation(summary = "카테고리 삭제 API")
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
    @Operation(summary = "카테고리 수정 API", description = "카테고리 이름 수정")
    @PostMapping("/update")
    public ResponseEntity<?> updateCategory(@RequestBody UpdateCategoryReq req){

        bookMarkService.updateCategory(new UpdateCategoryDTO(
                bookMarkService.getCategory(req.categoryId()),
                req.updateName()
        ));

        return new ResponseEntity<>(HttpStatus.OK);
    }


}

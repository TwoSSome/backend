package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import towssome.server.dto.CategoryRes;
import towssome.server.dto.CreateCategoryDTO;
import towssome.server.dto.UpdateCategoryDTO;
import towssome.server.entity.BookMark;
import towssome.server.entity.Category;
import towssome.server.entity.Member;
import towssome.server.entity.ReviewPost;
import towssome.server.exception.NotFoundBookMarkException;
import towssome.server.exception.NotFoundCategoryException;
import towssome.server.repository.BookMarkRepository;
import towssome.server.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final CategoryRepository categoryRepository;
    private final BookMarkRepository bookMarkRepository;

    /**
     * 카테고리 생성
     * @param 카테고리 생성 멤버, 카테고리 이름
     * @return
     */
    public Category createCategory(CreateCategoryDTO dto) {
        Category category = new Category(
                dto.name(),
                dto.member()
        );
        return categoryRepository.save(category);
    }

    /**
     * 카테고리 찾기
     * @param 카테고리 id
     * @return
     */
    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundCategoryException("해당 카테고리가 없습니다!!"));
    }

    /**
     * 카테고리 삭제
     * @param category
     */
    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

    /**
     * 카테고리 이름 업데이트
     * @param 카테고리 id, 업데이트 이름
     */
    @Transactional
    public void updateCategory(UpdateCategoryDTO dto) {
        Category category = dto.category();
        category.updateName(dto.updateName());
    }

    /**
     * 멤버의 카테고리 전부 찾기
     * @param member
     * @return
     */
    public List<Category> getMemberCategories(Member member) {
        return categoryRepository.findAllByMember(member);
    }

    /**
     * 멤버의 카테고리들 + 카테고리 안의 북마크 전부 찾기
     * @param member
     * @return 카테고리 id + 카테고리 이름 + 리뷰 id
     */
    public List<CategoryRes> getProfileCategory(Member member) {
        ArrayList<CategoryRes> categoryRes = new ArrayList<>();
        List<Category> memberCategories = getMemberCategories(member);
        for (Category memberCategory : memberCategories) {

            ArrayList<Long> reviewPostIds = new ArrayList<>(); //북마크한 리뷰들의 id를 담아줌
            List<BookMark> categoryBookMarks = getCategoryBookMarks(memberCategory);
            for (BookMark categoryBookMark : categoryBookMarks) {
                reviewPostIds.add(categoryBookMark.getId());
            }

            categoryRes.add(new CategoryRes(
                    memberCategory.getId(),
                    memberCategory.getName(),
                    reviewPostIds
            ));
        }

        return categoryRes;
    }

    /**
     * 북마크 추가
     * @param reviewPost
     * @param category
     * @return
     */
    public BookMark addBookMark(ReviewPost reviewPost, Category category) {
        BookMark bookMark = new BookMark(
                category,
                reviewPost
        );
        return bookMarkRepository.save(bookMark);
    }

    /**
     * 북마크 찾기
     * @param id
     * @return
     */
    public BookMark getBookMark(Long id) {
        return bookMarkRepository.findById(id).orElseThrow(() ->
                new NotFoundBookMarkException("해당 북마크가 없습니다"));
    }

    /**
     * 해당 카테고리의 모든 북마크 찾기
     * @param category
     * @return
     */
    public List<BookMark> getCategoryBookMarks(Category category) {
        return bookMarkRepository.findAllByCategory(category);
    }

    /**
     * 북마크 삭제
     * @param bookMark
     */
    public void deleteBookMark(BookMark bookMark) {
        bookMarkRepository.delete(bookMark);
    }

}

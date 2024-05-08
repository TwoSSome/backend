package towssome.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import towssome.server.dto.CreateCategoryDTO;
import towssome.server.entity.Category;
import towssome.server.exception.NotFoundCategoryException;
import towssome.server.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class BookMarkService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(CreateCategoryDTO dto) {
        Category category = new Category(
                dto.name(),
                dto.masterCategory(),
                dto.member()
        );
        category.setMasterCategory();
        return categoryRepository.save(category);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new NotFoundCategoryException("해당 카테고리가 없습니다!!"));
    }

    public void deleteCategory(Category category) {
        categoryRepository.delete(category);
    }

}

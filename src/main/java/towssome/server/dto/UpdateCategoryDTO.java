package towssome.server.dto;

import towssome.server.entity.Category;

public record UpdateCategoryDTO(
        Category category,
        String updateName
) {
}

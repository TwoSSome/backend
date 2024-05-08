package towssome.server.dto;

import towssome.server.entity.Category;
import towssome.server.entity.Member;

public record CreateCategoryDTO(
        Member member,
        String name,
        Category masterCategory
) {
}

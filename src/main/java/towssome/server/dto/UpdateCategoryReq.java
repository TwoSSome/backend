package towssome.server.dto;

public record UpdateCategoryReq(
        Long categoryId,
        String updateName
) {
}

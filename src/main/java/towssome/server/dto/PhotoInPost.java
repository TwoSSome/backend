package towssome.server.dto;

/**
 * @param id 해당 사진 객체의 id
 * @param photoPath 해당 사진의 S3 경로
 */
public record PhotoInPost(
        Long id,
        String photoPath
) {
}

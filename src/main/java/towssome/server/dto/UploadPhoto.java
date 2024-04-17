package towssome.server.dto;

/**
 * @param originalFileName 첨부된 사진의 실제 이름
 * @param saveFileName S3에 저장되는 사진의 이름 -> 왜 따로 만드느냐?
 *                     S3에서 사진의 path를 불러오려면 이름으로 불러와야 하는데,
 *                     중복되면 어느 사진을 가져올지 알 수 없다.
 * @param s3path 사진의 S3 경로
 */
public record UploadPhoto(
    String originalFileName,
    String saveFileName,
    String s3path
) {
}

package towssome.server.dto;

public record UploadPhoto(
    String originalFileName,
    String saveFileName,
    String s3path
) {
}

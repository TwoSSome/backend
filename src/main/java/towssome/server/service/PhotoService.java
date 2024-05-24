package towssome.server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.PhotoInPost;
import towssome.server.dto.UploadPhoto;
import towssome.server.entity.*;
import towssome.server.enumrated.PhotoType;
import towssome.server.exception.NotFoundPhotoException;
import towssome.server.repository.PhotoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final AmazonS3 amazonS3;
    private final PhotoRepository photoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * reviewPost에 사용되는 사진을 저장할 때 사용하는 함수
     * @param files 저장될 사진
     * @param reviewPost
     */
    public void saveReviewPhoto(List<MultipartFile> files, ReviewPost reviewPost) throws IOException {
        if (files == null) {
            return;
        }
        List<UploadPhoto> uploadPhotos = uploadPhotoList(files);
        for (UploadPhoto uploadPhoto : uploadPhotos) {
            Photo photo = new Photo(
                    uploadPhoto.originalFileName(),
                    uploadPhoto.saveFileName(),
                    uploadPhoto.s3path(),
                    PhotoType.REVIEW,
                    reviewPost,
                    null
            );
            photoRepository.save(photo);
        }
    }

    /**
     * 커뮤니티글에 사용되는 사진을 저장하는 함수
     * @param files 저장될 사진
     * @param communityPost
     */
    public void saveCommunityPhoto(List<MultipartFile> files, CommunityPost communityPost) throws IOException {
        if (files == null) {
            return;
        }
        List<UploadPhoto> uploadPhotos = uploadPhotoList(files);
        for (UploadPhoto uploadPhoto : uploadPhotos) {
            Photo photo = new Photo(
                    uploadPhoto.originalFileName(),
                    uploadPhoto.saveFileName(),
                    uploadPhoto.s3path(),
                    PhotoType.COMMUNITY,
                    null,
                    communityPost
            );
            photoRepository.save(photo);
        }
    }

    /**
     * 프로필 사진을 저장하는 함수
     * @param file
     * @return
     * @throws IOException
     */
    public Photo saveProfilePhoto(MultipartFile file) throws IOException {
        if(file == null){
            return null;
        }
        UploadPhoto uploadPhoto = uploadPhoto(file);
        Photo photo = new Photo(
                uploadPhoto.originalFileName(),
                uploadPhoto.saveFileName(),
                uploadPhoto.s3path(),
                PhotoType.PROFILE,
                null,
                null
        );
        return photoRepository.save(photo);
    }

    /**
     * 투표에 사용되는 사진을 저장하는 함수
     * @param file
     * @return
     * @throws IOException
     */
    public Photo saveVotePhoto(MultipartFile file) throws IOException {
        if (file == null) {
            return null;
        }
        UploadPhoto uploadPhoto = uploadPhoto(file);
        Photo photo = new Photo(
                uploadPhoto.originalFileName(),
                uploadPhoto.saveFileName(),
                uploadPhoto.s3path(),
                PhotoType.VOTE,
                null,
                null
        );
        photoRepository.save(photo);
        return photo;
    }

    /**
     * 해당 리뷰포스트의 사진들의 URL을 반환하는 함수
     * @param reviewPost
     * @return 사진의 URL 리스트
     */
    public List<PhotoInPost> getPhotoS3Path(ReviewPost reviewPost) {
        List<Photo> photoList = photoRepository.findAllByReviewPost(reviewPost);
//        log.info("photoList = {}", Arrays.toString(photoList.toArray()));
        List<PhotoInPost> photos = new ArrayList<>();
        for (Photo photo : photoList) {
            photos.add(new PhotoInPost(photo.getId(),photo.getS3Path()));
        }
        return photos;
    }

    /**
     * 해당 커뮤니티 글의 사진들의 URL을 반환하는 함수, 오버로딩되었음
     * @param communityPost
     * @return 사진의 URL 리스트
     */
    public List<PhotoInPost> getPhotoS3Path(CommunityPost communityPost) {
        List<Photo> photoList = photoRepository.findAllByCommunityPost(communityPost);
        ArrayList<PhotoInPost> photoInPosts = new ArrayList<>();
        for (Photo photo : photoList) {
            photoInPosts.add(new PhotoInPost(photo.getId(),photo.getS3Path()));
        }
        return photoInPosts;
    }

    /**
     * 리뷰글의 사진들을 모두 지울 때 사용
     * 반드시 사진을 먼저 지우고 리뷰글을 지워야 할 것!! 아니면 고아 객체가 만들어지고 S3의 이미지도 삭제가 불가함
     * @param reviewPost
     */
    public void deletePhotos(ReviewPost reviewPost) {
        List<Photo> photoList = photoRepository.findAllByReviewPost(reviewPost);
        for (Photo photo : photoList) {
            photoRepository.delete(photo);
            deleteS3Image(photo.getS3Name());
        }
    }

    //오버로딩된 함수
    public void deletePhotos(CommunityPost communityPost) {
        List<Photo> photoList = photoRepository.findAllByCommunityPost(communityPost);
        for (Photo photo : photoList) {
            photoRepository.delete(photo);
            deleteS3Image(photo.getS3Name());
        }
    }

    //오버로딩된 함수
    public void deletePhotos(List<Photo> photoList) {
        for (Photo photo : photoList) {
            photoRepository.delete(photo);
            deleteS3Image(photo.getS3Name());
        }
    }

    /**
     * id로 file 객체 반환
     * @param photoId
     * @return
     */
    public Photo findPhoto(Long photoId) {
        return photoRepository.findById(photoId).orElseThrow(NotFoundPhotoException::new);
    }

    /**
     * id로 file 객체 하나 삭제
     * @param photoId
     */
    public void deletePhoto(Long photoId) {
        Photo photo = photoRepository.findById(photoId).orElseThrow();
        deleteS3Image(photo.getS3Name());
        photoRepository.delete(photo);
    }

    /**
     * @param file -> 업로드될 사진
     * @return 해당 파일의 S3 URL, 파일 이름, s3에 저장될 파일 이름이 담긴 UploadPhoto 반환
     */
    private UploadPhoto uploadPhoto(MultipartFile file) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String uploadPhotoName = createUploadPhotoName(originalFilename);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucket, uploadPhotoName, file.getInputStream(), metadata);
        String fileURL = amazonS3.getUrl(bucket, uploadPhotoName).toString();


        UploadPhoto uploadPhoto = new UploadPhoto(
                originalFilename,
                uploadPhotoName,
                fileURL
        );

        return uploadPhoto;
    }

    /**
     * @param files
     * @return List<UploadPhoto>
     */
    private List<UploadPhoto> uploadPhotoList(List<MultipartFile> files) throws IOException {
        List<UploadPhoto> uploadPhotos = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadPhotos.add(uploadPhoto(file));
        }
        return uploadPhotos;
    }

    /**
     * @param s3Name
     * S3에 담겨있는 해당 파일을 지웁니다
     */
    private void deleteS3Image(String s3Name) {
        amazonS3.deleteObject(bucket,s3Name);
    }

    /**
     * s3에 중복되는 사진 이름이 들어가면 안되므로 유니크한 사진 이름을 만들어야 함
     * @param originalFileName
     * @return 유니크한 사진 이름 -> 확장자는 그대로 사용
     */
    private String createUploadPhotoName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}

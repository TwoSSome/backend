package towssome.server.advice;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import towssome.server.dto.GpsInformationDTO;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class ImageMetaDataAdvice {

    //사진 안의 GPS 정보 추출
    public GpsInformationDTO extract(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return new GpsInformationDTO(
                    null, null
            );
        }

        File file = fileConverter(multipartFile);
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory == null) {
            return new GpsInformationDTO(
                    null, null
            );
        }

        GpsInformationDTO result = null;

        if (hasGpsInformation(gpsDirectory)) {
            double longitude = gpsDirectory.getGeoLocation().getLongitude();
            double latitude = gpsDirectory.getGeoLocation().getLatitude();
            result = new GpsInformationDTO(longitude, latitude);
            log.info("latitude = {}, longitude = {}",latitude,longitude);
        }else{
            log.info("No GPS");
        }

        deleteFile(file);
        return result;
    }

    //MultipartFile -> File
    private File fileConverter(MultipartFile multipartFile) {

        File file = null;

        try {
            //임시 파일 생성
            file = File.createTempFile("temp", multipartFile.getOriginalFilename());
            //multiPartFile의 내용을 임시 파일에 전송
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    //임시 파일 삭제
    private void deleteFile(File file) {
        if (file != null && file.exists()) {
            boolean isDeleted = file.delete();
            if (isDeleted) {
                log.info("Temp file delete success");
            } else {
                log.error("Temp file delete failed");
            }
        }
    }

    //GPS 정보가 있는지 검사
    private boolean hasGpsInformation(GpsDirectory gpsDirectory) {
        return gpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE)
                && gpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE);
    }

}

package com.hanshin.supernova.s3.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.s3.S3InvalidException;
import com.hanshin.supernova.s3.application.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@Slf4j
@RestController
@RequestMapping(path = "/api/images")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Uploader s3Uploader;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String fileUrl = s3Uploader.uploadFile(image);
            if (fileUrl.isEmpty()) {
                throw new S3InvalidException(ErrorType.EMPTY_IMAGE_ERROR);
            }
            log.info("Successfully uploaded image. URL: {}", fileUrl);
            return ResponseDto.ok(fileUrl);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            throw new S3InvalidException(ErrorType.IMAGE_UPLOAD_FAILED_ERROR);
        }
    }
}

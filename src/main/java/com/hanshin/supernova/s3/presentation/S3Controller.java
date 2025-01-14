package com.hanshin.supernova.s3.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.s3.S3InvalidException;
import com.hanshin.supernova.s3.application.S3Uploader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "이미지 업로드", description = "S3를 이용한 이미지 업로드 전용 api")
    @ApiResponse(responseCode = "200", description = "이미지 업로드 성공, 이미지 url 반환")
    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String fileUrl = s3Uploader.uploadFile(image);
            if (fileUrl.isEmpty()) {
                throw new S3InvalidException(ErrorType.EMPTY_IMAGE_ERROR);
            }
            log.debug("Successfully uploaded image. URL: {}", fileUrl);
            return ResponseDto.ok(fileUrl);
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            throw new S3InvalidException(ErrorType.IMAGE_UPLOAD_FAILED_ERROR);
        }
    }
}

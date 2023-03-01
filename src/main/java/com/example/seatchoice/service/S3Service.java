package com.example.seatchoice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.seatchoice.entity.Image;
import com.example.seatchoice.exception.CustomException;
import com.example.seatchoice.type.ErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public List<String> uploadImage(List<MultipartFile> multipartFile) {
		if (CollectionUtils.isEmpty(multipartFile)) {
			return Collections.emptyList();
		}

		List<String> fileUrlList = new ArrayList<>();

		for (MultipartFile file : multipartFile) {
			String fileName = createFileName(file.getOriginalFilename());
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());

			try (InputStream inputStream = file.getInputStream()) {
				amazonS3.putObject(
					new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
						.withCannedAcl(CannedAccessControlList.PublicRead));
			} catch (IOException e) {
				if (!CollectionUtils.isEmpty(fileUrlList)) {
					deleteImage(fileUrlList);
				}
				throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL, HttpStatus.BAD_REQUEST);
			}

			fileUrlList.add(amazonS3.getUrl(bucket, fileName).toString());
		}

		return fileUrlList;
	}

	public void deleteImage(List<String> images) {
		String fileName = "";
		for (String url : images) {
			fileName = url.split("/")[3];
		}
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

	public void deleteImageFromObject(List<Image> images) {
		String fileName = "";
		for (Image img : images) {
			fileName = img.getUrl().split("/")[3];
			amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
		}
	}

	private String createFileName(String fileName) {
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		try {
			return fileName.substring(fileName.lastIndexOf("."));
		} catch (StringIndexOutOfBoundsException e) {
			throw new CustomException(ErrorCode.WRONG_FILE_FORM, HttpStatus.BAD_REQUEST);
		}
	}
}

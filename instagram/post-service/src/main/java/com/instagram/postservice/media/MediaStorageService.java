package com.instagram.postservice.media;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {

    String store(MultipartFile file);
}

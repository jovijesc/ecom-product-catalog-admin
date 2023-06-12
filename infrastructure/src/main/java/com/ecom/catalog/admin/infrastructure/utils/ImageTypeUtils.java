package com.ecom.catalog.admin.infrastructure.utils;

import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Optional;

public final class ImageTypeUtils {

    private ImageTypeUtils() {
    }

    public static String getContentType(String fileName) {
        String fileExtension = getFileExtension(fileName);

        Map<String, String> contentTypeMap = Map.of(
                "png", "image/png",
                "jpg", "image/jpeg",
                "jpeg", "image/jpeg"
        );

        return contentTypeMap.getOrDefault(fileExtension.toLowerCase(),
                "application/octet-stream");
    }

    public static MediaType getMediaType(String fileName) {
        String contentType = getContentType(fileName);
        return MediaType.parseMediaType(contentType);
    }

    private static String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1).toLowerCase())
                .orElse("");
    }
}

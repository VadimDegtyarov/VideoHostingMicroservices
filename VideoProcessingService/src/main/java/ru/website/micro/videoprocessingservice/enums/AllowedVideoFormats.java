package ru.website.micro.videoprocessingservice.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum AllowedVideoFormats {
    MP4("video/mp4", "mp4"),
    WEBM("video/webm", "webm"),
    MKV("video/x-matroska", "mkv"),
    MOV("video/quicktime", "mov");
    private final String mimeType;
    private final String extension;

    AllowedVideoFormats(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public static Optional<String> getExtension(String mimeType) {
        return Arrays.stream(values())
                .filter(el -> el.getMimeType().equals(mimeType))
                .map(AllowedVideoFormats::getExtension).findFirst();
    }

    public static Set<String> getAllVideoFormats() {
        return Arrays.stream(values())
                .map(AllowedVideoFormats::getMimeType)
                .collect(Collectors.toSet());
    }
}
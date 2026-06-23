package com.cherry.base.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;
import java.util.UUID;

/**
 * @author cherry
 */
@SuppressWarnings("NullableProblems")
public class Base64DecodeMultipartFile implements MultipartFile {
    private final byte[] imgContent;
    private final String contentType;

    public Base64DecodeMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        // 提取contentType
        this.contentType = header.split(";")[0].split(":")[1];
    }

    @Override
    public String getName() {
        // 使用UUID生成唯一文件名
        return UUID.randomUUID() + "." + getFileExtension();
    }

    @Override
    public String getOriginalFilename() {
        // 生成文件名并使用UUID来确保唯一性
        return UUID.randomUUID() + "." + getFileExtension();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException {
        // 使用try-with-resources来确保资源释放
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(imgContent);
        }
    }

    /**
     * 获取文件扩展名（例如jpg、png等）
     */
    private String getFileExtension() {
        return contentType.split("/")[1];
    }

    /**
     * base64转MultipartFile
     *
     * @param base64 base64数据
     * @return MultipartFile
     */
    public static MultipartFile base64Convert(String base64) {
        String[] baseArr = base64.split(",");
        // 使用java.util.Base64替代sun.misc.BASE64Decoder
        byte[] decodedBytes = Base64.getDecoder().decode(baseArr[1]);

        return new Base64DecodeMultipartFile(decodedBytes, baseArr[0]);
    }
}

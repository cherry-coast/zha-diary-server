package com.cherry.base.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.cherry.base.config.OssProperties;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CherryOssUtil {

    private final OssProperties ossProperties;

    /**
     * 上传图片到 OSS
     *
     * @param file 图片文件
     * @return 图片完整可访问 URL
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            originalFilename = "unknown.jpg";
        }
        
        // 提取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 生成按日期的文件夹路径和 UUID 文件名
        String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String fileName = "avatar/" + datePath + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(
                    ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret()
            );
            
            InputStream inputStream = file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            // 执行上传
            ossClient.putObject(ossProperties.getBucketName(), fileName, inputStream, objectMetadata);

            // 拼接返回 URL
            if (StringUtils.isNotBlank(ossProperties.getDomain())) {
                String domain = ossProperties.getDomain();
                if (!domain.endsWith("/")) {
                    domain += "/";
                }
                return domain + fileName;
            } else {
                // 如果没有自定义域名，使用默认 OSS 域名格式
                return "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + fileName;
            }

        } catch (Exception e) {
            log.error("OSS 文件上传失败", e);
            throw new CherryException(BaseExceptionEnum.FAIL.getErrorCode(), "文件上传失败");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

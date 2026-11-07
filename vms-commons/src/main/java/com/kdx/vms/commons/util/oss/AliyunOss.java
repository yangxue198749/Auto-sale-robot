package com.kdx.vms.commons.util.oss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 本地文件存储演示（原阿里云 OSS 官方示例，OSS 停用后改造）。
 *
 * 历史上演示 OSSClient 的 bucket/object 操作；现委托 {@link AliyunOssUtils}
 * 走本地磁盘，仅保留 main 作为冒烟入口，无业务调用方。
 */
public class AliyunOss {

    static Logger logger = LoggerFactory.getLogger(AliyunOss.class);

    public static void main(String[] args) {
        logger.info("Started (local storage mode)");
        String key = "demo/my-first-key.txt";

        try (InputStream is = new ByteArrayInputStream("Hello VMS local storage".getBytes(StandardCharsets.UTF_8))) {
            boolean uploaded = AliyunOssUtils.upload(key, is);
            System.out.println("Object：" + key + " 存入本地存储 " + (uploaded ? "成功" : "失败"));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        boolean deleted = AliyunOssUtils.delete(key);
        System.out.println("删除 Object：" + key + (deleted ? "成功" : "失败"));
        logger.info("Completed");
    }
}

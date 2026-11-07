package com.kdx.vms.commons.util.oss;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * 本地文件存储演示工具（原阿里云 OSS 调试类，OSS 停用后改造）。
 *
 * 历史上用 OSSClient 演示上传/下载/删除；现委托 {@link LocalFileStorage}，
 * 仅保留 main 作为本地存储的冒烟入口，无业务调用方。
 */
public class AliyunOssImage {

    public static void main(String[] args) throws IOException {
        String key = "demo/cole.txt";
        byte[] data = "vms local storage smoke".getBytes(StandardCharsets.UTF_8);

        try (InputStream in = new ByteArrayInputStream(data)) {
            boolean uploaded = AliyunOssUtils.upload(key, in);
            System.out.println("upload " + key + " -> " + uploaded);
        }

        try (InputStream read = LocalFileStorage.read(LocalFileStorage.publicRoot(), key)) {
            if (read != null) {
                System.out.println("read back -> " + new String(read.readAllBytes(), StandardCharsets.UTF_8));
            }
        }

        boolean deleted = AliyunOssUtils.delete(key);
        System.out.println("delete " + key + " -> " + deleted);
    }
}

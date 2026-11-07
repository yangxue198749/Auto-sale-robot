package com.kdx.vms;

import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.commons.util.oss.LocalFileStorage;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 本地文件存储闭环测试（替代阿里云 OSS）。
 *
 * 验证：
 *  1. 公共 bucket 上传后文件真实落盘，可通过 LocalFileStorage 读回
 *  2. 私有 bucket 上传隔离
 *  3. 删除生效
 *  4. 保持 AliyunOssUtils 方法签名兼容（业务零改动）
 *  5. 路径穿越防护（../ 被拒绝）
 */
public class LocalFileStorageTest {

    @Test
    public void testUploadReadDeletePublic() throws Exception {
        String key = "test/sku/" + System.currentTimeMillis() + ".png";
        byte[] payload = "fake-png-bytes".getBytes(StandardCharsets.UTF_8);

        // 1. 上传（走业务实际使用的 AliyunOssUtils 入口）
        boolean uploaded = AliyunOssUtils.upload(key, new ByteArrayInputStream(payload));
        assertTrue("上传应成功", uploaded);

        // 2. 物理文件存在于公共目录
        Path expected = Paths.get(LocalFileStorage.publicRoot(), key);
        assertTrue("文件应落盘到公共 bucket: " + expected, Files.exists(expected));
        assertEquals(payload.length, Files.size(expected));

        // 3. 读回内容一致
        try (InputStream in = LocalFileStorage.read(LocalFileStorage.publicRoot(), key)) {
            assertNotNull(in);
            byte[] back = in.readAllBytes();
            assertEquals("读回内容应与上传一致", new String(payload, StandardCharsets.UTF_8),
                    new String(back, StandardCharsets.UTF_8));
        }

        // 4. 删除
        boolean deleted = AliyunOssUtils.delete(key);
        assertTrue("删除应成功", deleted);
        assertFalse("删除后文件不应存在", Files.exists(expected));
    }

    @Test
    public void testPrivateBucketIsolation() throws Exception {
        String key = "test/cert/" + System.currentTimeMillis() + ".pem";
        byte[] payload = "private-cert".getBytes(StandardCharsets.UTF_8);

        boolean uploaded = AliyunOssUtils.uploadToPrivate(key, new ByteArrayInputStream(payload));
        assertTrue(uploaded);

        Path privPath = Paths.get(LocalFileStorage.privateRoot(), key);
        Path pubPath = Paths.get(LocalFileStorage.publicRoot(), key);
        assertTrue("私有文件应在私有目录", Files.exists(privPath));
        assertFalse("私有文件不应出现在公共目录", Files.exists(pubPath));

        AliyunOssUtils.delete(key);
        assertFalse(Files.exists(privPath));
    }

    @Test
    public void testPathTraversalRejected() {
        // key 含 ../ 试图越出 bucket 根目录，应被拒绝（返回 false），不写文件
        boolean result = AliyunOssUtils.upload("../../evil.txt",
                new ByteArrayInputStream("x".getBytes(StandardCharsets.UTF_8)));
        assertFalse("路径穿越应被拒绝", result);
    }

    @Test
    public void testInvalidParams() {
        assertFalse("空 key 上传应失败", AliyunOssUtils.upload("", new ByteArrayInputStream(new byte[0])));
        assertFalse("null 流上传应失败", AliyunOssUtils.upload("test/x.bin", null));
        assertFalse("空 key 删除应失败", AliyunOssUtils.delete(""));
    }
}

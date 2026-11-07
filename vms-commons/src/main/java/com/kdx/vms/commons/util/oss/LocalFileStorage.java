package com.kdx.vms.commons.util.oss;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件存储（替代已停用的阿里云 OSS）。
 *
 * 改造说明（VMS 第一期，2026-11）：
 * 原 AliyunOssUtils 依赖阿里云 OSS，服务停用后无法上传。本类将对象存储语义
 * （bucket + key）映射到本地磁盘目录，保持与 OSS 相同的方法契约：
 *   - key 形如 "theme/123_456.zip"、"sku/789.png"，自带目录前缀
 *   - 公共 bucket 写入 local.storage.public 目录
 *   - 私有 bucket 写入 local.storage.private 目录
 *   - 通过 Spring 静态资源映射或 Nginx 将 URL 前缀指向这些目录即可访问
 *
 * 存储根目录由 setting.properties 的 local.storage.root 配置，
 * 默认 ${user.home}/vms-files。
 */
public final class LocalFileStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileStorage.class);

    private static final String DEFAULT_ROOT = System.getProperty("user.home") + File.separator + "vms-files";
    private static final String PUBLIC_DIR = "public";
    private static final String PRIVATE_DIR = "private";

    private LocalFileStorage() {
    }

    /** 存储根目录 */
    private static String rootDir() {
        String configured = (String) PropertiesLoader.map.get("local.storage.root");
        return StringUtil.isEmpty(configured) ? DEFAULT_ROOT : configured;
    }

    /** 公共 bucket 根目录 */
    public static String publicRoot() {
        return rootDir() + File.separator + PUBLIC_DIR;
    }

    /** 私有 bucket 根目录 */
    public static String privateRoot() {
        return rootDir() + File.separator + PRIVATE_DIR;
    }

    /**
     * 保存文件到指定 bucket 目录。
     *
     * @param bucketRoot  bucket 根目录（公共/私有）
     * @param key         相对路径 key，如 theme/123.zip
     * @param inputStream 文件流
     * @return true 成功
     */
    public static boolean save(String bucketRoot, String key, InputStream inputStream) {
        try {
            if (StringUtil.isEmpty(key) || null == inputStream) {
                throw new IllegalArgumentException("Parameter error! key:" + key + ", inputStream:" + inputStream);
            }
            Path target = resolveSafe(bucketRoot, key);
            File parent = target.toFile().getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Cannot create directory: " + parent);
            }
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("[LocalFileStorage] saved {} -> {}", key, target);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * 删除文件。
     */
    public static boolean delete(String bucketRoot, String key) {
        try {
            if (StringUtil.isEmpty(key)) {
                throw new IllegalArgumentException("Parameter error! key is empty");
            }
            Path target = resolveSafe(bucketRoot, key);
            boolean ok = Files.deleteIfExists(target);
            LOGGER.info("[LocalFileStorage] delete {} -> {}, existed={}", key, target, ok);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * 读取文件为 InputStream。
     */
    public static InputStream read(String bucketRoot, String key) {
        try {
            Path target = resolveSafe(bucketRoot, key);
            if (!Files.exists(target)) {
                return null;
            }
            return new FileInputStream(target.toFile());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * 将 key 解析为 bucket 内的安全路径，防止 key 含 ../ 越界。
     */
    private static Path resolveSafe(String bucketRoot, String key) {
        Path root = Paths.get(bucketRoot).toAbsolutePath().normalize();
        Path target = root.resolve(key).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Invalid key (path traversal): " + key);
        }
        return target;
    }

    /**
     * 确保根目录存在（应用启动时可调用）。
     */
    public static void ensureDirs() {
        new File(publicRoot()).mkdirs();
        new File(privateRoot()).mkdirs();
        LOGGER.info("[LocalFileStorage] root={}, public={}, private={}", rootDir(), publicRoot(), privateRoot());
    }
}

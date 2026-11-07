package com.kdx.vms.commons.util.oss;

import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 文件存储工具（原阿里云 OSS，已改造为本地文件存储）。
 *
 * 改造说明（VMS 第一期，2026-11）：
 * 阿里云 OSS 停用，类名与 public 方法签名保持不变，业务调用方零改动；
 * 内部委托 {@link LocalFileStorage} 落盘到本地目录。
 *   - upload / delete / info            → 公共 bucket（local.storage.root/public）
 *   - uploadToPrivate                   → 私有 bucket（local.storage.root/private）
 *
 * 历史的 bucketName/endpoint/AK 概念在本地模式下不再使用，仅保留
 * setBucketName 以兼容可能的旧调用（本地实现忽略其值）。
 */
public class AliyunOssUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunOssUtils.class);

    static {
        // 应用加载该类时确保本地目录存在
        LocalFileStorage.ensureDirs();
    }

    /**
     * @deprecated 本地存储模式下 bucket 概念已弱化，保留仅为兼容旧调用，值被忽略
     */
    @Deprecated
    public static void setBucketName(String bucketName) {
        LOGGER.warn("[AliyunOssUtils] setBucketName ignored in local storage mode: {}", bucketName);
    }

    public static boolean upload(String key, InputStream inputStream) {
        LOGGER.info("[LocalStorage upload] key:{}", key);
        return LocalFileStorage.save(LocalFileStorage.publicRoot(), key, inputStream);
    }

    public static boolean uploadToPrivate(String key, InputStream inputStream) {
        LOGGER.info("[LocalStorage uploadToPrivate] key:{}", key);
        return LocalFileStorage.save(LocalFileStorage.privateRoot(), key, inputStream);
    }

    public static boolean delete(String key) {
        LOGGER.info("[LocalStorage delete] key:{}", key);
        if (StringUtil.isEmpty(key)) {
            return false;
        }
        // 历史数据可能在公共或私有 bucket，两处都尝试删除，任一存在即成功
        boolean pub = LocalFileStorage.delete(LocalFileStorage.publicRoot(), key);
        boolean pri = LocalFileStorage.delete(LocalFileStorage.privateRoot(), key);
        return pub || pri;
    }

    /**
     * 原 OSS 图片信息查询（image/info）。本地无等价服务，返回 null。
     * 业务代码当前无调用点，保留签名以兼容。
     */
    public static String info(String key) {
        LOGGER.warn("[LocalStorage info] not supported in local storage mode, key:{}", key);
        return null;
    }

}

package com.kdx.vms.commons.util.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by liyang on 2017/4/25.
 */
public class AliyunOssUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunOssUtils.class);

    private static final String END_POINT_DEV = "http://oss-cn-beijing.aliyuncs.com";
    private static final String END_POINT_PRO = "http://vpc100-oss-cn-beijing.aliyuncs.com";

    private static final String ACCESS_KEY_ID = (String) PropertiesLoader.map.get("aliyun.oss.accessKeyId");
    private static final String ACCESS_KEY_SECRET = (String) PropertiesLoader.map.get("aliyun.oss.accessKeySecret");

    private static final boolean online = "com".equals((String) PropertiesLoader.map.get("pay_mobile_suffix"));
    private static final String BUCKET_NAME_DEV = "oss-dev-s1";
    private static final String BUCKET_NAME_PRO = "oss-s1";

    private static final String PRIVATE_BUCKET_NAME_DEV = "oss-dev-private-s1";
    private static final String PRIVATE_BUCKET_NAME_PRO = "oss-private-s1";


    private static String end_point = online ? END_POINT_PRO : END_POINT_DEV;
    private static String bucketName = online ? BUCKET_NAME_PRO : BUCKET_NAME_DEV;
    private static String privateBucketName = online ? PRIVATE_BUCKET_NAME_PRO : PRIVATE_BUCKET_NAME_DEV;

    public static void setBucketName(String bucketName) {
        AliyunOssUtils.bucketName = bucketName;
    }

    public static boolean upload(String key, InputStream inputStream) {
        LOGGER.info("[AliyunOss upload] bucketName:" + bucketName + ", key:" + key + ", inputStream:" + inputStream);
        OSSClient ossClient = null;
        try {
            if (StringUtil.isEmpty(key) || null == inputStream) {
                throw new Exception("Parameter error! key:" + key + ", inputStream:" + inputStream);
            }
            ossClient = new OSSClient(end_point, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            PutObjectResult putObjectResult = ossClient.putObject(new PutObjectRequest(
                    bucketName,
                    key,
                    inputStream));
            LOGGER.info("requestId:" + putObjectResult.getRequestId() + ", eTag:" + putObjectResult.getETag());
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        } finally {
            if (null != ossClient) ossClient.shutdown();
        }
        return false;
    }

    public static boolean uploadToPrivate(String key, InputStream inputStream) {
        LOGGER.info("[AliyunOss upload] bucketName:" + privateBucketName + ", key:" + key + ", inputStream:" + inputStream);
        OSSClient ossClient = null;
        try {
            if (StringUtil.isEmpty(key) || null == inputStream) {
                throw new Exception("Parameter error! key:" + key + ", inputStream:" + inputStream);
            }
            ossClient = new OSSClient(end_point, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            PutObjectResult putObjectResult = ossClient.putObject(new PutObjectRequest(
                    privateBucketName,
                    key,
                    inputStream));
            LOGGER.info("requestId:" + putObjectResult.getRequestId() + ", eTag:" + putObjectResult.getETag());
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        } finally {
            if (null != ossClient) ossClient.shutdown();
        }
        return false;
    }

    public static boolean delete(String key) {
        LOGGER.info("[AliyunOss delete] bucketName:" + bucketName + ", key:" + key);
        OSSClient ossClient = null;
        try {
            if (StringUtil.isEmpty(key)) {
                throw new Exception("Parameter error!");
            }
            ossClient = new OSSClient(end_point, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            ossClient.deleteObject(bucketName, key);
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        } finally {
            if (null != ossClient) ossClient.shutdown();
        }
        return false;
    }

    public static String info(String key) {
        LOGGER.info("[AliyunOss info] bucketName:" + bucketName + ", key:" + key);
        OSSClient ossClient = null;
        try {
            if (StringUtil.isEmpty(key)) {
                throw new Exception("Parameter error!");
            }
            ossClient = new OSSClient(end_point, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            String style = "image/info";
            GetObjectRequest request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            OSSObject ossObject = ossClient.getObject(request);
            InputStream inputStream = ossObject.getObjectContent();
            return IOUtils.readStreamAsString(inputStream, "UTF-8");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        } finally {
            if (null != ossClient) ossClient.shutdown();
        }
        return null;
    }

}

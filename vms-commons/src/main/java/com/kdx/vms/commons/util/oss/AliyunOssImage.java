package com.kdx.vms.commons.util.oss;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;

import java.io.*;

/**
 *
 * Created by liyang on 2017/4/24.
 */
public class AliyunOssImage {

    private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = (String) PropertiesLoader.map.get("aliyun.oss.accessKeyId");
    private static String accessKeySecret = (String) PropertiesLoader.map.get("aliyun.oss.accessKeySecret");
    private static String bucketName = "oss-dev-s1";


    public static void main(String[] args) throws IOException {

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            // 上传文件
            PutObjectResult putObjectResult = ossClient.putObject(new PutObjectRequest(
                    bucketName,
                    "cole.jpg",
                    new File("C:\\logs\\cole.jpg")));
            System.out.println(putObjectResult);
            System.out.println(putObjectResult.getRequestId());
            System.out.println(putObjectResult.getETag());

            // 获取文件信息
            String style = "image/info";
            GetObjectRequest request= new GetObjectRequest(bucketName, "cole.jpg");
            request.setProcess(style);
            ossClient.getObject(request, new File("c:\\logs\\cole-info.txt"));

            // 下载文件
            OSSObject ossObject = ossClient.getObject(bucketName, "cole.jpg");
            FileOutputStream fos = new FileOutputStream("c:\\logs\\cole_1.jpg");
            System.out.println(ossObject.getObjectMetadata().getContentType());
            System.out.println(ossObject.getObjectMetadata().getContentLength());
            InputStream in = ossObject.getObjectContent();
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                fos.write(b, 0 , len);
            }
            fos.flush();
            fos.close();

            // 删除 一个
            ossClient.deleteObject(bucketName, "cole.jpg");

            // 批量删除
//            DeleteObjectsResult deleteObjectsResult =
//                    ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(Arrays.asList("1.jpg", "2.jpg")));
//            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
//            for (String object : deletedObjects) {
//                System.out.println("\t" + object);
//            }
        } finally {
            ossClient.shutdown();
        }


//        try {
//            // 缩放
//            String style = "image/resize,m_fixed,w_100,h_100";
//            GetObjectRequest request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-resize.jpg"));
//
//            // 裁剪
//            style = "image/crop,w_100,h_100,x_100,y_100,r_1";
//            request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-crop.jpg"));
//
//            // 旋转
//            style = "image/rotate,90";
//            request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-rotate.jpg"));
//
//            // 锐化
//            style = "image/sharpen,100";
//            request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-sharpen.jpg"));
//
//            // 水印
//            style = "image/watermark,text_SGVsbG8g5Zu-54mH5pyN5YqhIQ";
//            request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-watermark.jpg"));
//
//            // 格式转换
//            style = "image/format,png";
//            request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-format.png"));
//
//            // 图片信息
//            style = "image/info";
//            request = new GetObjectRequest(bucketName, key);
//            request.setProcess(style);
//
//            ossClient.getObject(request, new File("example-info.txt"));
//
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message: " + oe.getErrorCode());
//            System.out.println("Error Code:       " + oe.getErrorCode());
//            System.out.println("Request ID:      " + oe.getRequestId());
//            System.out.println("Host ID:           " + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message: " + ce.getMessage());
//        } catch (Throwable e) {
//            e.printStackTrace();
//        } finally {
//            ossClient.shutdown();
//        }
    }
}

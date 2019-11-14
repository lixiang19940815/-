package com.itheima.health.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author zhangmeng
 * @description 骑牛云工具类
 * @date 2019/9/26
 **/
@Slf4j
public class QiniuUtils {
    public static final String CONFIG_FILE = "qiniu-config.properties";
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String QINIU_IMG_URL_PRE;
    public static String BUCKET;

    static {
        Properties prop = new Properties();
        // 加载配置
        try(InputStream is = QiniuUtils.class.getClassLoader().getResourceAsStream("qiniu-config.properties")) {
            if(null == is){
                log.error("[七牛云工具类-初始化]失败,请提供配置文件：{}",CONFIG_FILE);
            }else {
                prop.load(is);
            }
        } catch (IOException e) {
            // 几乎不可能事件，直接转换为运行时异常继续上抛
            throw new RuntimeException(e);
        }
        ACCESS_KEY = prop.getProperty("access.key", "");
        SECRET_KEY = prop.getProperty("secret.key", "");
        QINIU_IMG_URL_PRE = prop.getProperty("img.url.prefix", "");
        BUCKET = prop.getProperty("bucket", "");
        log.info("[七牛云工具类-初始化]完成");
    }

    /**
     * 上传到七牛云
     *
     * @param is             上传内容的输入流
     * @param uploadFileName
     */
    public static void upload2Qiniu(InputStream is, String uploadFileName) throws QiniuException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = uploadFileName;
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        String upToken = auth.uploadToken(BUCKET);
        try {
            Response response = uploadManager.put(is, key, upToken, null, null);
            //解析上传成功的结果
            log.info(response.bodyString());
            // 访问路径
            log.info("{}/{}", QINIU_IMG_URL_PRE, uploadFileName);
        } catch (QiniuException ex) {
            Response r = ex.response;
            log.error(r.toString());
            try {
                log.error(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
                log.error("", ex2);
            }
            throw ex;
        }
    }

    public static void deleteFileFromQiniu(String fileName) throws QiniuException {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        String key = fileName;
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(BUCKET, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            log.error("code:{}", ex.code());
            log.error(ex.response.toString());
            throw ex;
        }
    }
}

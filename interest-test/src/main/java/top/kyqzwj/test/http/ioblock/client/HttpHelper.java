package top.kyqzwj.test.http.ioblock.client;

import net.dongliu.requests.RawResponse;
import net.dongliu.requests.RequestBuilder;
import net.dongliu.requests.Requests;
import net.dongliu.requests.body.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 使用Requests来上传数据的基础包装类
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/19 14:56
 */
public class HttpHelper {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

    public static void uploadMinistry(String url, String fileName, String reqData){
        RawResponse rawResponse = postData(url,
                "500201_500201919b", fileName, reqData.getBytes(),
                false,
                false,
                null,
                8090, null, null);

        System.out.println(rawResponse);
    }

    /**
     * post请求函数
     *
     * @param url
     * @param auth
     * @param fileName
     * @param reqData
     * @param isSSL
     * @param isProxy
     * @param proxyIp
     * @param proxyPort
     * @return
     */
    private static RawResponse postData(String url, String auth, String fileName, byte[] reqData,
                                        boolean isSSL, boolean isProxy, String proxyIp, int proxyPort,
                                        List<Part<?>> parts, Map<String, Object> headers) {
        logger.debug("begin http post url:{},fileName:{},isSSL:{},isProxy:{}", url, fileName, isSSL, isProxy);
        logger.info("[httphelper]start http posturl:{},fileName:{}", url, fileName);
        if (headers == null) {
            headers = new HashMap<>();
            headers.put("binfile-gzip", "true");
            headers.put("binfile-auth", auth);
            headers.put("isCollection", "true");
        }

        Map body = new HashMap(4);
        body.put("fileName", fileName);
        RequestBuilder builder = Requests.post(url).body(body);

        return builder.timeout(600000)
                .headers(headers).send().charset("utf-8");
    }
}

package com.styx.support.service;

import com.styx.common.utils.convert.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2021/2/25
 */
@Slf4j
@Service
public class WeiXinService {

    private static OkHttpClient httpClient;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(6, TimeUnit.SECONDS)
                .writeTimeout(6, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        httpClient = builder.build();
    }

    @Value("${support.weixin.appid:}")
    private String WXAppId;
    @Value("${support.weixin.secret:}")
    private String WXSecret;

    public String getOpenIdOfWX(String jsCode) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="
                + WXAppId + "&secret=" + WXSecret + "&js_code=" + jsCode + "&grant_type=authorization_code";

        //创建一个Request
        Request request = new Request.Builder().get().url(url).build();

        // 同步
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Map result = JsonUtil.parseJson(response.body().string(), Map.class);
                String openid = (String) result.get("openid");
                return openid;
            }
        } catch (IOException e) {
            log.error("获取微信openId异常", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        return null;
    }
}

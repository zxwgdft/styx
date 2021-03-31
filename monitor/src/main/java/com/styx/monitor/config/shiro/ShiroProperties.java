package com.styx.monitor.config.shiro;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "styx.monitor.shiro")
public class ShiroProperties {

    /**
     * token field，如果NULL则不用
     */
    private String tokenField = "Authorization";

    /**
     * session 在redis中缓存时间
     */
    private int sessionTime = 30;

    /**
     * 记住密码时session过期时间
     */
    private int longSessionTime = 60 * 24 * 1;

    /**
     * 登录URL
     */
    private String loginUrl;

    /**
     * session在只是更新时间变化情况下间隔多少分钟更新
     * 设置该时间可以减少序列化session更新到redis的次数（通过只延长原有session的过期时间），
     * 但也因此可能会出现提前结束session的情况，例如session过期时间为30分钟，更新间隔5分钟，
     * 可能出现5分钟没有更新+25分钟没有请求后session过期。
     * <p>
     * 结合实际业务设置
     */
    private int updateSessionInterval = 5;

}

package com.styx.common.config;

/**
 * 全局工具类
 * <p>
 * 一些难以归类而又是全局需要的方法
 *
 * @author TontoZhou
 * @since 2021/3/10
 */
public class GlobalUtils {


    /**
     * 获取节点的对应数据采集服务URI
     *
     * @param nodeCode 节点编码
     * @param route    具体服务路由地址
     * @return 返回微服务地址
     */
    public static String getDataServiceURI(String nodeCode, String route) {
        String uri = "http://" + GlobalConstants.DATA_SERVICE_PREFIX + nodeCode;
        return route == null ? uri : (route.startsWith("/") ? (uri + route) : (uri + "/" + route));
    }

}

package com.styx.data;

import com.styx.data.core.NettyMessageServer;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.styx.data.mapper")
public class DataApplication implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DataApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Autowired
    private NettyMessageServer nettyMessageServer;

    @Override
    public void run(String... args) throws Exception {
        Thread netty = new Thread(() -> {
            try {
                log.info("启动数据采集服务...");
                nettyMessageServer.start();
            } catch (Exception e) {
                log.error("启动数据采集服务异常", e);
            }
        });

        netty.setDaemon(true);
        netty.setName("nettyStart");
        netty.start();
    }

}
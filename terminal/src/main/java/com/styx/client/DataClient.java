package com.styx.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2021/1/8
 */
@Slf4j
public class DataClient implements TimerTask {

    private Bootstrap bootstrap;
    private EventLoopGroup workGroup;

    private DataMessageSender dataMessageSender;
    private HashedWheelTimer wheelTimer;

    private String host;
    private int port;
    private String uid;

    public DataClient(String host, int port, String uid) {
        this.host = host;
        this.port = port;
        this.uid = uid;
    }

    public void start() {
        workGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        dataMessageSender = new DataMessageSender(this, uid);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(dataMessageSender);
            }
        });

        wheelTimer = new HashedWheelTimer(10, TimeUnit.SECONDS, 6);

        // 启动客户端
        run(null);
    }


    public void reconnect(long delay) {
        wheelTimer.newTimeout(this, delay, TimeUnit.SECONDS);
        // 立即执行重连
        // eventLoop.schedule 会造成connect动作叠加，之前的connect线程仍旧会工作
        // channel.eventLoop().schedule(this, 1, TimeUnit.SECONDS);
    }

    public void run(Timeout timeout) {
        log.info("尝试连接服务器[" + host + ":" + port + "]");

        ChannelFuture future = bootstrap.connect(host, port);

        if (future == null) {
            wheelTimer.newTimeout(this, 19, TimeUnit.SECONDS);
        } else {
            future.addListener(connectFuture -> {
                if (!connectFuture.isSuccess()) {
                    wheelTimer.newTimeout(this, 19, TimeUnit.SECONDS);
                    // eventLoop.schedule 会造成connect动作叠加，之前的connect线程仍旧会工作
                    //future.channel().eventLoop().schedule(this, reconnectInterval, TimeUnit.SECONDS);
                } else {
                    log.info("连接服务器[" + host + ":" + port + "]成功");
                }

            });
        }
    }

    public static void main(String[] args) {

        CommandOption option = new CommandOption();
        CmdLineParser parser = new CmdLineParser(option);


        if (args.length == 0) {
            System.out.println("请输入必要参数");
            parser.printUsage(System.out);
            return;
        }

        try {
            parser.parseArgument(args);

            if (option.help) {
                parser.printUsage(System.out);
            }

            if (StringUtil.isNullOrEmpty(option.host) ||
                    StringUtil.isNullOrEmpty(option.port) ||
                    StringUtil.isNullOrEmpty(option.terminals)) {
                System.out.println("必要参数缺失");
                parser.printUsage(System.out);
                return;
            }

            if (StringUtil.isNullOrEmpty(option.variablePath)) {
                VariableReader.readVariable();
            } else {
                try {
                    VariableReader.readVariable(new FileInputStream(option.variablePath));
                } catch (FileNotFoundException e) {
                    System.out.println("变量文件地址不正确");
                    e.printStackTrace();
                    return;
                }
            }

            String host = option.host;
            int port = Integer.valueOf(option.port);

            for (String uid : option.terminals.split(",")) {
                new DataClient(host, port, uid).start();
            }

        } catch (CmdLineException e) {
            e.printStackTrace();
            parser.printUsage(System.out);
        }


    }

    @Getter
    @Setter
    public static class CommandOption {

        @Option(name = "-host", required = true, usage = "数据服务IP")
        private String host;
        @Option(name = "-port", required = true, usage = "数据服务PORT")
        private String port;
        @Option(name = "-tids", required = true, usage = "终端UID,如果多个则用逗号拼接")
        private String terminals;
        @Option(name = "-vp", usage = "需要发送的变量描述文件，如果不填则使用默认")
        private String variablePath;

        @Option(name = "-help", usage = "帮助")
        private boolean help;

    }


}

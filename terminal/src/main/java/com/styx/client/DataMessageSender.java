package com.styx.client;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * @author TontoZhou
 * @since 2021/1/8
 */
@Slf4j
@ChannelHandler.Sharable
public class DataMessageSender extends ChannelInboundHandlerAdapter implements Runnable {

    private byte[] datagram;
    private DataClient client;

    private List<DataProducer> dataProducers;
    private int interval = 10 * 1000;

    private boolean connected = false;
    private Channel channel;
    private Thread sendDataThread;

    public DataMessageSender(DataClient client, String terminalUid) {
        this.client = client;
        datagram = new byte[1028];

        datagram[0] = (byte) 0xfa;
        datagram[1] = (byte) 0x0a;
        datagram[1027] = (byte) 0xfb;

        byte[] uid = ByteBufUtil.decodeHexDump(terminalUid);
        System.arraycopy(uid, 0, datagram, 6, 12);

        dataProducers = new ArrayList<>();

        HashSet<Integer> set = new HashSet<>();

        for (Variable variable : VariableReader.getVariables()) {
            addDataProducer(variable);
        }

        sendDataThread = new Thread(this);
        sendDataThread.setName("send-data");
        sendDataThread.setDaemon(true);
        sendDataThread.start();
    }

    private void addDataProducer(Variable variable) {

        Float value = variable.getValue();
        boolean random = value == null;

        if (variable.getId() == 35) {
            dataProducers.add(new IncrementalDataProducer(variable.getStartPosition(), 10000, 5));
        } else if (variable.getType() == 2) {
            dataProducers.add(
                    random ? new DataProducer(variable.getStartPosition(), variable.getMax().intValue(), variable.getMin().intValue())
                            : new DataProducer(variable.getStartPosition(), value)
            );
        } else if (variable.getType() == 0 || variable.getType() == 3) {
            boolean open;
            if (random) {
                open = new Random().nextBoolean();
            } else {
                open = value.intValue() >= 1;
            }
            dataProducers.add(new SwitchDataProducer(variable.getStartPosition(), variable.getSwitchPosition(), open, random));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channel active");
        //ctx.fireChannelActive();
        connected = true;
        channel = ctx.channel();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connected = false;
        log.debug("channel inactive");
        // 为防止服务器重启后短时间内大量连接重连，加入随机延迟，1-10秒之间
        int delay = new Random().nextInt(9) + 1;
        client.reconnect(delay);
    }


    public void run() {
        while (true) {
            // 设置时间
            setDateTime(datagram, 18);
            // 生成数据
            for (DataProducer producer : dataProducers) {
                producer.produceData(datagram);
            }

            if (connected && channel != null) {
                try {
                    channel.writeAndFlush(Unpooled.wrappedBuffer(datagram));
                } catch (Exception e) {
                    log.error("发送失败", e);
                }
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 按照BCD码形式转换当前时间到字节
     */
    public static void setDateTime(byte[] data, int startIndex) {
        LocalDateTime dt = LocalDateTime.now();
        int year = dt.getYear();
        data[startIndex++] = intToByte(year / 100);
        data[startIndex++] = intToByte(year % 100);
        data[startIndex++] = intToByte(dt.getMonthValue());
        data[startIndex++] = intToByte(dt.getDayOfMonth());
        data[startIndex++] = intToByte(dt.getHour());
        data[startIndex++] = intToByte(dt.getMinute());
        data[startIndex] = intToByte(dt.getSecond());
    }

    // 20 -> 0x20
    private static byte intToByte(int n) {
        if (n < 10) {
            return (byte) n;
        }
        return (byte) (n / 10 * 16 + n % 10);
    }


}

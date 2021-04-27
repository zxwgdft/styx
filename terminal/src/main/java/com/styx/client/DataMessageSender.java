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

    private int headSize = 23;
    private int dataSize = 500;
    private int frameSize = headSize + dataSize + 1;


    public DataMessageSender(DataClient client, String terminalUid) {
        this.client = client;
        datagram = new byte[frameSize];

        int size = frameSize - 2;
        datagram[0] = (byte) (size >> 8 & 0xff);
        datagram[1] = (byte) (size & 0xff);


        byte[] uid = ByteBufUtil.decodeHexDump(terminalUid);
        System.arraycopy(uid, 0, datagram, 2, 12);

        dataProducers = new ArrayList<>();

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
            dataProducers.add(new IncrementalDataProducer(variable.getStartPosition() + headSize, 10000, 5));
        } else if (variable.getType() == 2) {
            dataProducers.add(
                    random ? new DataProducer(variable.getStartPosition() + headSize, variable.getMax().intValue(), variable.getMin().intValue())
                            : new DataProducer(variable.getStartPosition() + headSize, value)
            );
        } else if (variable.getType() == 0 || variable.getType() == 3) {
            boolean open;
            if (random) {
                open = new Random().nextBoolean();
            } else {
                open = value.intValue() >= 1;
            }
            dataProducers.add(new SwitchDataProducer(variable.getStartPosition() + headSize, variable.getSwitchPosition(), open, random));
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
            long time = System.currentTimeMillis();

            datagram[14] = (byte) (time >> 56 & 0xff);
            datagram[15] = (byte) (time >> 48 & 0xff);
            datagram[16] = (byte) (time >> 40 & 0xff);
            datagram[17] = (byte) (time >> 32 & 0xff);
            datagram[18] = (byte) (time >> 24 & 0xff);
            datagram[19] = (byte) (time >> 16 & 0xff);
            datagram[20] = (byte) (time >> 8 & 0xff);
            datagram[21] = (byte) (time & 0xff);

            datagram[22] = 0x0a;

            // 生成数据
            for (DataProducer producer : dataProducers) {
                producer.produceData(datagram);
            }

            byte check = 0;
            for (int i = 0; i < frameSize - 1; i++) {
                check ^= datagram[i];
            }

            datagram[frameSize - 1] = check;

            if (connected && channel != null) {
                try {
                    channel.writeAndFlush(Unpooled.wrappedBuffer(datagram));
                } catch (Exception e) {
                    log.error("发送失败", e);
                }

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

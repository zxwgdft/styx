package com.styx.client;

import lombok.Getter;
import lombok.Setter;

import java.util.Random;

/**
 * @author TontoZhou
 * @since 2021/1/8
 */
@Getter
@Setter
public class DataProducer {

    //数据地址
    protected int address;

    protected int max;
    protected int min;

    protected float value;
    protected boolean random;

    public DataProducer(int address, int max, int min) {
        this.address = address;
        this.max = max;
        this.min = min;
        this.random = true;
    }

    public DataProducer(int address, float value) {
        this.address = address;
        this.value = value;
        this.random = false;
    }

    public void produceData(byte[] datagram) {
        fillFloatByte(random ? getRandomFloat() : value, datagram, address);
    }

    protected void fillFloatByte(float value, byte[] datagram, int index) {
        int i = Float.floatToIntBits(value);
        datagram[index++] = (byte) (i >> 24);
        datagram[index++] = (byte) (i >> 16);
        datagram[index++] = (byte) (i >> 8);
        datagram[index] = (byte) (i);
    }

    protected float getRandomFloat() {
        int d = max - min;
        if (d == 0) return max;

        Random random = new Random();
        int xs = random.nextInt(99);
        int zs = random.nextInt(d) + min;
        float val = xs / 100f + zs;
        return val;
    }


}

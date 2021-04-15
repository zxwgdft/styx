package com.paladin.client;

import java.util.Random;

/**
 * @author TontoZhou
 * @since 2021/1/8
 */
public class IncrementalDataProducer extends DataProducer {

    protected int value;
    protected int increment;

    public IncrementalDataProducer(int address, int startValue, int increment) {
        super(address, 0, 0);
        this.value = startValue;
        this.increment = increment;
    }

    public void produceData(byte[] datagram) {
        fillFloatByte(getIncreaseValue(), datagram, address);
    }

    protected float getIncreaseValue() {
        Random random = new Random();
        value += random.nextInt(increment);
        return random.nextInt(99) / 100f + value;
    }
}

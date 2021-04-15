package com.paladin.client;

/**
 * @author TontoZhou
 * @since 2021/1/8
 */
public class SwitchDataProducer extends DataProducer {

    protected int switchBitIndex;
    protected boolean open;

    protected long lastTime = 0;
    protected long interval = 120 * 1000;
    protected boolean change;

    public SwitchDataProducer(int address, int switchBitIndex, boolean open, boolean change) {
        super(address, 0, 0);
        this.switchBitIndex = switchBitIndex;
        this.open = open;
        this.change = change;
    }

    public void produceData(byte[] datagram) {
        if (change && System.currentTimeMillis() - lastTime > interval) {
            open = !open;
            lastTime = System.currentTimeMillis();
        }

        int v = datagram[address];
        int bit = v >> switchBitIndex & 0x01;
        if (open && bit == 0) {
            v = v + (0x01 << switchBitIndex);
        } else if (!open && bit == 1) {
            v = v - (0x01 << switchBitIndex);
        }
        datagram[address] = (byte) v;
    }


}

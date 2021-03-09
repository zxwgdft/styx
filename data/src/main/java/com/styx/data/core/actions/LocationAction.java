package com.styx.data.core.actions;

import com.styx.data.core.CommandAction;
import com.styx.data.core.Constants;
import com.styx.data.core.Datagram;
import com.styx.data.core.ProtocolUtil;
import com.styx.data.core.terminal.Terminal;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author TontoZhou
 * @since 2020/10/27
 */
@Slf4j
@Component
public class LocationAction implements CommandAction {

    private static final int COMMAND = 0x0D;

    @Override
    public int getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] doAction(ChannelHandlerContext context, Terminal terminal, Datagram datagram) {

        String location = terminal.getLocation();
        byte[] content = null;
        try {
            content = location.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("终端定位[" + location + "]非法字符串");
            content = new byte[0];
        }

        int size = content.length;

        // 返回服务器当前时间
        byte[] response = new byte[17 + size];

        response[0] = Constants.FRAME_HEAD;
        response[1] = COMMAND;

        // 从请求数据帧中拷贝终端ID，12位
        datagram.copyTerminalID(response, 2);

        // 设置定位长度
        response[14] = (byte) size;
        // 设置定位内容
        if (size > 0) {
            System.arraycopy(content, 0, response, 15, size);
        }


        // 计算校验码
        response[15 + size] = ProtocolUtil.calcCheckCode(response, 1, 20);
        response[16 + size] = Constants.FRAME_FOOT;

        return response;
    }
}

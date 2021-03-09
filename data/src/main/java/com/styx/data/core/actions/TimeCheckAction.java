package com.styx.data.core.actions;

import com.styx.data.core.CommandAction;
import com.styx.data.core.Constants;
import com.styx.data.core.Datagram;
import com.styx.data.core.ProtocolUtil;
import com.styx.data.core.terminal.Terminal;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 时间校验请求处理，直接返回当前服务器时间即可
 */
@Component
@Slf4j
public class TimeCheckAction implements CommandAction {

    private static final int COMMAND = 0x0C;

    @Override
    public int getCommand() {
        return COMMAND;
    }

    @Override
    public byte[] doAction(ChannelHandlerContext context, Terminal terminal, Datagram datagram) {

        // 返回服务器当前时间
        byte[] response = new byte[23];

        response[0] = Constants.FRAME_HEAD;
        response[1] = COMMAND;

        // 从请求数据帧中拷贝终端ID，12位
        datagram.copyTerminalID(response, 2);

        // BCD码形式时间
        ProtocolUtil.setDateTime(response, 14);

        // 计算校验码
        response[21] = ProtocolUtil.calcCheckCode(response, 1, 20);
        response[22] = Constants.FRAME_FOOT;

        return response;
    }


}

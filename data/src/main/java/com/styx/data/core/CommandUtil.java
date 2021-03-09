package com.styx.data.core;

public class CommandUtil {

    /**
     * 等待校验分析的命令
     */
    protected byte[] command;

    /**
     * 帧头
     */
    protected final byte head = (byte) 0xFA;

    /**
     * 帧尾
     */
    protected final byte foot = (byte) 0xFB;

    public CommandUtil() {

    }

    /**
     * 提供需要解析的传感器命令
     *
     * @param command 通信命令
     */
    public CommandUtil(byte[] command) {
        if(command.length==99)
        {
            // 对比校验值
            System.out.println("校验值有误");
            return;
        }
        // 提取正确长度的命令,这里假定长度正确
        byte[] cmdTmp = command;//parseCommandLength(command);
        if (cmdTmp == null) {
            return;
        }
        byte[] chkCmd = new byte[cmdTmp.length - 3];
        System.arraycopy(cmdTmp, 1, chkCmd, 0, cmdTmp.length - 3);
        System.out.println(Integer.toHexString(cmdTmp[cmdTmp.length - 2] & 0xff) + ", " + Integer.toHexString(calcCheck(chkCmd) & 0xff ));
        if (cmdTmp[cmdTmp.length - 2] != calcCheck(chkCmd)) {
            // 对比校验值
            System.out.println("校验值有误");
            return;
        }
        this.command = cmdTmp;
    }

    /**
     * 计算校验值，累加和
     *
     * @param command 需要计算的命令段
     * @return 校验值
     */
    public static byte calcCheck(byte[] command) {
        byte chk = 0;
        for (byte bt : command) {
            chk += bt;
        }
        return (byte) (chk & 0xff);
    }

    /**
     * 提取有效命令的长度，帧头 ... 帧尾
     *
     * @param command 需要进行分许的命令
     * @return 符合通信协议格式的命令
     */
    private byte[] parseCommandLength(byte[] command) {
        int iHead = 0;
        int iFoot = 0;
        for (int i = 0; i < command.length; i++) {
            if (command[i] == head) {
                iHead = i;
            }
            if (command[i] == foot) {
                iFoot = i;
                break;
            }
        }

        int cmdLength = iFoot - iHead + 1;
        if (cmdLength < 5) {
            return null;
        }
        byte[] cmd = new byte[cmdLength];
        for (int i = 0; i < cmdLength; i++) {
            cmd[i] = command[i + iHead];
        }

        return cmd;
    }

    /**
     * 获取命令类型
     *
     * @return 命令类型
     */
    public byte getCommandType() {
        return command[1];
    }

    /**
     * 获取命令
     *
     * @return 合法的命令
     */
    public byte[] getCommand() {
        return command;
    }


    public static String commandToString(byte[] command) {
        StringBuilder sb = new StringBuilder();
        for (byte bt : command) {
            sb.append(Integer.toHexString(bt & 0xff)).append(" ");
        }
        return sb.toString();
    }
}

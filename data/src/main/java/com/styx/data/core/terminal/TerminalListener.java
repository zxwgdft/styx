package com.styx.data.core.terminal;

/**
 * @author TontoZhou
 * @since 2020/12/31
 */
public interface TerminalListener {

    default void dataChangedHandle(Terminal terminal) {
        // do nothing
    }


}

package com.styx.data.core.terminal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/4/20
 */
public class TerminalContainer {

    private long version;
    private List<Terminal> terminals;
    private Map<String, Terminal> terminal2uidMap;
    private Map<Integer, Terminal> terminal2idMap;

    public TerminalContainer(long version, List<Terminal> terminalList) {
        this.version = version;

        if (terminalList == null || terminalList.size() == 0) {
            this.terminals = Collections.emptyList();
            this.terminal2uidMap = Collections.emptyMap();
            this.terminal2idMap = Collections.emptyMap();
        } else {
            this.terminals = Collections.unmodifiableList(terminalList);

            int initialCapacity = (int) (terminalList.size() / .75f) + 1;
            this.terminal2uidMap = new HashMap<>(initialCapacity);
            this.terminal2idMap = new HashMap<>(initialCapacity);

            for (Terminal terminal : terminalList) {
                terminal2uidMap.put(terminal.getUid(), terminal);
                terminal2idMap.put(terminal.getId(), terminal);
            }
        }
    }


    public Terminal getTerminal(String uid) {
        return terminal2uidMap.get(uid);
    }

    public Terminal getTerminal(int id) {
        return terminal2idMap.get(id);
    }

    public long getVersion() {
        return version;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }
}

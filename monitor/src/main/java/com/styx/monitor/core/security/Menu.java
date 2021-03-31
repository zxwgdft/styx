package com.styx.monitor.core.security;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Menu implements Serializable {

    private String id;

    private String url;

    private String name;

    private String icon;

    private List<Menu> children;

    public boolean equals(Object obj) {
        if (obj instanceof Menu) {
            return id.equals(((Menu) obj).id);
        }
        return false;
    }

    public int hashCode() {
        return 17 * 31 + id.hashCode();
    }

}

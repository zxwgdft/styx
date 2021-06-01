package com.styx.common.distrcit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/5/7
 */
@Getter
@Setter
public class District {

    private Integer id;
    private String name;
    private Integer pid;

    // 等级 1 省 2市 3县
    private int level;

    @JsonIgnore
    private District parent;
    private List<District> children;

    @JsonIgnore
    public String getFullName() {
        if (parent != null) {
            return parent.getFullName() + "-" + name;
        }
        return name;
    }

    public int hashCode() {
        return 17 * 31 + id.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof District) {
            return id.equals(((District) obj).id);
        }
        return false;
    }

}

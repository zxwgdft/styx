package com.paladin.monitor.core.security;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author TontoZhou
 * @since 2020/3/17
 */
@Getter
@Setter
public class Permission implements Serializable {

    @ApiModelProperty("权限ID")
    private String id;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("权限code")
    private String code;


    public boolean equals(Object obj) {
        if (obj instanceof Permission) {
            return code.equals(((Permission) obj).code);
        }
        return false;
    }

    public int hashCode() {
        return 17 * 31 + code.hashCode();
    }

}

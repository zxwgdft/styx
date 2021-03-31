package com.styx.monitor.service.org.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/10/19
 */
@Getter
@Setter
public class GrantPermissionDTO {
    private String roleId;
    private List<String> permissionIds;
}

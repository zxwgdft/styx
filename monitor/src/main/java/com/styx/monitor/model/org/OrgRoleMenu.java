package com.styx.monitor.model.org;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * @author TontoZhou
 * @since 2021/3/29
 */
@Getter
@Setter
public class OrgRoleMenu {

    @Id
    private String roleId;
    @Id
    private String menuId;

}

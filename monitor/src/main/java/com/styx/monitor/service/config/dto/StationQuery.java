package com.styx.monitor.service.config.dto;

import com.styx.monitor.core.distrcit.DistrictQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Getter
@Setter
public class StationQuery extends DistrictQuery {

    @ApiModelProperty("站点名称")
    private String name;

    @ApiModelProperty("节点服务器所属")
    private String serverNode;

    @ApiModelProperty("使用状态")
    private Boolean enabled;

}

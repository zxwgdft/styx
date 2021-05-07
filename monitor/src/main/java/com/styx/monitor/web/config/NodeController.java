package com.styx.monitor.web.config;

import com.styx.monitor.model.config.ConfigNode;
import com.styx.monitor.service.config.NodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2021/4/8
 */
@Api(tags = "节点")
@RestController
@RequestMapping("/monitor/config/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @ApiOperation("节点列表")
    @PostMapping("/find/all")
    public List<ConfigNode> findList() {
        return nodeService.findList();
    }

}

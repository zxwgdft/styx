package com.styx.monitor.service.config;

import com.styx.monitor.core.MonitorServiceSupport;
import com.styx.monitor.mapper.config.ConfigNodeMapper;
import com.styx.monitor.model.config.ConfigNode;
import org.springframework.stereotype.Service;

@Service
public class NodeService extends MonitorServiceSupport<ConfigNode, ConfigNodeMapper> {


}
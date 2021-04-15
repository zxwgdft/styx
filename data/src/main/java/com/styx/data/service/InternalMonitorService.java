package com.styx.data.service;

import com.styx.data.service.dto.VersionConfig;
import com.styx.data.service.dto.VersionUpdate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author TontoZhou
 * @since 2020/11/3
 */
@FeignClient(name = "styx-monitor")
public interface InternalMonitorService {

    @PostMapping("/internal/config/update")
    VersionConfig getVersionConfig(@RequestBody VersionUpdate versionUpdate);

}

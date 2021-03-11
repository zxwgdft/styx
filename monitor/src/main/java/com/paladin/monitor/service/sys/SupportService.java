package com.paladin.monitor.service.sys;

import com.paladin.framework.common.R;
import com.paladin.monitor.service.sys.dto.CommitOperate;
import com.paladin.monitor.service.sys.dto.SendMessage;
import com.paladin.monitor.service.sys.vo.FileResource;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/8/31
 */
@FeignClient(name = "msms-support")
public interface SupportService {

    @PostMapping("/file/operate")
    R commitOperate(@RequestBody CommitOperate commitOperate);

    @GetMapping("/file/get")
    List<FileResource> getFileResource(@RequestParam String id);

    @PostMapping("/support/sms/send")
    R sendMessage(@RequestBody SendMessage message);

    @GetMapping("/support/wx/get/openid")
    String getWXOpenId(@RequestParam String jsCode);


}

package com.styx.monitor.web.org;

import com.styx.common.service.PageResult;
import com.styx.common.spring.web.ControllerSupport;
import com.styx.monitor.model.org.OrgRole;
import com.styx.monitor.service.org.OrgRoleService;
import com.styx.monitor.service.org.dto.OrgRoleQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(tags = "角色")
@RestController
@RequestMapping("/monitor/org/role")
public class OrgRoleController extends ControllerSupport {

    @Autowired
    private OrgRoleService orgRoleService;

    @ApiOperation("角色列表")
    @PostMapping("/find/all")
    public List<OrgRole> findAll(@RequestBody OrgRoleQuery query) {
        return orgRoleService.findList(query);
    }


    @ApiOperation("角色列表（分页）")
    @PostMapping("/find/page")
    public PageResult<OrgRole> findPage(@RequestBody OrgRoleQuery query) {
        return orgRoleService.findPage(query);
    }


}

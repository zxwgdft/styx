package com.paladin.monitor.web.sys;

import com.paladin.framework.common.R;
import com.paladin.framework.spring.web.ControllerSupport;
import com.paladin.monitor.core.config.CTerminalContainer;
import com.paladin.monitor.core.log.OperationLog;
import com.paladin.monitor.core.security.NeedPermission;
import com.paladin.monitor.service.sys.SysNodeService;
import com.paladin.monitor.service.sys.dto.SysNodeDTO;
import com.paladin.monitor.service.sys.vo.SysNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Api(tags = "节点管理")
@RestController
@RequestMapping("/monitor/sys/node")
public class SysNodeController extends ControllerSupport {

    @Autowired
    private SysNodeService sysNodeService;

    @ApiOperation("节点列表查询")
    @GetMapping("/find")
    public List<SysNodeVO> findPage() {
        return sysNodeService.findAll(SysNodeVO.class);
    }


    @ApiOperation("添加节点")
    @PostMapping("/save")
    @NeedPermission("sys:node:add")
    @OperationLog(model = "节点管理", operate = "新增节点")
    public R save(@Valid @RequestBody SysNodeDTO save, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        sysNodeService.saveNode(save);
        return R.success();
    }

    @ApiOperation("更新节点")
    @PostMapping("/update")
    @NeedPermission("sys:node:edit")
    @OperationLog(model = "节点管理", operate = "更新节点")
    public R update(@Valid @RequestBody SysNodeDTO update, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validErrorHandler(bindingResult);
        }
        sysNodeService.updateNode(update);
        return R.success();
    }

    @ApiOperation("删除节点")
    @PostMapping("/delete")
    @NeedPermission("sys:node:delete")
    @OperationLog(model = "节点管理", operate = "删除节点")
    public R delete(@RequestParam String nodeId) {
        sysNodeService.removeNode(nodeId);
        return R.success();
    }

    @ApiOperation("获取简单节点列表")
    @PostMapping("/list")
    public List<CTerminalContainer.SimpleServerNode> getServerNode() {
        return sysNodeService.getSimpleNodeList();
    }

    @ApiOperation("父节点")
    @GetMapping("/parent/node")
    public List<CTerminalContainer.SimpleServerNode> getParentNode(){
        return sysNodeService.getParentNode();
    }

}

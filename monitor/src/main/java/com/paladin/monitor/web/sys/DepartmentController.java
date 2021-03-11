package com.paladin.monitor.web.sys;

import com.paladin.framework.common.R;
import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.PageResult;
import com.paladin.monitor.service.sys.DepartmentService;
import com.paladin.monitor.service.sys.dto.DepartmentDTO;
import com.paladin.monitor.service.sys.vo.DepartmentTreeVO;
import com.paladin.monitor.service.sys.vo.DepartmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author cxt
 * @date 2020/9/29
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/monitor/sys/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/get")
    @ApiOperation("查询所有部门")
    public PageResult<DepartmentVO> getDepartmentPage(OffsetPage query, @ApiParam("部门名称") @RequestParam(value = "name", required = false) String name) {
        return this.departmentService.getDepartmentPage(query, name);
    }

    @GetMapping("/tree")
    @ApiOperation("查询部门树结构")
    public List<DepartmentTreeVO> getSuperior() {
        return this.departmentService.getSuperior();
    }

    @GetMapping("/remove/{id}")
    @ApiOperation("删除部门")
    public R remove(@PathVariable("id") String id) {
        this.departmentService.remove(id);
        return R.success();
    }

    @PostMapping("/update/{id}")
    @ApiOperation("修改部门")
    public R updateDepartment(@PathVariable("id") String id, @RequestBody DepartmentDTO department) {
        this.departmentService.updateDepartment(id,department);
        return R.success();
    }

    @PostMapping("/save")
    @ApiOperation("添加部门")
    public R addDepartment(@RequestBody DepartmentDTO department) {
        this.departmentService.addDepartment(department);
        return R.success();
    }
}

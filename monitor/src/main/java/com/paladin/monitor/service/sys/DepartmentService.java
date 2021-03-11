package com.paladin.monitor.service.sys;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.paladin.framework.exception.BusinessException;
import com.paladin.framework.service.OffsetPage;
import com.paladin.framework.service.PageResult;
import com.paladin.framework.utils.UUIDUtil;
import com.paladin.monitor.core.MonitorUserSession;
import com.paladin.monitor.mapper.sys.DepartmentMapper;
import com.paladin.monitor.model.sys.Department;
import com.paladin.monitor.service.sys.dto.DepartmentDTO;
import com.paladin.monitor.service.sys.vo.DepartmentTreeVO;
import com.paladin.monitor.service.sys.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cxt
 * @date 2020/9/29
 */
@Service
public class DepartmentService {

    private DepartmentMapper departmentMapper;

    /**
     * 查询部门
     */
    public PageResult<DepartmentVO> getDepartmentPage(OffsetPage query, String name) {
        Page<DepartmentVO> page = PageHelper.offsetPage(query.getOffset(), query.getLimit());
        this.departmentMapper.getDepartment(name);
        return new PageResult<>(page);
    }


    /**
     * 查询部门树结构
     * 挑出顶级部门，遍历顶级下面的部门
     *
     * @return
     */
    public List<DepartmentTreeVO> getSuperior() {
        List<Department> departments = this.departmentMapper.selectByExample(new Example.Builder(Department.class)
                .where(WeekendSqls.<Department>custom().andEqualTo(Department::getState, "1")).build());
        List<DepartmentTreeVO> list = new ArrayList<>();
        departments.forEach(a -> {
            if (a.getPid() == null || a.getPid().equals("")) {
                list.add(new DepartmentTreeVO().singleDepartment(a.getId(), a.getName()));
            }
        });
        if (!list.isEmpty()) {
            list.forEach(child -> {
                child.setChildren(getChildList(child, departments));
            });
        }
        return list;
    }

    public List<DepartmentTreeVO> getChildList(DepartmentTreeVO departmentTreeVO, List<Department> departments) {
        ArrayList<DepartmentTreeVO> childList = new ArrayList<>();
        departments.forEach(child -> {
            if (departmentTreeVO.getId().equals(child.getPid())) {
                childList.add(new DepartmentTreeVO().singleDepartment(child.getId(), child.getName()));
            }
        });
        childList.forEach(son -> {
            son.setChildren(getChildList(new DepartmentTreeVO().singleDepartment(son.getId(), son.getName()), departments));
        });
        return childList;
    }

    /**
     * 修改部门
     */
    public void updateDepartment(String id, DepartmentDTO dto) {
        MonitorUserSession currentUserSession = MonitorUserSession.getCurrentUserSession();
        String userName = currentUserSession.getUserName();
        Department department = new Department(id, dto.getName(), dto.getSort(), dto.getPid(), dto.getState());
        department.setUpdateBy(userName);
        department.setUpdateTime(new Date());
        int update = this.departmentMapper.updateDepartment(department);
        if (update != 1) {
            throw new BusinessException("更新失败");
        }
    }

    /**
     * 添加部门
     */
    public void addDepartment(DepartmentDTO dto) {
        MonitorUserSession currentUserSession = MonitorUserSession.getCurrentUserSession();
        String userName = currentUserSession.getUserName();
        Department department = new Department(UUIDUtil.createUUID(), dto.getName(), dto.getSort(), dto.getPid(), dto.getState());
        department.setCreateBy(userName);
        department.setCreateTime(new Date());
        int insert = this.departmentMapper.insert(department);
        if (insert != 1) {
            throw new BusinessException("添加失败");
        }
    }

    /**
     * 删除部门
     */
    public void remove(String id) {
        this.departmentMapper.deleteByPrimaryKey(id);
    }

    @Autowired
    public void setDepartmentMapper(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }
}

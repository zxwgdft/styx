package com.paladin.monitor.service.org;


import com.paladin.monitor.core.distrcit.District;
import com.paladin.monitor.core.distrcit.DistrictContainer;
import com.paladin.monitor.model.org.OrgUser;
import com.paladin.monitor.model.sys.SysUser;
import com.paladin.monitor.service.org.dto.OrgUserDTO;
import com.paladin.monitor.service.sys.SysUserService;
import com.styx.common.exception.BusinessException;
import com.styx.common.service.ServiceSupport;
import com.styx.common.utils.StringUtil;
import com.styx.common.utils.UUIDUtil;
import com.styx.common.utils.convert.SimpleBeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


@Service
public class OrgUserService extends ServiceSupport<OrgUser> {

    @Autowired
    private SysUserService sysUserService;

    @Transactional
    public String saveUser(OrgUserDTO saveUser) {
        checkUser(saveUser);

        String userId = UUIDUtil.createUUID();
        String account = saveUser.getAccount();

        String password = sysUserService.createUserAccount(account, userId, SysUser.USER_TYPE_PERSONNEL);
        OrgUser user = SimpleBeanCopyUtil.simpleCopy(saveUser, new OrgUser());
        user.setId(userId);
        save(user);

        return password;
    }

    @Transactional
    public void updateUser(OrgUserDTO updateUser) {
        String id = updateUser.getId();
        if (StringUtil.isEmpty(id)) {
            throw new BusinessException("用户ID不能为空");
        }

        OrgUser user = get(id);
        if (user == null) {
            throw new BusinessException("找不到需要更新的用户[ID:" + id + "]");
        }

        checkUser(updateUser);

        // 更新账号表中账号
        sysUserService.updateUserAccount(id, user.getAccount());

        SimpleBeanCopyUtil.simpleCopy(updateUser, user);
        update(user);
    }

    /**
     * 检查用户数据合法性
     */
    private void checkUser(OrgUserDTO user) {
        int userType = user.getType();
        if (userType == OrgUser.USER_TYPE_STATION) {
            // 检查station
            String stationStr = user.getStationId();
            if (StringUtil.isEmpty(stationStr)) {
                throw new BusinessException("站点管理员必须选择至少一个站点");
            }
            // 检查数据库中是否存在该站点，暂时没必要
        } else if (userType == OrgUser.USER_TYPE_DISTRICT) {
            // 检查district
            String districtCodeStr = user.getDistrictCode();
            if (StringUtil.isEmpty(districtCodeStr)) {
                throw new BusinessException("区域管理员必须选择至少一个区域");
            }

            String[] dcs = districtCodeStr.split(",");
            HashSet<Integer> set = new HashSet<>();
            for (int i = 0; i < dcs.length; i++) {
                set.add(Integer.valueOf(dcs[i]));
            }

            // 检查区域重复性
            ArrayList<Integer> list = new ArrayList<>(set.size());
            for (Integer code : set) {
                if (needDistrict(set, code)) {
                    list.add(code);
                }
            }

            user.setDistrictCode(StringUtil.join(list));
        }
    }

    private boolean needDistrict(Set<Integer> codeSet, Integer code) {
        District district = DistrictContainer.getDistrict(code);
        if (district != null) {
            District parent = district.getParent();
            if (parent == null) {
                return true;
            } else {
                if (codeSet.contains(parent.getId())) {
                    return false;
                } else {
                    parent = parent.getParent();
                    if (parent == null) {
                        return true;
                    } else {
                        return !codeSet.contains(parent.getId());
                    }
                }
            }
        } else {
            return false;
        }
    }
    
}

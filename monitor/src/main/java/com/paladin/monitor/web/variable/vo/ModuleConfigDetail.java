package com.paladin.monitor.web.variable.vo;

import com.paladin.monitor.service.variable.vo.ModuleConfigVO;
import com.paladin.monitor.service.variable.vo.VariableVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author TontoZhou
 * @since 2020/12/17
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "终端模组配置详情")
public class ModuleConfigDetail {

    private List<ModuleConfigVO> config;

    private List<VariableVO> variables;
}

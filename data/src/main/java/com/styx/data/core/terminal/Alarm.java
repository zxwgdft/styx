package com.styx.data.core.terminal;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author TontoZhou
 * @since 2020/10/28
 */
@Getter
@Setter
public class Alarm {

    private int id;

    // 报警名称
    private String name;

    // 公式
    private String formula;

    // 公式变量ID集合
    private Set<Integer> variableIds;

    // 公式模板
    private ExpressionTemplate formulaTemplate;


    public Alarm(int id, String name, String formula, String variableIds) {
        this.id = id;
        this.formula = formula;
        this.name = name;
        this.formulaTemplate = new ExpressionTemplate(formula);

        String[] vids = variableIds.split(",");
        this.variableIds = new HashSet<>();
        for (String vid : vids) {
            this.variableIds.add(Integer.valueOf(vid));
        }
    }


}

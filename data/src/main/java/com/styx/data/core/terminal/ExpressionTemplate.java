package com.styx.data.core.terminal;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TontoZhou
 */
public class ExpressionTemplate {

    protected String template;

    public ExpressionTemplate(String template) {
        setTemplate(template);
    }

    public String createExpression(Map<String, Float> valueMap) {

        if (valueMap == null) return null;

        StringBuilder sb = new StringBuilder();

        int size = items.size();
        for (int i = 0; i < size; i++) {
            sb.append(items.get(i));
            if (++i < size) {
                Float value = valueMap.get(items.get(i));
                if (value != null) {
                    sb.append(value.toString());
                } else {
                    return null;
                }
            }
        }

        return sb.toString();
    }


    // 模板参数map
    protected List<String> items;


    public void setTemplate(String template) {

        List<String> items = new ArrayList<>();

        if (template != null && template.length() > 0) {
            Pattern pattern = Pattern.compile("value[0-9]+");
            Matcher matcher = pattern.matcher(template);
            int i = 0;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String item = template.substring(i, start);
                items.add(item);
                String group = matcher.group();
                items.add(group);
                i = end;
            }
            items.add(template.substring(i));
        }

        if (items.size() == 0) {
            throw new RuntimeException("非法的表达式");
        }

        this.template = template;
        this.items = items;
    }


}

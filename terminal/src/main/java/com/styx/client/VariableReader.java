package com.styx.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TontoZhou
 * @since 2021/1/8
 */
@Slf4j
public class VariableReader {

    private static Map<Integer, Variable> variableMap;
    private static List<Variable> variableList;

    public static void readVariable() {
        readVariable(VariableReader.class.getResourceAsStream("variable.json"));
    }

    public static void readVariable(InputStream input) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            variableList = objectMapper.readValue(input, new TypeReference<List<Variable>>() {
            });

            variableMap = new HashMap<>();
            for (Variable variable : variableList) {
                variableMap.put(variable.getId(), variable);
            }
        } catch (IOException e) {
            log.error("读取变量数据异常", e);
        }
    }

    public static Variable getVariable(int variableId) {
        return variableMap.get(variableId);
    }

    public static List<Variable> getVariables() {
        return variableList;
    }

}

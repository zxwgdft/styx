package com.styx.common.spring.web;

import com.styx.common.api.HttpCode;
import com.styx.common.api.R;
import com.styx.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ControllerSupport {

    /**
     * 验证异常处理
     *
     * @param bindingResult
     * @return
     */
    public void validErrorHandler(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            String[][] result = new String[errors.size()][3];
            int i = 0;
            for (FieldError error : bindingResult.getFieldErrors()) {
                result[i++] = new String[]{error.getCode(), error.getField(), error.getDefaultMessage()};
            }
            throw new BusinessException(HttpStatus.BAD_REQUEST, "", R.fail(HttpCode.REQUEST_VALID_ERROR, "请求参数验证未通过", result));
        }
    }


}

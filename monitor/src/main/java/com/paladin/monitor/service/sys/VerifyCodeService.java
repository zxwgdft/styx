package com.paladin.monitor.service.sys;

import com.paladin.framework.exception.BusinessException;
import com.paladin.monitor.util.verifyCode.IVerifyCodeGen;
import com.paladin.monitor.util.verifyCode.SimpleCharVerifyCodeGenImpl;
import com.paladin.monitor.util.verifyCode.VerifyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author TontoZhou
 * @since 2020/11/30
 */
@Service
public class VerifyCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private IVerifyCodeGen iVerifyCodeGen = new SimpleCharVerifyCodeGenImpl();

    private static String prefix = "VERIFY_CODE_";

    @Value("${monitor.verify.enabled:true}")
    private boolean verifyEnabled;


    public VerifyCode createVerifyCode() {
        try {
            VerifyCode verifyCode = iVerifyCodeGen.generate(80, 28);
            String code = verifyCode.getCode();
            String value = prefix + code.toUpperCase();
            redisTemplate.opsForValue().set(value, value, 90, TimeUnit.SECONDS);

            return verifyCode;
        } catch (IOException e) {
            throw new BusinessException("生成验证码失败");
        }
    }

    public boolean validVerifyCode(String code) {
        if (!verifyEnabled) {
            return true;
        }

        if (code == null || code.length() == 0) {
            return false;
        }
        String value = prefix + code.toUpperCase();
        return redisTemplate.opsForValue().get(value) != null;
    }

}

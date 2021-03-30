package com.styx.common.utils.secure;


import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class SecureUtil {

    private final static SecureRandom secureRandom = new SecureRandom();

    public static String createSalt() {
        return createSalt(16);
    }

    public static String createSalt(int numBytes) {
        byte[] bytes = new byte[numBytes];
        secureRandom.nextBytes(bytes);
        return Hex.encodeHexString(bytes);
    }

    public static String hashByMD5(String content, String salt) {
        // 进行一次加盐操作
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

            digest.reset();
            digest.update(saltBytes);

            byte[] hashed = digest.digest(contentBytes);
            return Hex.encodeHexString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("hash by md5 error", e);
        }
    }

}

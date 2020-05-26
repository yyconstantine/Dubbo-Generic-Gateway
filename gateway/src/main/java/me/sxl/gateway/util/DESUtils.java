package me.sxl.gateway.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author songxianglong
 * @date 2019/11/11 16:22
 */
@Slf4j
public class DESUtils {

    /**
     * 对上传字符串进行DES加密并Base64
     * @param content 数据
     * @param key DES-KEY
     * @return DES+Base64
     */
    public static String encrypt(String content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);
            return Base64.getMimeEncoder().encodeToString(cipher.doFinal(content.getBytes()));
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String decrypt(String content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, secureKey, random);
            byte[] result = cipher.doFinal(Base64.getMimeDecoder().decode(content));
            return new String(result);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}


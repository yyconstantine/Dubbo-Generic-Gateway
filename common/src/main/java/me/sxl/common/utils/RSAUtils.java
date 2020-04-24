package me.sxl.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA加密工具类
 *
 * @author songxianglong
 * @date 2019/11/11 14:21
 */
@Slf4j
public class RSAUtils {

    /**
     * 定义加密方式
     */
    private static final String KEY_RSA = "RSA";

    /**
     * 定义公钥关键词
     */
    private static final String KEY_RSA_PUBLIC_KEY = "RSAPublicKey";

    /**
     * 定义私钥关键词
     */
    private static final String KEY_RSA_PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 定义签名算法
     */
    private final static String KEY_RSA_SIGNATURE = "MD5withRSA";

    /**
     * 生成公私密钥对
     *
     * @return 公私密钥对
     */
    public static Map<String, Object> init() {
        Map<String, Object> map = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_RSA);
            // 设置密钥对的bit数,1024为较为安全的值
            generator.initialize(1024);
            KeyPair keyPair = generator.generateKeyPair();
            // 获取公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // 获取私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 将密钥对放入map
            map = new HashMap<>(2);
            map.put(KEY_RSA_PUBLIC_KEY, publicKey);
            map.put(KEY_RSA_PRIVATE_KEY, privateKey);
        } catch (NoSuchAlgorithmException e) {
            log.error("生成公私密钥对出错:", e);
        }

        return map;
    }

    /**
     * 获取Base64编码的公钥字符串
     *
     * @param map 公私密钥对
     * @return Base64编码的公钥字符串
     */
    public static String getPublicKey(Map<String, Object> map) {
        String str = "";
        Key key = (Key) map.get(KEY_RSA_PUBLIC_KEY);
        str = encryptBase64(key.getEncoded());
        return str;
    }

    /**
     * 获取Base64编码的私钥字符串
     *
     * @param map 公私密钥对
     * @return Base64编码的私钥字符串
     */
    public static String getPrivateKey(Map<String, Object> map) {
        String str = "";
        Key key = (Key) map.get(KEY_RSA_PRIVATE_KEY);
        str = encryptBase64(key.getEncoded());
        return str;
    }

    /**
     * 公钥加密
     *
     * @param encryptStr   编码后的字符串
     * @param publicKeyStr 公钥字符串
     * @return 加密结果
     */
    public static String encryptByPublicKey(String encryptStr, String publicKeyStr) {
        try {
            // 将公钥由字符串转为UTF-8的字节数组
            byte[] publicKeyBytes = decryptBase64(publicKeyStr);
            // 获取公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 取得待加密数据
            byte[] data = encryptStr.getBytes(StandardCharsets.UTF_8);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PublicKey publicKey = factory.generatePublic(keySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 返回加密及Base64编码的加密信息
            return encryptBase64(cipher.doFinal(data));
        } catch (Exception e) {
            log.error("公钥加密出错:", e);
            return null;
        }
    }

    /**
     * 公钥解密
     *
     * @param encryptedStr 编码后的字符串
     * @param publicKeyStr 公钥字符串
     * @return 解密结果
     */
    public static String decryptByPublicKey(String encryptedStr, String publicKeyStr) {
        try {
            // 对公钥解密
            byte[] publicKeyBytes = decryptBase64(encryptedStr);
            // 取得公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 取得待加密数据
            byte[] data = Base64.getDecoder().decode(encryptedStr);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PublicKey publicKey = factory.generatePublic(keySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            // 返回UTF-8编码的解密信息
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("公钥解密出错:", e);
            return null;
        }
    }

    /**
     * 私钥加密
     *
     * @param encryptingStr 加密串
     * @param privateKeyStr 私钥
     * @return 加密结果
     */
    public static String encryptByPrivateKey(String encryptingStr, String privateKeyStr) {
        try {
            byte[] privateKeyBytes = decryptBase64(privateKeyStr);
            // 获得私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // 取得待加密数据
            byte[] data = encryptingStr.getBytes(StandardCharsets.UTF_8);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            // 返回加密后由Base64编码的加密信息
            return encryptBase64(cipher.doFinal(data));
        } catch (Exception e) {
            log.error("私钥加密出错:", e);
            return null;
        }
    }

    /**
     * 私钥解密
     *
     * @param encryptedStr  解密串
     * @param privateKeyStr 私钥
     * @return 解密结果
     */
    public static String decryptByPrivateKey(String encryptedStr, String privateKeyStr) {
        try {
            // 对私钥解密
            byte[] privateKeyBytes = decryptBase64(privateKeyStr);
            // 获得私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // 获得待解密数据
            byte[] data = decryptBase64(encryptedStr);
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 返回UTF-8编码的解密信息
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("私钥解密出错:", e);
            return null;
        }
    }

    /**
     * 用私钥对加密数据进行签名
     *
     * @param encryptedStr 加密串
     * @param privateKey   私钥
     * @return 签名结果
     */
    public static String sign(String encryptedStr, String privateKey) {
        String str = "";
        try {
            //将私钥加密数据字符串转换为字节数组
            byte[] data = encryptedStr.getBytes();
            // 解密由base64编码的私钥
            byte[] bytes = decryptBase64(privateKey);
            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            // 指定的加密算法
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            // 取私钥对象
            PrivateKey key = factory.generatePrivate(keySpec);
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(KEY_RSA_SIGNATURE);
            signature.initSign(key);
            signature.update(data);
            str = encryptBase64(signature.sign());
        } catch (Exception e) {
            log.error("签名出错:", e);
        }
        return str;
    }

    /**
     * 校验数字签名
     *
     * @param encryptedStr 加密串
     * @param publicKey    公钥
     * @param sign         签名串
     * @return 校验成功返回true，失败返回false
     */
    public static boolean verify(String encryptedStr, String publicKey, String sign) {
        boolean flag = false;
        try {
            //将私钥加密数据字符串转换为字节数组
            byte[] data = encryptedStr.getBytes();
            // 解密由base64编码的公钥
            byte[] bytes = decryptBase64(publicKey);
            // 构造X509EncodedKeySpec对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            // 指定的加密算法
            KeyFactory factory = KeyFactory.getInstance(KEY_RSA);
            // 取公钥对象
            PublicKey key = factory.generatePublic(keySpec);
            // 用公钥验证数字签名
            Signature signature = Signature.getInstance(KEY_RSA_SIGNATURE);
            signature.initVerify(key);
            signature.update(data);
            flag = signature.verify(decryptBase64(sign));
        } catch (Exception e) {
            log.error("校验签名出错: ", e);
        }
        return flag;
    }

    /**
     * Base64解码封装
     *
     * @param str 解码字符串
     * @return 解码后的byte数组
     */
    private static byte[] decryptBase64(String str) {
        return Base64.getMimeDecoder().decode(str);
    }

    /**
     * Base64编码封装
     *
     * @param bytes 编码byte数组
     * @return 编码生成的字符串
     */
    private static String encryptBase64(byte[] bytes) {
        return Base64.getMimeEncoder().encodeToString(bytes);
    }

    public static void main(String[] args) {
        Map map = init();
        System.out.println("private key: " + map.get(KEY_RSA_PRIVATE_KEY));
        System.out.println("public key: " + map.get(KEY_RSA_PUBLIC_KEY));
    }

}


package com.cherry.base.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.cherry.base.exception.BaseExceptionEnum;
import com.cherry.base.exception.CherryException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author cherry
 * @version 1.0.0
 * Description
 * Date 2024年10月12日 15:35:00
 * ClassName CherryAesUtil
 * packageName com.cherry.animal.base.utils
 */
@SuppressWarnings("unused")
public class CherryAesUtil {
    private final static String key = "9cfcf268818291a6c65b155e864b8eff";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding"; // 使用 GCM 模式自动提供加密和认证
    private static final int GCM_TAG_LENGTH = 128; // 认证标签长度（位）
    private static final int GCM_IV_LENGTH = 12; // GCM 模式推荐的 IV 长度（字节）
    private static final int ITERATIONS = 65536; // PBKDF2 迭代次数
    private static final int KEY_LENGTH = 256; // AES-256 密钥长度

    // key为自定义密钥
    public static String encrypt(String encrypt) {
        if (CherryStringUtil.isBlank(encrypt)) {
            throw new CherryException(BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(), "加密字段为空");
        }
        // 构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, getBytes());
        return aes.encryptHex(encrypt);
    }

    // key为自定义密钥
    public static String decrypt(String decrypt) {
        if (CherryStringUtil.isBlank(decrypt)) {
            throw new CherryException(BaseExceptionEnum.SYSTEM_ERROR.getErrorCode(), "解密字段为空");
        }
        // 构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, getBytes());
        // 解密为字符串
        return aes.decryptStr(decrypt, CharsetUtil.CHARSET_UTF_8);
    }

    private static byte[] getBytes() {
        // 生成密钥
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        // 在密钥生成时必须为128/192/256 bits（位），本案例中使用256位
        // 故需进行判断
        // byte，一个字节，8位，故需要达到256位，需要32个字节
        if (bytes.length != 32) {
            // 创建32字节的byte数组
            byte[] b = new byte[32];
            if (bytes.length < 32) {
                /*
                 * 将自定义密钥添加到b数组
                 * 方法：System.arraycopy
                 * 参数：
                 * src：the source array要插入的数组
                 * srcPos：starting position in the source array插入数组的起始位置
                 * dest：the destination array被插入的数组
                 * destPos：starting position in the destination data被插入数组插入时的起始位置
                 * length：the number of array elements to be copied要插入的数组的长度
                 */
                System.arraycopy(bytes, 0, b, 0, bytes.length);
            }
            bytes = b;
        }
        return bytes;
    }


    // 加密方法
    public static String encrypt(String plaintext, Timestamp timestamp) throws Exception {
        // salt
        String salt = Arrays.toString(getBytes());

        // 生成随机 IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // 从密码派生密钥
        SecretKey secretKey = deriveKey(
                CherryDateUtil.format(timestamp, CherryDateUtil.CherryDatePattern.PURE_DATETIME_PATTERN),
                salt.getBytes(StandardCharsets.UTF_8)
        );

        // 初始化 Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        // 执行加密
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // 合并 IV 和密文
        byte[] encryptedBytes = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryptedBytes, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryptedBytes, iv.length, ciphertext.length);

        // 转换为 Base64 字符串
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 解密方法
    public static String decrypt(String ciphertext, Timestamp timestamp) throws Exception {
        // salt
        String salt = Arrays.toString(getBytes());

        // 解码 Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);

        // 分离 IV 和密文
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] ciphertextBytes = new byte[encryptedBytes.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedBytes, 0, iv, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedBytes, GCM_IV_LENGTH, ciphertextBytes, 0, ciphertextBytes.length);

        // 从密码派生密钥
        SecretKey secretKey = deriveKey(
                CherryDateUtil.format(timestamp, CherryDateUtil.CherryDatePattern.PURE_DATETIME_PATTERN),
                salt.getBytes(StandardCharsets.UTF_8)
        );

        // 初始化 Cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // 执行解密
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // 使用 PBKDF2 派生密钥
    private static SecretKey deriveKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }

    // 生成随机盐值
    public static String generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static void main(String[] args) throws Exception {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        String encrypted = CherryAesUtil.encrypt("123456", timestamp);
        System.out.println(encrypted);
        System.out.println(CherryAesUtil.decrypt(encrypted, timestamp));
    }

}

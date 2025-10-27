package com.hrimDev.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
@Component
public class AESUtil {

    private static final Charset ENCODING_TYPE = StandardCharsets.UTF_8;
    private static final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";

    private static final String KEY_STRING = "HYERIM";
    private static final String SALT = "DEMO";

    private static final int KEY_SIZE = 128;
    private static final int ITERATION_COUNT = 65536;

    private final SecretKey secretKey;
    private final IvParameterSpec ivParameterSpec;

    public AESUtil() {
        log.info("AESUtil 생성");
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; //16byte = 128bit
        secureRandom.nextBytes(iv);

        ivParameterSpec = new IvParameterSpec(iv);
        secretKey = generateAESKey();

    }

    /**
     * AES 대칭키 생성하는 메소드
     * @return SecretKey 형태의 대칭키
     */
    public SecretKey generateAESKey() {
        try{
            // keyString과 salt를 이용해 PBEKeySpec을 생성
            KeySpec spec = new PBEKeySpec(KEY_STRING.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_SIZE);

            //PBKDF2 암호화에 사용할 key 생성
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating AES Key: alg", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Error generating AES Key: invalid key", e);
        }
    }

    /**
     * AES 암호화
     * @param plaintext
     * @return 암호문
     */
    public String encrypt(String plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(ENCODING_TYPE));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    /**
     * AES 복호화
     * @param cipherText
     * @return
     */
    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(INSTANCE_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

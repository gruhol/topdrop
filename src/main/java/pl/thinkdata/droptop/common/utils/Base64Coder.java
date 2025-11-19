package pl.thinkdata.droptop.common.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Coder {

    public static String decodeBase64(String toDecode) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(toDecode);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String encodeBase64(String toEncode) {
        try {
            return Base64.getEncoder().encodeToString(toEncode.getBytes());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

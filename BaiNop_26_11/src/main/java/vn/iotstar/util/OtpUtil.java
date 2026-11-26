package vn.iotstar.util;

import java.security.SecureRandom;

/** Sinh mã OTP 6 chữ số bằng SecureRandom. */
public class OtpUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpUtil() {
    }

    public static String generate() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}

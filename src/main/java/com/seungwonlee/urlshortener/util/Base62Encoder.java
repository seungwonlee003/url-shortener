package com.seungwonlee.urlshortener.util;

public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    public static String encode(Long number) {
        StringBuilder encodedString = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % BASE);
            encodedString.insert(0, BASE62_CHARS.charAt(remainder));
            number /= BASE;
        }

        return encodedString.length() == 0 ? "0" : encodedString.toString();
    }
}

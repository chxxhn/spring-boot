package com.example.mariadb_demo.config;

public class MaskingUtil {

    public static String maskName(String name) {
        if (name == null || name.isBlank()) return "";

        int len = name.length();

        if (len == 1) {
            return "*";
        } else if (len == 2) {
            return name.charAt(0) + "*";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(name.charAt(0));
            for (int i = 1; i < len - 1; i++) {
                sb.append("*");
            }
            sb.append(name.charAt(len - 1));
            return sb.toString();
        }
    }
}


package com.ll.mb.standard.util;

public class Ut {
    public static class str {

        // 가장 첫번째 글자를 소문자로 만든다. / lcfirst(lowercase character first)
        public static String lcfirst(String str) {
            if (str == null || str.isEmpty()) return str;

            return str.substring(0, 1).toLowerCase() + str.substring(1);
        }
    }
}

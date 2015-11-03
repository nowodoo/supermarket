package com.zach.util;

/**
 * Created by Administrator on 2015/10/28 0028.
 */
public class PswUtil {
    public static String getEncodedPsw(String password){
        String origin = password;
        int key = 822;
        char result[] = new char[origin.length()];

        int c1 = 52845;
        int c2 = 22719;

        int t;  //用户获取下一个key

        for(int i = 0; i < origin.length(); i++){
            result[i] = (char)(charToByte(origin.charAt(i))^(key >> 8));
            t = (int)(result[i])+key;
            key = t;  //在这里刷新key值
        }

        String result_s = new String(result);
        return result_s;
    }

    public static byte charToByte(char c) {
        byte b = (byte)c;
        return b;
    }
}

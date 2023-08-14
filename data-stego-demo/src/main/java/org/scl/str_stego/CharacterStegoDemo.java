package org.scl.str_stego;

import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Auther: sichaolong
 * @Date: 2023/7/28 14:24
 * @Description:
 * 0宽字符隐写demo，采用二进制
 */
public class CharacterStegoDemo {

    // 0宽字符正则模式
    private Pattern zeroWidthCharacterPattern = Pattern.compile("[\\u200B-\\u200F\\u2060-\\u2069\\u202A-\\u202E\\uFEFF]");



    public static void main(String[] args) throws Exception {
        // String str = "hello,世界！";
        // int index = 7;
        // char c = str.charAt(index);
        // System.out.println(c); // 界
        // nt codePoint = str.codePointAt(7);
        // System.out.println(codePoint); // 30028

        // 加密
        String encrypted = encrypt("sichaolong", "1234567812345678");
        System.out.println(String.format("加密完成【%s】，长度为：【%s】",encrypted,encrypted.length()));

        // 解密
        String secret = decrypt(encrypted);
        System.out.println("解密出来的内容: " + secret);


        /**
         * 开始隐写....
         * 需要隐藏的内容为【1234567812345678】，长度为：16，转为二进制为【110001 110010 110011 110100 110101 110110 110111 111000 110001 110010 110011 110100 110101 110110 110111 111000】，长度为 ：111
         * 需要隐藏的内容为【1234567812345678】，长度为：16，转为0宽字符为【​‎​‎‌‎‌‎‌‎​‎‍‎​‎​‎‌‎‌‎​‎‌‎‍‎​‎​‎‌‎‌‎​‎​‎‍‎​‎​‎‌‎​‎‌‎‌‎‍‎​‎​‎‌‎​‎‌‎​‎‍‎​‎​‎‌‎​‎​‎‌‎‍‎​‎​‎‌‎​‎​‎​‎‍‎​‎​‎​‎‌‎‌‎‌‎‍‎​‎​‎‌‎‌‎‌‎​‎‍‎​‎​‎‌‎‌‎​‎‌‎‍‎​‎​‎‌‎‌‎​‎​‎‍‎​‎​‎‌‎​‎‌‎‌‎‍‎​‎​‎‌‎​‎‌‎​‎‍‎​‎​‎‌‎​‎​‎‌‎‍‎​‎​‎‌‎​‎​‎​‎‍‎​‎​‎​‎‌‎‌‎‌】，长度为 ：221
         * 加密完成【​‎​‎‌‎‌‎‌‎​‎‍‎​‎​‎‌‎‌‎​‎‌‎‍‎​‎​‎‌‎‌‎​‎​‎‍‎​‎​‎‌‎​‎‌‎‌‎‍‎​‎​‎‌‎​‎‌‎​‎‍‎​‎​‎‌‎​‎​‎‌‎‍‎​‎​‎‌‎​‎​‎​‎‍‎​‎​‎​‎‌‎‌‎‌‎‍‎​‎​‎‌‎‌‎‌‎​‎‍‎​‎​‎‌‎‌‎​‎‌‎‍‎​‎​‎‌‎‌‎​‎​‎‍‎​‎​‎‌‎​‎‌‎‌‎‍‎​‎​‎‌‎​‎‌‎​‎‍‎​‎​‎‌‎​‎​‎‌‎‍‎​‎​‎‌‎​‎​‎​‎‍‎​‎​‎​‎‌‎‌‎‌sichaolong】，长度为：【231】
         * 开始解密...
         * 原文内容: sichaolong
         * 原文内容 len: 10
         * 零宽字符: ​‎​‎‌‎‌‎‌‎​‎‍‎​‎​‎‌‎‌‎​‎‌‎‍‎​‎​‎‌‎‌‎​‎​‎‍‎​‎​‎‌‎​‎‌‎‌‎‍‎​‎​‎‌‎​‎‌‎​‎‍‎​‎​‎‌‎​‎​‎‌‎‍‎​‎​‎‌‎​‎​‎​‎‍‎​‎​‎​‎‌‎‌‎‌‎‍‎​‎​‎‌‎‌‎‌‎​‎‍‎​‎​‎‌‎‌‎​‎‌‎‍‎​‎​‎‌‎‌‎​‎​‎‍‎​‎​‎‌‎​‎‌‎‌‎‍‎​‎​‎‌‎​‎‌‎​‎‍‎​‎​‎‌‎​‎​‎‌‎‍‎​‎​‎‌‎​‎​‎​‎‍‎​‎​‎​‎‌‎‌‎‌
         * 零宽字符 len: 221
         * 解密出来的内容: 1234567812345678
         */



    }


    /**
     * 隐写
     * @param content
     * @param secret
     * @return
     * @throws Exception
     */
    public static String encrypt(String content,String secret) throws Exception {
        if(StrUtil.isBlank(secret)){
            throw new Exception("secret mast not blank !");
        }
        if(StrUtil.isBlankIfStr(content)){
            return content;
        }
        System.out.println("开始隐写....");
        int len = content.length();
        int randomIndex = RandomUtil.randomInt(0, len+1);

        // 码点值转为二进制
        String binaryStr = Arrays.stream(secret.split(""))
                .map(i -> Integer.toString(i.codePointAt(0),2))
                .collect(Collectors.joining(" "));
        System.out.println(String.format("需要隐藏的内容为【%s】，长度为：%s，转为二进制为【%s】，长度为 ：%s",secret,secret.length(),binaryStr,binaryStr.length()));

        // 替换为0宽字符
        String cryptStr = Arrays.stream(binaryStr.split("")).map(i -> {
            // 零宽空格
            if (i.equals("1")) return Character.toString(8203);
            // 零宽非连字符
            if (i.equals("0")) return Character.toString(8204);
                // 空格-->零宽连字符
            else return Character.toString(8205);
        }).collect(Collectors.joining(Character.toString(8206))); // 0宽字符的分隔符
        System.out.println(String.format("需要隐藏的内容为【%s】，长度为：%s，转为0宽字符为【%s】，长度为 ：%s",secret,secret.length(),cryptStr,cryptStr.length()));

        // 随机插入
        StringBuilder sb = new StringBuilder(content);
        sb.insert(randomIndex,cryptStr);
        return sb.toString();


    }

    /**
     *  隐写内容提取
     * @param content
     * @return
     */
    public static String decrypt(String content){
        if(StrUtil.isBlankIfStr(content)){
            System.out.println("解密内容为空！");
            return null;
        }
        // 当前字符
        System.out.println("开始解密...");

        // 删除特殊字符
        Pattern pattern1 = Pattern.compile("[\\u200b-\\u200f\\uFEFF\\u202a-\\u202e]");
        Matcher matcher1 = pattern1.matcher(content);
        String targetStr = matcher1.replaceAll("");
        System.out.println("原文内容: " + targetStr);
        System.out.println("原文内容 len: " + targetStr.length());

        // 提取特殊字符
        Pattern pattern2 = Pattern.compile("[^\\u200b-\\u200f\\uFEFF\\u202a-\\u202e]");
        Matcher matcher2 = pattern2.matcher(content);
        String secretZeroWidthStr = matcher2.replaceAll("");
        System.out.println("零宽字符: " + secretZeroWidthStr);
        System.out.println("零宽字符 len: " + secretZeroWidthStr.length());

        String binaryStr = Arrays.stream(secretZeroWidthStr.split(Character.toString(8206))).map(i -> {
            if (i.equals(Character.toString(8203))) return "1";
            if (i.equals(Character.toString(8204))) return "0";
            else return " ";
        }).collect(Collectors.joining(""));

        String secret = Arrays.stream(binaryStr.split(" "))
                .map(i -> Character.toString(Integer.parseInt(i, 2)))
                .collect(Collectors.joining(""));
        return secret;

    }
}

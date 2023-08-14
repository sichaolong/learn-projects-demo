package org.scl.str_stego;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Auther: sichaolong
 * @Date: 2023/8/11 13:36
 * @Description: 0宽字符隐写util
 * 1、适用于文本类型隐写0宽字符，支持提取、清除
 * 2、结合业务分析，需要隐藏写数字ID，因此定义10种零宽字符与0-9数字对应。
 */

public class ZeroWidthCharacterStegoUtil {

    // 0宽字符正则模式

    // 全部零宽字符
    private static Pattern allZwcPattern = Pattern.compile("[\\u200B-\\u200F\\u2060-\\u2069\\u202A-\\u202E\\uFEFF\\uFE00-\\uFE0F]");

    // 非零宽字符
    private static Pattern nonZwcPattern = Pattern.compile("[^\\u200B-\\u200F\\u2060-\\u2069\\u202A-\\u202E\\uFEFF\\uFE00-\\uFE0F]");

    // 隐写用到的零宽字符，共10种
    private static Pattern xopZwcPattern = Pattern.compile("[\\uFE06-\\uFE0F]");
    private static Pattern nonXopZwcPattern = Pattern.compile("[^\\uFE06-\\uFE0F]");


    /**
     * 隐写
     *
     * @param content
     * @param secretNumStr
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String secretNumStr) throws Exception {
        if (!NumberUtil.isNumber(secretNumStr)) {
            throw new Exception("secretNum mast be number str !");
        }
        if (StrUtil.isBlankIfStr(content)) {
            return content;
        }

        int len = content.length();
        int secretNumLen = secretNumStr.length();
        printProcessLog("开始准备隐写...");
        printProcessLog("目标文本：" + content);
        printProcessLog("目标文本长度：" + content.length());
        printProcessLog("隐写内容：" + secretNumStr);
        printProcessLog("隐写内容长度：" + secretNumStr.length());

        StringBuilder sb = new StringBuilder(content);
        // 字符0-9转为0宽字符
        StringBuilder secretSb = new StringBuilder();
        for (int i = 0; i < secretNumLen; i++) {
            Character item = secretNumStr.charAt(i);
            Character zwc = ZeroWidthCharacterEnum.getZwcBySecretNumCharacter(item);
            sb.append(zwc);
        }
        printProcessLog("0宽字符转换结果：" + secretSb);
        printProcessLog("0宽字符转换结果长度：" + secretSb.length());
        // 随机插入
        int randomIndex = RandomUtil.randomInt(0, len + 1);
        sb.insert(randomIndex, secretSb);
        return sb.toString();


    }

    public static void main(String[] args) throws Exception {

        // 测试隐写
        String content = "sichaolong";
        String secretNum = "12335435";
        String encryptedContent = encrypt(content, secretNum);
        printProcessLog("0宽字符隐写结果：" + encryptedContent);
        printProcessLog("0宽字符隐写结果长度：" + encryptedContent.length());

        /**
         * 开始准备隐写...
         * 目标文本：sichaolong
         * 目标文本长度：10
         * 隐写内容：1233
         * 隐写内容长度：4
         * 0宽字符转换结果：
         * 0宽字符转换结果长度：0
         * 0宽字符隐写结果：sichaolong︋︌︍︍
         * 0宽字符隐写结果长度：14
         */

        String decryptContent = decrypt(encryptedContent);
        printProcessLog("从零宽字符解密出: " + decryptContent);
        printProcessLog("从零宽字符解密出 len: " + decryptContent.length());
    }


    /**
     * 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(String content) throws Exception {
        if (StrUtil.isBlankIfStr(content)) {
            printProcessLog("解密内容为空！");
            return null;
        }
        printProcessLog("开始解密...");

        // 清除全部零宽字符
        String targetContent = clearAllZwc(content);
        System.out.println("解密出原文内容: " + targetContent);
        System.out.println("解密出原文内容 len: " + targetContent.length());

        // 提取特殊字符
        Matcher nonXopZwcMatcher = nonXopZwcPattern.matcher(content);
        String xopZwc = nonXopZwcMatcher.replaceAll("");
        if (StrUtil.isBlank(xopZwc)) {
            printProcessLog("待解密内容为空！");
            return null;
        }
        printProcessLog("提取解密内容： " + xopZwc);
        printProcessLog("提取解密内容 len： " + xopZwc.length());

        // 找出映射关系,完成解密
        StringBuilder sb = new StringBuilder();
        int len = xopZwc.length();
        for (int i = 0; i < len; i++) {

            sb.append(ZeroWidthCharacterEnum.getSecretNumCharacterByZwc(xopZwc.charAt(i)));
        }
        return sb.toString();

    }


    /**
     * 使用正则清除全部零宽字符
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String clearAllZwc(String content) throws Exception {
        if (StrUtil.isBlankIfStr(content)) {
            printProcessLog("待清空内容为空！");
            return null;
        }
        // 清除全部零宽字符
        Matcher allZwcMatcher = allZwcPattern.matcher(content);
        String targetContent = allZwcMatcher.replaceAll("");
        return targetContent;
    }

    /**
     * 获取字符的码点值
     *
     * @param character 字符
     * @return
     */
    public static int getCodePoint(Character character) throws Exception {
        if (Objects.isNull(character)) {
            throw new Exception("param must not be null!");
        }
        String str = String.valueOf(character);
        Matcher matcher = allZwcPattern.matcher(str);
        if (!matcher.find()) {
            throw new Exception("param not be zeroWidthCharacter!");
        }
        return str.codePointAt(0);
    }

    /**
     * 打印日志工具
     *
     * @param log
     */
    private static void printProcessLog(String log) {
        System.out.println(log);
    }
}

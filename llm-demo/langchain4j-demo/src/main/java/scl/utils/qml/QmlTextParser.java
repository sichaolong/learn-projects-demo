package scl.utils.qml;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scl.utils.Utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sichaolong
 * @createdate 2024/4/22 13:43
 */
public class QmlTextParser {
    final static Logger logger = LoggerFactory.getLogger(QmlTextParser.class);

    private static final Pattern QML_TAG_PATTERN =
        Pattern.compile("</?([\\w-]+)(\\s+[\\w-]+(=\"[^\"]*?\")?)*\\s*/?>");
    private static final Pattern QML_MATH_PATTERN =
        Pattern.compile("(<math[^>]+?latex=\")([^\"]*)(\"[\\s\\S]+?</math>)");
    private static final Pattern MATH_PATTERN =
        Pattern.compile("<math[^>]*>[\\s\\S]+?</math>");
    private static final Map<String, String> EQUAL_HTML_MAP = new HashMap<String, String>() {
        {
            put("<sub>", "_");
            put("<sup>", "^");
            put("</sub>", " ");
            put("</sup>", " ");
        }
    };

    /**
     * qml中，起到隔离作用的标签，也就是标签内外的字符肯定是隔离的，而不是粘粘的。
     * 这些标签再被替换掉的时候，需要用空格占位
     */
    private final static String[] IsolationTagNames =
        {"stem", "og", "op", "sq", "p", "br", "td", "bk", "media-ref", "img"};

    /**
     * 默认使用AlphaTex处理公式
     *
     * @param xmlText
     * @return
     */
    public static String parseText(String xmlText) {
        return parseText(xmlText, true);
    }

    /**
     * 将一段QML 提取纯文本，用于分词。
     * 本程序中已经考虑不同标签之间的文本是否应该隔离还是粘粘。
     * 同时修正了一些影响查重的连续字符。比如连续的多个空格，连续的多个下划线，全角都转为半角等。
     *
     * @param xmlText
     * @param useAlphaTex 公式是否转换成AlphaTex. 如果是true则转换成AlphaTex, 否则使用LaTex
     * @return
     */
    public static String parseText(String xmlText, boolean useAlphaTex) {
        if (StringUtils.isEmpty(xmlText)) {
            return null;
        }

        //替换math标签为Latex或者AlphaTex，因为MathML无法直接参与搜索查重
        //Math公式替换为更有利于查重的AlphaTex
        String rlt = xmlText;
        rlt = replaceVideoAndAudio(rlt);
        //先去掉qml tag，然后再unescapeHtml才是正确的
        rlt = replaceQmlTag(rlt, true);
        rlt = StringEscapeUtils.unescapeHtml(rlt);

        //全角转半角
        rlt = Utilities.sbc2dbcCase(rlt);

        //替换掉干扰查重的字符
        rlt = replaceNoisyWord(rlt, false);

        return rlt;
    }

    /**
     * @param xmlText
     * @param htmlTagSplit 0=前后需要有分隔的html标签添加空格,1前后需要有分隔的html标签添加换行(\r\n)
     * @return
     */
    public static String parseTextWithOutBlank(String xmlText, int htmlTagSplit) {
        if (StringUtils.isEmpty(xmlText)) {
            return null;
        }

        String rlt = xmlText;
        rlt = replaceVideoAndAudio(rlt);
        //先去掉qml tag，然后再unescapeHtml才是正确的
        rlt = replaceQmlTag(rlt, false);
        rlt = StringEscapeUtils.unescapeHtml(rlt);

        //全角转半角
        rlt = Utilities.sbc2dbcCase(rlt);

        //替换掉干扰查重的字符
        rlt = replaceNoisyWord(rlt, htmlTagSplit == 1);


        return rlt;
    }

    /**
     * 将一段QML 提取纯文本，公式使用LaTex
     *
     * @param xmlText
     * @return
     */
    public static String parseTextUseLaTex(String xmlText) {
        if (StringUtils.isEmpty(xmlText)) {
            return null;
        }

        //Math公式替换为LaTex
        String rlt = replaceMathWithLaTex(xmlText, true);
        rlt = replaceVideoAndAudio(rlt);
        //先去掉qml tag，然后再unescapeHtml才是正确的
        rlt = replaceQmlTag(rlt, true);
        rlt = StringEscapeUtils.unescapeHtml(rlt);

        //全角转半角
        rlt = Utilities.sbc2dbcCase(rlt);

        //替换掉干扰查重的字符
        rlt = replaceNoisyWord(rlt, false);

        return rlt;
    }

    private static String replaceVideoAndAudio(String rlt) {
        return rlt.replaceAll("<media-ref[^>]*>", "【此处有音视频】");
    }


    /**
     * 将qml中的math标签替换为文本类型的公式，LaTeX
     *
     * @param qmlText
     * @param useLaTexTag 是否使用$$来包裹LaTex公式，避免与其他部分粘粘，影响分词
     * @return
     */
    public static String replaceMathWithLaTex(String qmlText, boolean useLaTexTag) {
        Matcher matcher = QML_MATH_PATTERN.matcher(qmlText);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            //获取LaTex的内容
            String texMath = org.apache.commons.lang.StringUtils.strip(matcher.group(2), "$");
            //因为qmlText是xml格式，所以内容需要转义一下。比如3<x>5，不转义的话，<x>就被识别为qml tag了
            //前后加上空格或$$，避免与其他部分粘粘，影响分词
            texMath = (useLaTexTag ? "$$" : " ") + StringEscapeUtils.escapeXml(texMath) + (useLaTexTag ? "$$" : " ");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(texMath));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    private static boolean isIsolationTag(String tagName) {
        for (String t : IsolationTagNames) {
            if (t.equalsIgnoreCase(tagName)) {
                return true;
            }
        }

        return false;
    }
    /**
     *
     * @param textStem 文本
     * @param replaceIsolationTagWithBlank  true=isolationTag 替换为空格，false=去掉IsolationTag
     * @return
     */
    public static String replaceQmlTag(String textStem, boolean replaceIsolationTagWithBlank) {
        //替换掉html的标签
        for (Map.Entry<String, String> entry : EQUAL_HTML_MAP.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            textStem = textStem.replaceAll(key, val);
        }
        Matcher m = QML_TAG_PATTERN.matcher(textStem);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            //如果是隔离性标签，那么这个标签不管是开始还是结束都应该用空格替换。
            //多余的空格会在后续的处理流程中去掉
            String tagName = m.group(1).trim();
            String replacement = replaceIsolationTagWithBlank && isIsolationTag(tagName) ? " " : "";
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(replaceQmlTag("你好 <bk index=\"1\" size=\"13\" type=\"underline\"/> 122", true));
    }

    /**
     * @param keepNewLineCharacter 是否保留换行符
     * @return
     */
    public static String replaceNoisyWord(String textStem, boolean keepNewLineCharacter) {
        //多个换行符替换为1个
        //依次执行的替换有：
        // 1. 换行变为一个空格。为啥不替换为空字符串呢？
        // 因为上一行结尾如果是英文，下一行也是英文，没有空格的话，就会粘连在一起，
        // 分词时就错误了。影响solr查找相似试题
        // 2. "\u00a0"和\t变为普通空格
        // 3. 连续多个空格变成一个
        // 4. 连续多个下划线变成一个下划线
        textStem = textStem
            .replaceAll("-{2,}", "-")
            .replaceAll("[_]{2,}", "_")
            .trim();
        return keepNewLineCharacter ? textStem.replaceAll("[\\r\\n]{2,}","\\\r\\\n").replaceAll("[^\\S\\r\\n]{2,}", " ").trim()
            : textStem.replaceAll("[\\n\u00a0\\t]{2,}", " ")
            .replaceAll("\\s{2,}", " ").trim();
    }

    /**
     * 只保留分词必要的空格，也就是英文词汇之间的空格。
     * 但是，根据大家的讨论，对中文分词可能不利。
     * 比如“中国 人” 中间有一个空格的话，分词结果是2个词，
     * 一旦调用了该方法，就变成中国人一个词了
     *
     * @param text
     * @return
     */
    public static String retainRequisiteSpace(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        StringBuilder sb = new StringBuilder(text.length());
        String[] phrases = text.split("\\s+");
        boolean lastCharIsLetterOrDigit = false;
        for (int i = 0; i < phrases.length; i++) {
            String ph = phrases[i].trim();
            char firstChar = ph.charAt(0);
            boolean phStartIsLetterOrDigit = CharUtils.isAsciiAlphanumeric(firstChar);
            if (lastCharIsLetterOrDigit && phStartIsLetterOrDigit) {
                sb.append(" ");
            }
            sb.append(ph);

            char lastChar = ph.charAt(ph.length() - 1);
            lastCharIsLetterOrDigit = CharUtils.isAsciiAlphanumeric(lastChar);
        }
        return sb.toString();
    }
}


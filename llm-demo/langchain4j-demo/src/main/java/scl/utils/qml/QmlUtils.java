package scl.utils.qml;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2024/5/10 10:46
 */
public class QmlUtils {

    private static Pattern pattern = Pattern.compile("\\d+");

    public static Double extractNumbersFromString(String source) {
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        }
        return null;
    }

    /**
     * 检查题号是否合法。要求16位，全部数字
     *
     * @param quesId 待检测题号
     * @return
     */
    public static boolean isStandardQuestionId(String quesId) {
        if (quesId == null) {
            return false;
        }
        return quesId.trim().matches("^\\d{16}$");
    }

    public static double getPointByPx(double px) {
        return px * .75;
    }

    public static double getFontSizeByPx(double px) {
        return getPointByPx(px);
    }


    public static int countSubQuestionsFromAnswerQml(String answerQml) {
        int count = 0;
        int pos = answerQml.indexOf("</sq>");
        while (pos >= 0) {
            count++;
            pos += 5;
            pos = answerQml.indexOf("</sq>", pos);
        }
        return count;
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static String getWhiteSpaces(int count, String blankPlaceholder) {
        return new String(new char[count]).replace("\0", blankPlaceholder);
    }

    public static <T extends QmlElement> List<T> findElementsRecursively(QmlElement rootQmlElement, Class<T> elementType) {
        List<T> results = new ArrayList<>();

        if (rootQmlElement.getClass().isAssignableFrom(elementType)) {
            results.add((T) rootQmlElement);
        }

        for (QmlElement qmlElement : rootQmlElement.getChildElements()) {
            results.addAll(findElementsRecursively(qmlElement, elementType));
        }

        return results;
    }

    /**
     * 找到所有没孩子的元素
     *
     * @param rootQmlElement
     * @return
     */
    public static List<QmlElement> findLeafElementsRecursively(QmlElement rootQmlElement) {
        List<QmlElement> results = new ArrayList<>();

        if (!rootQmlElement.hasElements()) {
            results.add(rootQmlElement);
        }

        for (QmlElement qmlElement : rootQmlElement.getChildElements()) {
            results.addAll(findLeafElementsRecursively(qmlElement));
        }

        return results;
    }

    public static <T extends QmlElement> T findFirstDescendant(QmlElement rootQmlElement, Class<T> elementType) {
        List<T> descendants = findElementsRecursively(rootQmlElement, elementType);
        if (descendants != null && descendants.size() > 0) {
            return descendants.get(0);
        }
        return null;
    }

    /**
     * 递归遍历删除符合指定断言的子元素
     *
     * @param rootQmlElement
     * @param predicate
     */
    public static void removeChildrenRecursively(QmlElement rootQmlElement, Predicate<QmlElement> predicate) {
        for (int i = rootQmlElement.getChildElements().size() - 1; i >= 0; i--) {
            QmlElement ele = rootQmlElement.getChildElements().get(i);
            if (predicate.test(ele)) {
                rootQmlElement.getChildElements().remove(ele);
            } else {
                removeChildrenRecursively(ele, predicate);
            }
        }
    }

    public static List<QmlElement> findElementsRecursively(QmlElement rootQmlElement, String tagName) {
        List<QmlElement> results = new ArrayList<>();

        if (rootQmlElement.getTagName().equals(tagName)) {
            results.add(rootQmlElement);
        }

        for (QmlElement qmlElement : rootQmlElement.getChildElements()) {
            results.addAll(findElementsRecursively(qmlElement, tagName));
        }

        return results;
    }

    public static boolean hasDescendant(QmlElement rootQmlElement, String tagName) {
        if (rootQmlElement.getTagName().equals(tagName)) {
            return true;
        }

        for (QmlElement qmlElement : rootQmlElement.getChildElements()) {
            if (hasDescendant(qmlElement, tagName)) {
                return true;
            }
        }

        return false;
    }

    public static List<Integer> splitBorderAttr(String borderAttrValue) {

        if (isEmpty(borderAttrValue)) {
            return null;
        }
        //Arrays.asList: Returns a fixed-size list backed by the specified array.
        //You can't add to it; you can't remove from it. You can't structurally modify the List.
        List<String> borderList = new LinkedList<>(Arrays.asList(borderAttrValue.split("\\s+")));
        //提升容错性，如果不够4个，自动补上默认值
        while (borderList.size() < 4) {
            borderList.add("1");
        }
        //提升容错性，如果超过4个，自动去掉
        while (borderList.size() > 4) {
            borderList.remove(4);
        }

        //提升容错性，如果不是合法边框宽度，自动替换为1
        return borderList.stream().map(border -> {
            Double borderNumber = extractNumbersFromString(border);
            if (borderNumber == null) {
                return 1;
            }
            return borderNumber.intValue();
        }).collect(Collectors.toList());
    }


    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 将选择题的选项索引翻译为字母。比如1,2翻译为A,B
     *
     * @param choiceIndexes 选项索引。可以多个，用逗号分割
     * @return
     */
    public static String translateChoiceIndexesToAlphabets(String choiceIndexes) {
        String result = "";
        if (StringUtils.isEmpty(choiceIndexes)) {
            return "";
        }
        //单选或者多选题的答案处理
        String[] optionIndexes = choiceIndexes.split(",");
        for (String strIndex : optionIndexes) {
            try {
                //翻译选择题选项的值
                int index = Integer.parseInt(strIndex.trim());

                //index=1,代表A选项
                String alphabet = String.valueOf((char) (64 + index));
                result += alphabet;
            } catch (NumberFormatException ex) {
                //index 异常，无法转换为数字，忽略
                continue;
            }
        }

        return result;
    }

    public static String removeReturns(String input) {
        return input.replaceAll("[\\r\\n]", "");
    }

    /**
     * 获取元素集合的最后一个，没有则返回null
     *
     * @param elements
     * @return
     */
    public static QmlElement findLastOrDefault(List<QmlElement> elements) {
        return elements.stream().reduce((prev, next) -> next).orElse(null);
    }

    public static String unescapeXml(String value) {
        if (value == null) {
            return value;
        }
        return value.replace("&lt;", "<").replace("&gt;", ">");
    }

    public static String escapeXml(String value) {
        if (value == null) {
            return value;
        }
        return value.replace("<", "&lt;").replace(">", "&gt;");
    }
}

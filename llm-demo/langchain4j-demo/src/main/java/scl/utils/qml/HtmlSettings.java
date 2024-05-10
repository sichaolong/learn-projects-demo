package scl.utils.qml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sichaolong
 * @createdate 2024/5/10 10:53
 */
public class HtmlSettings {
    public int MIN_BLANK_SIZE = 6;
    public String BLANK_PLACEHOLDER = "　";//英文空格、中文全角空格等
    public boolean RESERVE_FONT_SIZE = false;
    /**
     * 是否保留QML中的字体。默认true。
     * 如果设置为false，也只会忽略常用字体比如宋体、微软雅黑、Times New Roman、楷体。(INGNORABLE_FONTS参数可以指定忽略的字体）
     * 其他字体仍然会保留（因为要防止特殊字体比如Symbol忽略后，使用者无法正确的渲染字符了）
     * 另外很重要的一点：QML转HTML后，为了保证空格排版和word中效果一致，我们对span元素中连续两个及以上空格的文本做了特殊处理，需要保留字体才会生效
     */
    public boolean RESERVE_FONT = true;
    /**
     * 当RESERVE_FONT=false时，此参数配置可忽略的字体
     */
    public List<String> INGNORABLE_FONTS = new ArrayList<>(Arrays.asList("宋体", "Times New Roman", "微软雅黑", "楷体"));

    public boolean RESERVE_P_INDENT = false;
    public boolean RESERVE_P_ALIGN = true;
    public boolean RESERVE_P_SPACE_BEFORE = false;
    public boolean RESERVE_P_SPACE_AFTER = false;

    public boolean RESERVE_P_LINE_HEIGHT = false;
    public boolean RESERVE_P_LEFT = false;
    public boolean RESERVE_P_RIGHT = false;

    /**
     * 每个试题前的空行数量
     */
    public int EMPTY_LINES_BEFORE_EACH_QUESTION = 1;

    /**
     * 图片是否缩放到视窗大小
     */
    public boolean IMG_RESIZE = false;

    /**
     * 是否生成题号，默认是false。 对于复合题，只控制主题干是否生成题号，里面的小题小问序号还是会生成。
     */
    public boolean GEN_MAIN_QUESTION_NO = false;

    /**
     * 答题空bk元素转成span时，是否生成题号，增加圆圈序号
     */
    public boolean GEN_BLANK_SPAN_INDEX = false;
}


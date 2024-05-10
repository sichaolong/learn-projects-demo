package scl.utils.qml;

/**
 * @author sichaolong
 * @createdate 2024/5/10 10:44
 */
public class Attr {
    /**
     * span元素的属性
     */
    public static final String ATTR_WORD_FONT = "word-font";
    public static final String ATTR_WORD_FONT_SIZE = "word-font-size";
    public static final String ATTR_B = "b";
    public static final String ATTR_I = "i";
    public static final String ATTR_EM = "em";
    public static final String ATTR_U = "u";
    public static final String ATTR_WAVE = "wave";
    public static final String ATTR_STRIKE = "strike";
    /**
     * p 和 span table 都有的属性
     */
    public static final String ATTR_ALIGN = "align";
    public static final String ATTR_CLASS = "class";

    /**
     * p元素的属性
     */
    public static final String ATTR_INDENT = "indent";
    public static final String ATTR_SPACE_BEFORE = "space-before";
    public static final String ATTR_SPACE_AFTER = "space-after";
    public static final String ATTR_LEFT = "left";
    public static final String ATTR_RIGHT = "right";
    public static final String ATTR_CENTER = "center";
    public static final String ATTR_LINE_HEIGHT = "line-height";

    public static final String ATTR_SQ = "sq";
    public static final String ATTR_INDEX = "index";
    public static final String ATTR_NAME = "name";


    /**
     * 答案空是否精确答案，即是否支持机器阅卷
     */
    public static final String ATTR_EXACT = "exact";
    /**
     * 小问
     */
    public static final String ATTR_ID_CONTAINER = "id-container";
    public static final String ATTR_ID_CONTAINER_VAL = "question";
    /**
     * 是否是选择题答案
     */
    public static final String ATTR_IS_OP = "isop";
    /**
     * 图片的origin属性，比如竖式svg图片origin存的是竖式的svg图片url
     */
    public static final String ATTR_ORIGIN = "origin";
    /**
     * 类型属性，图片的竖式和拼音田字格有此属性
     */
    public static final String ATTR_TYPE = "type";
    /**
     * 判断题答案属性：judge
     * 示例： <an judge="0"> judge取值为0,1,2. 0表示错，1表示对，2表示未指定
     */
    public static final String ATTR_JUDGE = "judge";
    /**
     * type="pinyin"
     */
    public static final String ATTR_TYPE_VAL_PINYIN = "pinyin";
    /**
     * type="math-vertical"
     */
    public static final String ATTR_TYPE_VAL_MATH_VERTICAL = "math-vertical";
    /**
     * 单元格斜线 1 = 斜线下的斜线，2 = 斜向上的斜线 目前只支持1。
     */
    public static final String ATTR_SLASH = "slash";

    private String name;
    private String value;

    public Attr() {

    }

    public Attr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

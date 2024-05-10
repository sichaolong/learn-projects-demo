package scl.utils.qml;

/**
 * @author sichaolong
 * @createdate 2024/5/10 10:42
 */
public class BlankElement extends QmlElement {
    public static final String SIZE_ATTR = "size";
    public static final String TYPE_ATTR = "type";

    public BlankElement(QmlElement parent) {
        super("bk", parent);
    }

    public boolean isSubQuestion() {
        return this.hasAttr(Attr.ATTR_SQ);
    }

    public int getBlankSize() {
        return this.getAttrIntValue(SIZE_ATTR);
    }

    public BlankType getBlankType() {
        BlankType blankType = BlankType.UNDERLINE;
        if ("bracket".equalsIgnoreCase(this.getAttrValue(TYPE_ATTR))) {
            blankType = BlankType.BRACKET;
        } else if ("record".equalsIgnoreCase(this.getAttrValue(TYPE_ATTR))) {
            blankType = BlankType.RECORD;
        }
        return blankType;
    }
}


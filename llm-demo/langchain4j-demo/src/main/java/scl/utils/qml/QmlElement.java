package scl.utils.qml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author sichaolong
 * @createdate 2024/5/10 10:43
 */
public abstract class QmlElement {

    private String innerQml;
    private String tagName;
    private List<Attr> attrs;
    private List<QmlElement> childElements;
    /**
     * 元素的文本。如果一个元素value有值，那么childElement一定为空
     */
    private String value;
    private boolean selfClosed;
    private QmlElement parent;

    public QmlElement(String tagName, QmlElement parent) {
        this.childElements = new ArrayList<QmlElement>();
        this.attrs = new ArrayList<Attr>();
        this.tagName = tagName;
        this.parent = parent;
    }

    public boolean getSelfClosed() {
        return selfClosed;
    }

    public void setSelfClosed(boolean selfClosed) {
        this.selfClosed = selfClosed;
    }



    /// <summary>
    /// 获取元素的InnerText，滤掉了标签，只有纯文本
    /// </summary>
    public String getInnerText() {
        if (!this.hasElements()) {
            return QmlUtils.unescapeXml(this.value);
        }

        StringBuilder sb = new StringBuilder();
        for (QmlElement child : this.childElements) {
            String innerText = QmlUtils.unescapeXml(child.getInnerText());
            //如果innerText是null的话，sb.append就会拼接一个“null”，会造成innerText异常
            if (innerText != null) {
                sb.append(innerText);
            }

        }
        return sb.toString();
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
        if (this.value != null && this.value != "") {
            this.childElements.clear();
        }
    }

    public boolean isTextNode() {
        return !this.hasElements();
    }

    public boolean hasElements() {
        return this.childElements != null && this.childElements.size() > 0;
    }

    public QmlElement getParent() {
        return parent;
    }

    public void setParent(QmlElement parent) {
        this.parent = parent;
    }




    public String getTagName() {
        return tagName;
    }

    public Attr getAttr(String name) {
        if (this.attrs == null || this.attrs.size() == 0) {
            return null;
        }
        Optional<Attr> attrOptional =
            this.attrs.stream().filter(attr -> attr.getName().equalsIgnoreCase(name)).findFirst();
        if (attrOptional.isPresent()) {
            return attrOptional.get();
        }
        return null;
    }

    public boolean hasAttr(String name) {
        Optional<Attr> attrOptional =
            this.attrs.stream().filter(attr -> attr.getName().equalsIgnoreCase(name)).findFirst();
        return attrOptional.isPresent();
    }

    public boolean isXiaoWen() {
        return this.attrs.stream().anyMatch(attr -> attr.getName().equalsIgnoreCase(Attr.ATTR_ID_CONTAINER)
            && attr.getValue().equals(Attr.ATTR_ID_CONTAINER_VAL));
    }

    public String getAttrValue(String name) {
        Attr attr = getAttr(name);
        if (attr != null) {
            return attr.getValue();
        }
        return null;
    }

    public int getAttrIntValue(String name) {
        return (int)getAttrDoubleValue(name);
    }

    public double getAttrDoubleValue(String name) {
        String value = this.getAttrValue(name);
        if (QmlUtils.isEmpty(value)) {
            return 0;
        }

        if (value.endsWith("px")) {
            value = value.substring(0, value.length() - 2);
        }
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
        }
        return Double.parseDouble(value);
    }
    public List<Attr> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<Attr> attrs) {
        this.attrs = attrs;
    }

    /**
     * 有=update，无=add
     *
     * @param name
     * @param value
     */
    public void setAttr(String name, String value) {
        if (this.attrs == null) {
            this.attrs = new ArrayList<>();
        }
        Attr attr =
            this.attrs.stream().filter(a -> a.getName().equals(name)).findFirst().orElse(null);
        if (attr == null) {
            attr = new Attr(name, value);
            this.attrs.add(attr);
        } else {
            attr.setValue(value);
        }
    }

    public List<QmlElement> getChildElements() {
        return childElements;
    }





    public List<QmlElement> siblings() {
        if (this.getParent() == null) {
            return new ArrayList<>();
        }
        return this.getParent().getChildElements();
    }

    public QmlElement previous() {
        List<QmlElement> siblings = siblings();
        int index = siblings.indexOf(this);
        if (index > 0) {
            return siblings.get(index - 1);
        }
        return null;
    }

    public QmlElement next() {
        List<QmlElement> siblings = siblings();
        int index = siblings.indexOf(this);
        if (index < siblings.size() - 1) {
            return siblings.get(index + 1);
        }
        return null;
    }

    public QmlElement lastChild() {
        return QmlUtils.findLastOrDefault(this.getChildElements());
    }

    /**
     * 自己没有的属性，从父级继承
     *
     * @param attrName
     * @param from
     */
    public void extendAttr(String attrName, QmlElement from) {
        QmlElement my = this;
        QmlElement parent = from;
        if (parent == null) {
            return;
        }

        if (!my.hasAttr(attrName) && parent.hasAttr(attrName)) {
            my.setAttr(attrName, parent.getAttrValue(attrName));
        }
    }

    public void extendAttrFromParent(String attrName) {
        extendAttr(attrName, this.getParent());
    }
}
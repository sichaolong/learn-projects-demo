package scl.solr;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import org.apache.solr.client.solrj.beans.BindingException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.SuppressForbidden;


import org.apache.solr.client.solrj.beans.BindingException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.SuppressForbidden;
import scl.utils.FieldUtil;


import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author sichaolong
 * @createdate 2024/5/9 17:05
 */

public class DocumentObjectWithEnumBinder {
    private final Map<Class, List<DocField>> infocache = new ConcurrentHashMap<>();

    public DocumentObjectWithEnumBinder() {
    }

    public <T> List<T> getBeans(Class<T> clazz, SolrDocumentList solrDocList) {
        if (solrDocList == null) {
            return new ArrayList<>();
        }
        List<DocField> fields = getDocFields(clazz);
        List<T> result = new ArrayList<>(solrDocList.size());

        for (SolrDocument sdoc : solrDocList) {
            result.add(getBean(clazz, fields, sdoc));
        }
        return result;
    }

    public <T> T getBean(Class<T> clazz, SolrDocument solrDoc) {
        return getBean(clazz, null, solrDoc);
    }

    private <T> T getBean(Class<T> clazz, List<DocField> fields, SolrDocument solrDoc) {
        if (fields == null) {
            fields = getDocFields(clazz);
        }

        try {
            T obj = clazz.newInstance();
            for (DocField docField : fields) {
                docField.inject(obj, solrDoc);
            }
            return obj;
        } catch (Exception e) {
            throw new BindingException("Could not instantiate object of " + clazz, e);
        }
    }

    public SolrInputDocument toSolrInputDocument(Object obj) {
        List<DocField> fields = getDocFields(obj.getClass());
        if (fields.isEmpty()) {
            throw new BindingException("class: " + obj.getClass() + " does not define any fields.");
        }

        SolrInputDocument doc = new SolrInputDocument();
        for (DocField field : fields) {
            if (field.dynamicFieldNamePatternMatcher != null &&
                field.get(obj) != null &&
                field.isContainedInMap) {
                Map<String, Object> mapValue = (Map<String, Object>) field.get(obj);

                for (Map.Entry<String, Object> e : mapValue.entrySet()) {
                    doc.setField(e.getKey(), e.getValue());
                }
            } else {
                if (field.child != null) {
                    addChild(obj, field, doc);
                } else {
                    doc.setField(field.name, field.get(obj));
                }
            }
        }
        return doc;
    }

    private void addChild(Object obj, DocField field, SolrInputDocument doc) {
        Object val = field.get(obj);
        if (val == null) {
            return;
        }
        if (val instanceof Collection) {
            Collection collection = (Collection) val;
            for (Object o : collection) {
                SolrInputDocument child = toSolrInputDocument(o);
                doc.addChildDocument(child);
            }
        } else if (val.getClass().isArray()) {
            Object[] objs = (Object[]) val;
            for (Object o : objs) {
                doc.addChildDocument(toSolrInputDocument(o));
            }
        } else {
            doc.addChildDocument(toSolrInputDocument(val));
        }
    }

    private List<DocField> getDocFields(Class clazz) {
        List<DocField> fields = infocache.get(clazz);
        if (fields == null) {
            synchronized (infocache) {
                infocache.put(clazz, fields = collectInfo(clazz));
            }
        }
        return fields;
    }

    @SuppressForbidden(reason = "Needs access to possibly private @Field annotated fields/methods")
    private List<DocField> collectInfo(Class clazz) {
        List<DocField> fields = new ArrayList<>();
        Class superClazz = clazz;
        List<AccessibleObject> members = new ArrayList<>();

        while (superClazz != null && superClazz != Object.class) {
            members.addAll(FieldUtil.getDeclaredFields(superClazz));
            members.addAll(Arrays.asList(superClazz.getDeclaredMethods()));
            superClazz = superClazz.getSuperclass();
        }
        boolean childFieldFound = false;
        for (AccessibleObject member : members) {
            if (member.isAnnotationPresent(Field.class)) {
                AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                    member.setAccessible(true);
                    return null;
                });
                DocField df = new DocField(member);
                if (df.child != null) {
                    if (childFieldFound) {
                        throw new BindingException(
                            clazz.getName() + " cannot have more than one Field with child=true");
                    }
                    childFieldFound = true;
                }
                fields.add(df);
            }
        }
        return fields;
    }

    private class DocField {
        private Field annotation;
        private String name;
        private java.lang.reflect.Field field;
        private Method setter;
        private Method getter;
        private Class type;
        private boolean isArray;
        private boolean isList;
        private boolean isEnum;
        private List<DocField> child;

        /*
         * dynamic fields may use a Map based data structure to bind a given field.
         * if a mapping is done using, "Map<String, List<String>> foo", <code>isContainedInMap</code>
         * is set to <code>TRUE</code> as well as <code>isList</code> is set to <code>TRUE</code>
         */
        private boolean isContainedInMap;
        private Pattern dynamicFieldNamePatternMatcher;

        public DocField(AccessibleObject member) {
            if (member instanceof java.lang.reflect.Field) {
                field = (java.lang.reflect.Field) member;
            } else {
                setter = (Method) member;
            }
            annotation = member.getAnnotation(Field.class);
            storeName(annotation);
            storeType();

            // Look for a matching getter
            if (setter != null) {
                String gname = setter.getName();
                if (gname.startsWith("set")) {
                    gname = "get" + gname.substring(3);
                    try {
                        getter = setter.getDeclaringClass().getMethod(gname, (Class[]) null);
                    } catch (Exception ex) {
                        // no getter -- don't worry about it...
                        if (type == Boolean.class) {
                            gname = "is" + setter.getName().substring(3);
                            try {
                                getter =
                                    setter.getDeclaringClass().getMethod(gname, (Class[]) null);
                            } catch (Exception ex2) {
                                // no getter -- don't worry about it...
                            }
                        }
                    }
                }
            }
        }

        private void storeName(Field annotation) {
            if (annotation.value().equals(DEFAULT)) {
                if (field != null) {
                    name = field.getName();
                } else {
                    String setterName = setter.getName();
                    if (setterName.startsWith("set") && setterName.length() > 3) {
                        name = setterName.substring(3, 4).toLowerCase(Locale.ROOT)
                            + setterName.substring(4);
                    } else {
                        name = setter.getName();
                    }
                }
            } else if (annotation.value().indexOf('*')
                >= 0) { //dynamic fields are annotated as @Field("categories_*")
                //if the field was annotated as a dynamic field, convert the name into a pattern
                //the wildcard (*) is supposed to be either a prefix or a suffix, hence the use of replaceFirst
                name = annotation.value().replaceFirst("\\*", "\\.*");
                dynamicFieldNamePatternMatcher = Pattern.compile("^" + name + "$");
            } else {
                name = annotation.value();
            }
        }

        private void storeType() {
            if (field != null) {
                type = field.getType();
            } else {
                Class[] params = setter.getParameterTypes();
                if (params.length != 1) {
                    throw new BindingException("Invalid setter method. Must have one and only one parameter");
                }
                type = params[0];
            }

            if (type == Collection.class || type == List.class || type == ArrayList.class) {
                isList = true;
                if (annotation.child()) {
                    populateChild(field.getGenericType());
                } else {
                    type = Object.class;
                }
            } else if (type == byte[].class) {
                //no op
            } else if (type.isArray()) {
                isArray = true;
                if (annotation.child()) {
                    populateChild(type.getComponentType());
                } else {
                    type = type.getComponentType();
                }
            } else if (type == Map.class
                || type == HashMap.class) { //corresponding to the support for dynamicFields
                if (annotation.child()) {
                    throw new BindingException("Map should is not a valid type for a child document");
                }
                isContainedInMap = true;
                //assigned a default type
                type = Object.class;
                if (field != null) {
                    if (field.getGenericType() instanceof ParameterizedType) {
                        //check what are the generic values
                        ParameterizedType parameterizedType =
                            (ParameterizedType) field.getGenericType();
                        Type[] types = parameterizedType.getActualTypeArguments();
                        if (types != null && types.length == 2 && types[0] == String.class) {
                            //the key should always be String
                            //Raw and primitive types
                            if (types[1] instanceof Class) {
                                //the value could be multivalued then it is a List, Collection, ArrayList
                                if (types[1] == Collection.class || types[1] == List.class
                                    || types[1] == ArrayList.class) {
                                    type = Object.class;
                                    isList = true;
                                } else {
                                    //else assume it is a primitive and put in the source type itself
                                    type = (Class) types[1];
                                }
                            } else if (types[1] instanceof ParameterizedType) { //Of all the Parameterized types, only List is supported
                                Type rawType = ((ParameterizedType) types[1]).getRawType();
                                if (rawType == Collection.class || rawType == List.class
                                    || rawType == ArrayList.class) {
                                    type = Object.class;
                                    isList = true;
                                }
                            } else if (types[1] instanceof GenericArrayType) { //Array types
                                type =
                                    (Class) ((GenericArrayType) types[1]).getGenericComponentType();
                                isArray = true;
                            } else { //Throw an Exception if types are not known
                                throw new BindingException(
                                    "Allowed type for values of mapping a dynamicField are : "
                                        + "Object, Object[] and List");
                            }
                        }
                    }
                }
            } else if (type.isEnum()) {
                isEnum = true;
            } else {
                if (annotation.child()) {
                    populateChild(type);
                }
            }
        }

        private void populateChild(Type typ) {
            if (typ == null) {
                throw new RuntimeException(
                    "no type information available for" + (field == null ? setter : field));
            }
            if (typ.getClass() == Class.class) {//of type class
                type = (Class) typ;
            } else if (typ instanceof ParameterizedType) {
                try {
                    type =
                        Class.forName(((ParameterizedType) typ).getActualTypeArguments()[0].getTypeName());
                } catch (ClassNotFoundException e) {
                    throw new BindingException(
                        "Invalid type information available for" + (field == null ?
                            setter :
                            field));
                }
            } else {
                throw new BindingException(
                    "Invalid type information available for" + (field == null ? setter : field));

            }
            child = getDocFields(type);
        }

        /**
         * Called by the {@link #inject} method to read the value(s) for a field
         * This method supports reading of all "matching" fieldName's in the <code>SolrDocument</code>
         * <p>
         * Returns <code>SolrDocument.getFieldValue</code> for regular fields,
         * and <code>Map<String, List<Object>></code> for a dynamic field. The key is all matching fieldName's.
         */
        @SuppressWarnings("unchecked")
        private Object getFieldValue(SolrDocument solrDocument) {
            if (child != null) {
                List<SolrDocument> children = solrDocument.getChildDocuments();
                if (children == null || children.isEmpty()) {
                    return null;
                }
                if (isList) {
                    ArrayList list = new ArrayList(children.size());
                    for (SolrDocument c : children) {
                        list.add(getBean(type, child, c));
                    }
                    return list;
                } else if (isArray) {
                    Object[] arr = (Object[]) Array.newInstance(type, children.size());
                    for (int i = 0; i < children.size(); i++) {
                        arr[i] = getBean(type, child, children.get(i));
                    }
                    return arr;

                } else {
                    return getBean(type, child, children.get(0));
                }
            }
            if (isEnum) {
                String fieldValue = (String) solrDocument.getFieldValue(name);
                if (fieldValue == null) {
                    return null;
                }
                return Enum.valueOf((Class<Enum>) field.getType(), fieldValue);

            }
            Object fieldValue = solrDocument.getFieldValue(name);
            if (fieldValue != null) {
                //this is not a dynamic field. so return the value
                return fieldValue;
            }

            if (dynamicFieldNamePatternMatcher == null) {
                return null;
            }

            //reading dynamic field values
            Map<String, Object> allValuesMap = null;
            List allValuesList = null;
            if (isContainedInMap) {
                allValuesMap = new HashMap<>();
            } else {
                allValuesList = new ArrayList();
            }

            for (String field : solrDocument.getFieldNames()) {
                if (dynamicFieldNamePatternMatcher.matcher(field).find()) {
                    Object val = solrDocument.getFieldValue(field);
                    if (val == null) {
                        continue;
                    }

                    if (isContainedInMap) {
                        if (isList) {
                            if (!(val instanceof List)) {
                                List al = new ArrayList();
                                al.add(val);
                                val = al;
                            }
                        } else if (isArray) {
                            if (!(val instanceof List)) {
                                Object[] arr = (Object[]) Array.newInstance(type, 1);
                                arr[0] = val;
                                val = arr;
                            } else {
                                val = Array.newInstance(type, ((List) val).size());
                            }
                        }
                        allValuesMap.put(field, val);
                    } else {
                        if (val instanceof Collection) {
                            allValuesList.addAll((Collection) val);
                        } else {
                            allValuesList.add(val);
                        }
                    }
                }
            }
            if (isContainedInMap) {
                return allValuesMap.isEmpty() ? null : allValuesMap;
            } else {
                return allValuesList.isEmpty() ? null : allValuesList;
            }
        }

        <T> void inject(T obj, SolrDocument sdoc) {
            Object val = getFieldValue(sdoc);
            if (val == null) {
                return;
            }

            if (isArray && !isContainedInMap) {
                List list;
                if (val.getClass().isArray()) {
                    set(obj, val);
                    return;
                } else if (val instanceof List) {
                    list = (List) val;
                } else {
                    list = new ArrayList();
                    list.add(val);
                }
                set(obj, list.toArray((Object[]) Array.newInstance(type, list.size())));
            } else if (isList && !isContainedInMap) {
                if (!(val instanceof List)) {
                    List list = new ArrayList();
                    list.add(val);
                    val = list;
                }

                //added on 2018-06-15, by tecky lee
                //如果目标类型是泛型，而且参数类型是整数或者字符串时，需要将solr中查询出来的arraylist中的元素进行类型转换
                //比如solr中tagIds中存储的是多值int类型，而我们的实体类中tagIds要求是List<string>类型。
                // 如果不转换，序列化或者使用泛型遍历的时候就会出现无法将integer转换为String的异常。
                if (this.field.getGenericType() instanceof ParameterizedTypeImpl) {
                    Type argumentType =
                        ((ParameterizedTypeImpl) this.field.getGenericType()).getActualTypeArguments()[0];
                    //目前我们只支持String和Integer之间的互转
                    convertElementType((List) val, (Class) argumentType);
                }

                set(obj, val);
            } else if (isContainedInMap) {
                if (val instanceof Map) {
                    set(obj, val);
                }
            } else {
                set(obj, val);
            }

        }

        /**
         * 把list中的元素转换为目标类型
         * 目前只支持string和int的转换，其他类型不转换，保留原值。
         *
         * @param list
         * @param targetElementClass
         */
        private void convertElementType(List list, Class targetElementClass) {
            if (!targetElementClass.equals(String.class) &&
                targetElementClass.equals(Integer.class)) {
                return;
            }

            for (int i = 0; i < list.size(); i++) {
                Object ele = list.get(i);
                if (targetElementClass.isAssignableFrom(ele.getClass())) {
                    //如果第一个元素类型是匹配的，我们可以认为后面的元素类型也是匹配的，那就可以跳过了。
                    if (i == 0) {
                        return;
                    }
                    //如果类型是兼容的，就保留
                    continue;
                }

                try {
                    if (targetElementClass.equals(String.class)) {
                        list.set(i, String.valueOf(ele));
                    } else if (targetElementClass.equals(Integer.class)) {
                        list.set(i, Integer.valueOf(String.valueOf(ele)));
                    }
                } catch (Exception ex) {
                    //如果转换过程中发生任何异常，就保留solr中的原值
                }
            }

        }

        private void set(Object obj, Object v) {
            if (v != null && type == ByteBuffer.class && v.getClass() == byte[].class) {
                v = ByteBuffer.wrap((byte[]) v);
            }
            try {
                if (field != null) {
                    if (SolrObjectDeserializer.class.isAssignableFrom(field.getType())) {
                        // 如果当前field的类型是SolrObjectDeserializer的子类，则认为是复杂类型，会调用deserializer()实现反序列化
                        Object o = field.getType().newInstance();
                        field.set(obj, ((SolrObjectDeserializer) o).deserializer(v));
                    } else {
                        field.set(obj, v);
                    }
                } else if (setter != null) {
                    setter.invoke(obj, v);
                }
            } catch (Exception e) {
                throw new BindingException(
                    "Exception while setting value : " + v + " on " + (field != null ?
                        field :
                        setter), e);
            }
        }

        public Object get(final Object obj) {
            if (field != null) {
                try {
                    return field.get(obj);
                } catch (Exception e) {
                    throw new BindingException("Exception while getting value: " + field, e);
                }
            } else if (getter == null) {
                throw new BindingException("Missing getter for field: " + name
                    + " -- You can only call the 'get' for fields that have a field of 'get' method");
            }

            try {
                return getter.invoke(obj, (Object[]) null);
            } catch (Exception e) {
                throw new BindingException("Exception while getting value: " + getter, e);
            }
        }
    }


    public static final String DEFAULT = "#default";
}

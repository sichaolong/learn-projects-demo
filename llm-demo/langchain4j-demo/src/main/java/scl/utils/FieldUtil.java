package scl.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:56
 */
public class FieldUtil {

    private static final String AJC_MAGIC = "ajc$";

    /**
     * <p>每当类有一个方法被aspectj织入，就会产生一个ajc$xxx字段
     * <p>获取除ajc$xxx以外的所有字段
     *
     * @param clazz Class
     * @return List
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .peek(field -> field.setAccessible(true))
            .filter(field -> !field.getName().startsWith(AJC_MAGIC))
            .collect(Collectors.toList());
    }
}

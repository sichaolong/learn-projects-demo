package scl.utils;

import org.springframework.util.StringUtils;
import scl.utils.qml.Attr;
import scl.utils.qml.BlankElement;
import scl.utils.qml.BlankType;
import scl.utils.qml.QmlTextParser;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sichaolong
 * @createdate 2024/4/18 15:55
 */
public class Utilities {

    public static String convertLocalDateTimeToUTCStr(LocalDateTime localDateTime) {
        if(localDateTime == null){
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static String convertDateToUTCString(Date date) {
        if (date == null) {
            return null;
        }
        final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        return sdf.format(date);
    }

    /**
     * 全角字符转半角
     *
     * @param src
     * @return DBC case
     */
    public static String sbc2dbcCase(String src) {
        if (src == null) {
            return null;
        }
        char[] c = src.toCharArray();
        for (int i = 0; i < c.length; i++) {
            // WHITESPCE ASCII-32
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }

            // ASCII character 33-126 <-> unicode 65281-65374
            if (c[i] > 65280 && c[i] < 65375) {
                c[i] = (char) (c[i] - 65248);
            }
        }
        return new String(c);
    }

}

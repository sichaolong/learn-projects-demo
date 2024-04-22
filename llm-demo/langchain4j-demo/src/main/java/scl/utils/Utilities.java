package scl.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

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
}

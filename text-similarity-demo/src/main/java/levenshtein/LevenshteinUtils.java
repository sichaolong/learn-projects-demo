package levenshtein;

import org.apache.commons.lang3.StringUtils;

/**
 * @author sichaolong
 * @createdate 2024/11/11 14:07
 */
public class LevenshteinUtils {

    /**
     * 计算两个字符串的莱温斯坦距离，0-1之间的数字
     *
     * @param longer
     * @param shorter
     * @return
     */
    public static double similarityByLevenshtein(String longer, String shorter) {
        double maxLength = Math.max(longer.length(), shorter.length());

        //返回0-maxlength之间的数字
        int distance = StringUtils.getLevenshteinDistance(longer, shorter);

        //将距离转换为0-1之间的数字
        return (maxLength - distance) / maxLength;
    }

}

package jaccard;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2024/11/11 14:49
 */
public class JaccardUtils {


    /**
     * 计算相似度，使用Jaccard相似系数算法。从字符串中提取字符作为集合元素。
     *
     * @param s1
     * @param s2
     * @return
     */
    public static double similarityByJaccardByChars(String s1, String s2, JaccardDenominatorPolicy denominatorPolicy) {
        List<Integer> s1Chars = s1.chars().distinct().boxed().collect(Collectors.toList());
        List<Integer> s2Chars = s2.chars().distinct().boxed().collect(Collectors.toList());

        return getSimilarityByJaccard(s1Chars, s2Chars, denominatorPolicy);
    }


    private static double getSimilarityByJaccard(Collection left, Collection right, JaccardDenominatorPolicy policy) {
        long sameChars = CollectionUtils.intersection(left, right).size();

        double denominator = getDenominatorByPolicy(left, right, policy);
        return sameChars / denominator;
    }

    private static double getDenominatorByPolicy(Collection left, Collection right, JaccardDenominatorPolicy policy) {
        double denominator;
        switch (policy) {
            case MIN:
                //以两个字符串最小长度作为分母，能够放大相似度，忽略两个字符串长度差异
                denominator = Math.min(left.size(), right.size());
                break;
            case MAX:
                //以两个字符串的最大长度作为分母，能够使相似度缩小。强调字符串长度差异，差异越大，相似度越低
                denominator = Math.max(left.size(), right.size());
                break;
            case MEAN:
                //以两个字符串的平均长度作为分母，如果字符串长度差异大，相似度适中
                denominator = (left.size() + right.size()) / 2D;
                break;
            case LEFT:
                denominator = left.size();
                break;
            case UNION:
            default:
                //以两个字符串的并集作为分母，如果字符集合差异大，相似度会较小。
                denominator = CollectionUtils.union(left, right).stream().distinct().count();
                break;
        }
        return denominator;
    }
}

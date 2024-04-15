package jaccard;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2023/12/22 13:44
 */
public class JaccardDemo {

    public static void main(String[] args) {
        String s1 = "你好，你不是我的朋友";
        String s2 = "你不好，我的朋友是你";
        List<Integer> s1Chars = s1.chars().distinct().boxed().collect(Collectors.toList());
        System.out.println("s1 元素ASCII集合：" + s1Chars);

        List<Integer> s2Chars = s2.chars().distinct().boxed().collect(Collectors.toList());
        System.out.println("s2 元素ASCII集合：" + s2Chars);

        long sameChars = CollectionUtils.intersection(s1Chars, s2Chars).size();
        double denominator = CollectionUtils.union(s1Chars, s2Chars).stream().distinct().count();
        System.out.println("Jaccard 相似性系数为: " + sameChars / denominator); // 1.0
    }
}

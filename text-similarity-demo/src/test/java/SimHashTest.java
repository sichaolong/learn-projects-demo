import jaccard.JaccardDenominatorPolicy;
import jaccard.JaccardUtils;
import levenshtein.LevenshteinUtils;
import simhash.SimHash;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author sichaolong
 * @createdate 2024/11/11 13:45
 */
public class SimHashTest {

    public static void main(String[] args) throws Exception {

        // 原始试卷内容
        String origin_paper_text = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/origin_paper_text.txt"));
        // 试卷内容1，将21题和22题互相换位置
        System.out.println("==================================");
        System.out.println("CASE1:将21题和22题互相换位置");
        String paper_text1 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text1.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容1长度：" + paper_text1.length());

        long start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text1);
        long end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text1);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text1);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");

        // 试卷内容2，删除试卷中间的11题
        System.out.println("==================================");
        System.out.println("CASE2:删除试卷中间的11题");
        String paper_text2 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text2.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容2长度：" + paper_text2.length());

        start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text2);
        end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text2);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text2);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");



        // 试卷内容3，删除10题之后的所有内容
        System.out.println("==================================");
        System.out.println("CASE3:删除10题之后的所有内容");
        String paper_text3 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text3.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容3长度：" + paper_text3.length());

        start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text3);
        end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text3);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text3);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");

        // 试卷内容4，完全打乱试题顺序
        System.out.println("==================================");
        System.out.println("CASE4:完全打乱试题顺序");
        String paper_text4 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text4.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容4长度：" + paper_text4.length());
        start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text4);
        end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text4);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text4);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");


        // 试卷内容5，删除11题之前的所有内容
        System.out.println("==================================");
        System.out.println("CASE5:删除11题之前的所有内容");
        String paper_text5 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text5.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容5长度：" + paper_text5.length());

        start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text5);
        end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text5);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text5);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");

        // 试卷内容6，前11道题内容不相同，后面内容相同
        System.out.println("==================================");
        System.out.println("CASE6:前11道题内容不相同，后面内容相同");
        String paper_text6 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text6.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容6长度：" + paper_text6.length());

        start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text6);
        end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text6);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text6);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");

        // 试卷内容7，在11题后面中间加入一道试题
        System.out.println("==================================");
        System.out.println("CASE7:在11题后面中间加入一道试题");
        String paper_text7 = FileUtils.readFileToString(new File("/Users/sichaolong/Documents/my_projects/my_github_projects/learn-projects-demo/text-similarity-demo/src/main/resources/paper_text7.txt"));
        System.out.println("试卷原文内容长度：" + origin_paper_text.length());
        System.out.println("内容7长度：" + paper_text7.length());

        start = System.currentTimeMillis();
        testSimHash(origin_paper_text, paper_text7);
        end = System.currentTimeMillis();
        System.out.println("simHash耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testLevenshteinDistance(origin_paper_text, paper_text7);
        end = System.currentTimeMillis();
        System.out.println("levenshtein耗时：" + (end - start) + "ms");

        start = System.currentTimeMillis();
        testJaccard(origin_paper_text, paper_text7);
        end = System.currentTimeMillis();
        System.out.println("jaccard耗时：" + (end - start) + "ms");

    }


    /**
     * 测试simHash算法
     * @throws Exception
     */
    public static void testSimHash(String one, String two) {
        // 计算simHash相似度
        SimHash simHash = new SimHash();
        double similar = simHash.getSimilar(one, two);
        System.out.println("simHash相似度：" + similar);
    }

    /**
     * 测试LevenshteinDistance算法
     * @throws Exception
     */
    public static void testLevenshteinDistance(String one, String two) {
        double similarity = LevenshteinUtils.similarityByLevenshtein(one, two);
        System.out.println("levenshtein相似度：" + similarity);
    }


    /**
     * 测试Jaccard算法
     * @param one
     * @param two
     */
    public static void testJaccard(String one, String two){
        double similarity = JaccardUtils.similarityByJaccardByChars(one, two, JaccardDenominatorPolicy.MIN);
        System.out.println("jaccard相似度：" + similarity);
    }

}

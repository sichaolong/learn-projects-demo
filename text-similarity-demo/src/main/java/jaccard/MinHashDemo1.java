package jaccard;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sichaolong
 * @createdate 2023/12/22 13:37
 */
public class MinHashDemo1 {
    private int numHashFunctions; // minhash函数数量
    private int[] hashCoefficients; // 哈希系数数组

    public MinHashDemo1(int numHashFunctions) {
        this.numHashFunctions = numHashFunctions;
        this.hashCoefficients = generateHashCoefficients(numHashFunctions);
    }

    // 生成哈希系数数组
    private int[] generateHashCoefficients(int numHashFunctions) {
        Random random = new Random();
        int[] coefficients = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            coefficients[i] = random.nextInt(Integer.MAX_VALUE);
        }
        return coefficients;
    }

    // 计算集合A和B的MinHash值
    private int[] computeMinHash(Set<String> set) {
        int[] minHashValues = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            int minHashValue = Integer.MAX_VALUE;
            for (String element : set) {
                int hashValue = (hashCoefficients[i] ^ element.hashCode()) % Integer.MAX_VALUE;
                if (hashValue < minHashValue) {
                    minHashValue = hashValue;
                }
            }
            minHashValues[i] = minHashValue;
        }
        return minHashValues;
    }

    // 计算Jaccard 相似度
    public double computeJaccardSimilarity(Set<String> setA, Set<String> setB) {
        int countUnion = setA.size() + setB.size(); // 全集元素数
        int countIntersection = 0; // 交集元素数

        int[] minHashValuesA = computeMinHash(setA);
        int[] minHashValuesB = computeMinHash(setB);

        for (int i = 0; i < numHashFunctions; i++) {
            if (minHashValuesA[i] == minHashValuesB[i]) {
                countIntersection++;
            }
        }

        double jaccardSimilarity = (double) countIntersection / (double) (countUnion - countIntersection);
        return jaccardSimilarity;
    }

    public static void main(String[] args) {
        // 测试case1
        Set<String> setA = Set.of("1","2","3");
        Set<String> setB = Set.of("2","3","4");
        MinHashDemo1 minHash = new MinHashDemo1(128); // 设置要使用的minhash函数数量
        double jaccardSimilarity = minHash.computeJaccardSimilarity(setA, setB);
        System.out.println("Jaccard相似度: " + jaccardSimilarity);



        // 测试case2
        String a = "阅读下面的文字,完成下面小题。 材料一: 刘姥姥吃毕了饭,拉了板儿过来,舔舌咂嘴的道谢。凤姐笑道:“且请坐下,听我告诉你老人家。方才的意思,我已知道了。①若论亲戚之间,原该不等上门来就该有照应才是。但如今家内杂事太烦,太太渐上了年纪,一时想不到也是有的。况是我近来接着管些事,都不知道这些亲戚们。二则外头看着虽是烈烈轰轰的,殊不知大有大的艰难去处,说与人也未必信罢。今儿你既老远的来了,又是头一次见我张口,怎好叫你空回去呢。可巧昨儿太太给我的丫头们做衣裳的二十两银子,我还没动呢,你若不嫌少,就暂且先拿了去罢。” 那刘姥姥先听见告艰难,只当是没有,心里便突突的,后来听见给他二十两,喜的又浑身发痒起来,说道:“嗳,我也是知道艰难的。但俗语说的‘瘦死的骆驼比马大’,凭他怎样,你老拔根寒毛比我们的腰还粗呢!” 周瑞家的见他说的粗鄙,只管使眼色止他。凤姐看见,笑而不睬,只命平儿把昨儿那包银子拿来,再拿一吊钱来,都送到刘姥姥的跟前。凤姐乃道:“这是二十两银子,暂且给这孩子做件冬衣罢。若不拿着,就真是怪我了。这钱雇车坐罢。改日无事,只管来逛逛,方是亲戚们的意思。天也晚了,也不虚留你们了,到家里该问好的问个好儿罢。”一面说,一面就站了起来。 刘姥姥只管千恩万谢的,拿了银子钱,随了周瑞家的来至外面,仍从后门去了。 (选自《红楼梦》第六回,有删改) 材料二: ②那刘姥姥入了坐,拿起箸来,沉甸甸的不伏手。原是凤姐和鸳鸯商议定了,单拿一双老年四楞象牙镶金的筷子与刘姥姥。刘姥姥见了,说道:“这叉爬子比俺那里铁锹还沉,那里拿的动?”说的众人都笑起来。 只见一个媳妇端了一个盒子站在当地,一个丫鬟上来揭去盒盖,里面盛着两碗菜。李纨端了一碗放在贾母桌上。凤姐儿偏拣了一碗鸽子蛋,放在刘姥姥桌上。贾母这边说声“请”,刘姥姥便站起身来,高声说道:“老刘,老刘,食量大似牛,吃一个老母猪不抬头。”说着,却鼓着腮帮子,两眼直视,一声不语。众人先是发怔。后来一听,上上下下都哈哈大笑起来。湘云掌不住,一口饭都喷了出来;黛玉笑岔了气,伏着桌子只叫“嗳哟”;宝玉早滚到贾母怀里,贾母笑的搂着宝玉叫“心肝”;王夫人笑的用手指着凤姐儿,只说不出话来;薛姨妈也掌不住,口里的茶喷了探春一裙子;探春手里的饭碗都合在迎春身上;惜春离了坐位,拉着他奶母叫揉一揉肠子。地下的无一个不弯腰屈背。也有躲出去蹲着笑去的,也有忍着笑上来替他姊妹换衣裳的。独有凤姐、鸳鸯二人掌着,还只管让刘姥姥。刘姥姥拿起箸来,只觉不听使,又说道:“这里的鸡儿也俊,下的这蛋也小巧。怪俊的,我且抓得一个儿。” 众人方住了笑,听见这话,又笑起来。贾母笑的眼泪出来,琥珀在后捶着。贾母笑道:“这定是凤丫头促狭鬼儿闹的,快别信他的话了。”那刘姥姥正夸鸡蛋小巧,凤姐儿笑道:“一两银子一个呢,你快尝尝罢,冷了就不好吃了。”刘姥姥便伸箸子要夹,那里夹的起来,③满碗里闹了一阵,好容易撮起一个来,才伸着脖子要吃,偏又滑下来,滚在地下,忙放下箸子要亲自去捡,早有地下的人捡了出去了。刘姥姥叹道:“一两银子,也没听见响声儿就没了。”众人已没心吃饭,都看着他笑。 (选自《红楼梦》第四十回,有删改) 材料三: 只见平儿同刘姥姥带了一个小女孩儿进来,说:“我们姑奶奶在那里?”平儿引到炕边,刘姥姥便说:“请姑奶奶安。”凤姐睁眼一看,不觉一阵伤心,说:“姥姥你好?怎么这时候才来?你瞧你外孙女儿也长的这么大了。”刘姥姥看着凤姐骨瘦如柴,神情恍惚,心里也就悲惨起来,说:“我的奶奶,怎么这几个月不见,就病到这个分儿。我糊涂的要死,怎么不早来请姑奶奶的安!”便叫青儿给姑奶奶请安。青儿只是笑,凤姐看了倒也十分喜欢,便叫小红招呼着。 这里平儿恐刘姥姥话多,搅烦了凤姐,便拉了刘姥姥说:“你提起太太来,你还没有过去呢。我出去叫人带了你去见见,也不枉来这一趟。”刘姥姥便要走。凤姐道:“忙什么,你坐下,我问你近来的日子还过的么?”刘姥姥千恩万谢的说道:“我们若不仗着姑奶奶”,说着,指着青儿说:“他的老子娘都要饿死了。如今虽说是庄家人苦,家里也挣了好几亩地,又打了一眼井,种些菜蔬瓜果,一年卖的钱也不少,尽够他们嚼吃的了。这两年姑奶奶还时常给些衣服布匹,在我们村里算过得的了。阿弥陀佛,前日他老子进城,听见姑奶奶这里动了家,我就几乎唬杀了。亏得又有人说不是这里,我才放心。后来又听见说这里老爷升了,我又喜欢,就要来道喜,为的是满地的庄家来不得。昨日又听说老太太没有了,我在地里打豆子,听见了这话,唬得连豆子都拿不起来了,就在地里狠狠的哭了一大场。我和女婿说,我也顾不得你们了,不管真话谎话,我是要进城瞧瞧去的。④我女儿女婿也不是没良心的,听见了也哭了一回子,今儿天没亮就赶着我进城来了。我也不认得一个人,没有地方打听,一径来到后门,进了门找周嫂子,再找不着,撞见一个小姑娘,说周嫂子他得了不是了,撵了";
        String b = "三十一、阅读下文,回答问题。原是凤姐和鸳鸯商议定了,单拿一双老年四楞象牙镶金的筷子与刘姥姥。刘姥姥见了,说道:”这叉爬子比俺那里铁锨还沉,那里犟的过他。”说的众人都笑起来。只见一个媳妇端了一个盒子站在当地,一个丫鬟上来揭去盒盖,里面盛着两碗菜。李纨端了一碗放在贾母桌上。凤姐儿偏拣了一碗鸽子蛋放在刘姥姥桌上。贾母这边说声”请”,刘姥姥便站起身来,高声说道:”老刘,老刘,食量大似牛,吃一个老母猪不抬头。”自己却鼓着腮不语。众人先是发怔,后来一听,上上下下都哈哈的大笑起来。史湘云撑不住,一口饭都喷了出来;林黛玉笑岔了气,伏着桌子叫”嗳哟”;宝玉早滚到贾母怀里,贾母笑的搂着宝玉叫”心肝”;王夫人笑的用手指着凤姐儿,只说不出话来;薛姨妈也撑不住,口里茶喷了探春一裙子;探春手里的饭碗都合在迎春身上;惜春离了坐位,拉着他奶母叫揉一揉肠子。地下的无一个不弯腰屈背,也有躲出去蹲着笑去的,也有忍着笑上来替他姊妹换衣裳的,独有凤姐鸳鸯二人撑着,还只管让刘姥姥。刘姥姥拿起箸来,只觉不听使,又说道:”这里的鸡儿也俊,下的这蛋也小巧,怪俊的。我且攮一个。”众人方住了笑,听见这话又笑起来。贾母笑的眼泪出来,琥珀在后捶着。贾母笑道:”这定是凤丫头促狭鬼儿闹的,快别信他的话了。”那刘姥姥正夸鸡蛋小巧,要禽攮一个,凤姐儿笑道:”一两银子一个呢,你快尝尝罢,那冷了就不好吃了。”刘姥姥便伸箸子要夹,那里夹的起来,满碗里闹了一阵好的,好容易撮起一个来,才伸着脖子要吃,偏又滑下来滚在地下,忙放下箸子要亲自去捡,早有地下的人捡了出去了。刘姥姥叹道:”一两银子,也没听见响声儿就没了。'1.本语段节选自名著《红楼梦》,作者是_(朝代)的_(人名)。2.请用简洁的语言概括选文内容。3.结合选文内容分析刘姥姥形象。";
        Set<String> aSet = a.chars().distinct().boxed().map(String::valueOf).collect(Collectors.toSet());
        Set<String> bSet = b.chars().distinct().boxed().map(String::valueOf).collect(Collectors.toSet());
        MinHashDemo1 minHash2 = new MinHashDemo1(128);
        double jaccardSimilarity2 = minHash2.computeJaccardSimilarity(aSet, bSet);
        System.out.println("Jaccard相似度: " + jaccardSimilarity2);
    }
}

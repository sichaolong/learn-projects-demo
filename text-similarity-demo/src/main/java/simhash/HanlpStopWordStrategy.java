package simhash;

import com.hankcs.hanlp.corpus.tag.Nature;

import java.util.HashSet;
import java.util.Set;


/**
 * @author sichaolong
 * @createdate 2024/11/11 13:39
 */

/**
 * Hanlp停用词策略
 */
public class HanlpStopWordStrategy implements StopWordStrategy {

    private final Set<String> stopWordSet;

    public HanlpStopWordStrategy() {
        Nature[] stopNatures = {
                // 去除标点符号
                Nature.w, Nature.wd, Nature.wf, Nature.wj, Nature.wky, Nature.wkz,
                Nature.wm, Nature.wn, Nature.wp, Nature.ws, Nature.wt, Nature.ww,
                Nature.wyy, Nature.wyz,
                // 去除助词
                Nature.u, Nature.uzhe, Nature.ule, Nature.uguo, Nature.ude1,
                Nature.ude2, Nature.ude3, Nature.usuo, Nature.udeng, Nature.uyy,
                Nature.udh, Nature.uls, Nature.uzhi, Nature.ulian };
        this.stopWordSet = new HashSet<>((int) (stopNatures.length / 0.75 + 1));
        for (Nature nature : stopNatures) {
            this.stopWordSet.add(nature.toString());
        }
    }

    @Override
    public boolean isStopWord(String word, String nature) {
        // 过滤停用词性
        return this.stopWordSet.contains(nature);
    }
}
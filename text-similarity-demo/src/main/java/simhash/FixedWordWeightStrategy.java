package simhash;

/**
 * @author sichaolong
 * @createdate 2024/11/11 13:40
 */
public class FixedWordWeightStrategy implements WordWeightStrategy{
    private final int wordWeight;

    /**
     * 默认固定分词权重1
     */
    public FixedWordWeightStrategy() {
        this(1);
    }

    public FixedWordWeightStrategy(int wordWeight) {
        this.wordWeight = wordWeight;
    }

    @Override
    public int getWordWeight(String word, String nature) {
        return this.wordWeight;
    }
}

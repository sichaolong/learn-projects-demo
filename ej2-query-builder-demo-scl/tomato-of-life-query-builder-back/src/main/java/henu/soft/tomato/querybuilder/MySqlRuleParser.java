package henu.soft.tomato.querybuilder;

import com.itfsw.query.builder.support.model.IRule;
import com.itfsw.query.builder.support.model.sql.Operation;
import com.itfsw.query.builder.support.parser.AbstractSqlRuleParser;
import com.itfsw.query.builder.support.parser.JsonRuleParser;

/**
 * @author sichaolong
 * @date 2022/11/14 20:24
 */
public class MySqlRuleParser extends AbstractSqlRuleParser {
    @Override
    public boolean canParse(IRule iRule) {
        return false;
    }

    @Override
    public Operation parse(IRule iRule, JsonRuleParser jsonRuleParser) {
        return null;
    }
}

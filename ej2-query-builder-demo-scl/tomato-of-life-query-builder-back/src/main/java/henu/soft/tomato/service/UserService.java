package henu.soft.tomato.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.itfsw.query.builder.SqlQueryBuilderFactory;
import com.itfsw.query.builder.support.builder.SqlBuilder;
import com.itfsw.query.builder.support.model.result.SqlQueryResult;
import henu.soft.tomato.mapper.UserMapper;
import henu.soft.tomato.model.User;
import henu.soft.tomato.vo.TUserSQLRuleReqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sichaolong
 * @date 2022/11/14 15:16
 */

@Service
@Slf4j
public class UserService {


    @Resource
    UserMapper userMapper;


    @Resource
    JdbcTemplate jdbcTemplate;






    public List<User> getUserList() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>());
        return users;
    }


    public List<User> getListByQueryBuilderRule(TUserSQLRuleReqVo TUserSqlRuleReqVo) {
        String ruleSQL = TUserSqlRuleReqVo.getRuleSQL();
        log.info("ruleSQL===>{}",ruleSQL);
        List<User> listBySQLRule = userMapper.getListByQueryBuilderRule(ruleSQL);
        return listBySQLRule;
    }

    public List<User> getListByJSONRule(String ruleJson) {
        log.info("ruleJson===>{}",ruleJson);



        /**
         * String json = "{
         *  "condition":"OR",
         *  "rules":[
         *    {
         *      "id":"name",
         *      "field":"username",
         *      "type":"string",
         *      "input":"text",
         *      "operator":"equal",
         *      "value":"Mistic"
         *     }
         *   ],
         *  "not":false,
         *  "valid":true
         * }";
         *
         *
         * {
         *     "condition":"and",
         *     "rules":[
         *         {
         *             "label":"username",
         *             "field":"username",
         *             "operator":"equal",
         *             "type":"string",
         *             "value":"sichaolong"
         *         },
         *         {
         *             "condition":"and",
         *             "rules":[
         *                 {
         *                     "label":"head_url",
         *                     "field":"head_url",
         *                     "operator":"startswith",
         *                     "type":"string",
         *                     "value":"888"
         *                 }
         *             ]
         *         }
         *     ]
         * }
         */
        // get SqlBuilder
        SqlQueryBuilderFactory sqlQueryBuilderFactory = new SqlQueryBuilderFactory();
        SqlBuilder sqlBuilder = sqlQueryBuilderFactory.builder();

        // build query
        try {

            SqlQueryResult sqlQueryResult = sqlBuilder.build(ruleJson);

            // TODO JSON格式转换异常
            List<User> users = jdbcTemplate.queryForList(new StringBuffer("SELECT * FROM `user` WHERE ").append(sqlQueryResult.getQuery()).toString(), sqlQueryResult.getParams().toArray(), User.class);
            return users;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}

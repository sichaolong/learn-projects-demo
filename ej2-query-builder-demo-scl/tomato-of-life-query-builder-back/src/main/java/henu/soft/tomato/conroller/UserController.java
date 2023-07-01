package henu.soft.tomato.conroller;

import henu.soft.tomato.model.User;
import henu.soft.tomato.service.UserService;
import henu.soft.tomato.utils.ResponseResult;
import henu.soft.tomato.vo.TUserSQLRuleReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sichaolong
 * @date 2022/11/14 15:09
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/user/list")
    public ResponseResult getUserList() {
        List<User> list  = userService.getUserList();
        return ResponseResult.success(list);
    }


    /**
     * 配合vue-query-builder实现指定规则查询数据库
     * @param TUserSqlRuleReqVo ，规则sql
     * @return
     */
    @PostMapping("/user/list-by-sql-rule")
    public ResponseResult getListBySQLRule(@RequestBody TUserSQLRuleReqVo TUserSqlRuleReqVo) {
        List<User> list  = userService.getListByQueryBuilderRule(TUserSqlRuleReqVo);
        return ResponseResult.success(list);
    }

    @PostMapping("/user/list-by-json-rule")
    public ResponseResult getListByJSONRule(@RequestBody String ruleJson) {
        List<User> list  = userService.getListByJSONRule(ruleJson);
        return ResponseResult.success(list);
    }


}

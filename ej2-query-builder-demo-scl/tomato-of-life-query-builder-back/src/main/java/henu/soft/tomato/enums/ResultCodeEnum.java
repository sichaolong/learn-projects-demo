package henu.soft.tomato.enums;

/**
 * @author sichaolong
 * @date 2022/11/14 15:12
 */
public enum ResultCodeEnum {
    /**
     * 成功:200
     */
    SUCCESS(200, "成功"),
    /**
     * 参数错误：1001-1999
     */
    PARAM_IS_INVALID(1001, "参数无效"),
    PARAM_IS_NULL(1002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    /**
     * 用户错误：2001-2999
     */
    USER_NOT_LOGIN_IN(2001, "用户未登录，访问的路径需要验证，请登录"),
    USER_LOGIN_ERROR(2002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(2003, "账号被禁用"),
    USER_NOT_EXIST(2004, "用户不存在"),
    USER_HAS_EXISTED(2005, "用户已存在");


    private final Integer code;
    private final String message;


    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

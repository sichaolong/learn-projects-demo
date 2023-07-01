package henu.soft.tomato.utils;

/**
 * @author sichaolong
 * @date 2022/11/14 15:12
 */


import henu.soft.tomato.enums.ResultCodeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装请求的响应结果的类
 * <p>
 * 注解版的进阶做法见下文
 * https://mp.weixin.qq.com/s/EW-vo8ERQLAVc3D8YwTr6w
 */
@Data
public class ResponseResult implements Serializable {

    private Integer code;
    private String message;
    private Object data;

    public void setResultCodeAndMessage(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    // =============================================== 构造的方式 ===============================================

    /**
     * 返回不带数据的成功
     *
     * @return
     */
    public static ResponseResult success() {
        ResponseResult result = new ResponseResult();
        result.setResultCodeAndMessage(ResultCodeEnum.SUCCESS);
        return result;
    }

    /**
     * 返回带数据的成功
     *
     * @return
     */
    public static ResponseResult success(Object data) {
        ResponseResult result = new ResponseResult();
        result.setResultCodeAndMessage(ResultCodeEnum.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 返回不带数据的失败
     *
     * @return
     */
    public static ResponseResult failure(ResultCodeEnum resultCodeEnum) {
        ResponseResult result = new ResponseResult();
        result.setResultCodeAndMessage(resultCodeEnum);
        return result;
    }

    /**
     * 返回带数据的失败
     *
     * @return
     */
    public static ResponseResult failure(ResultCodeEnum resultCodeEnum, Object data) {
        ResponseResult result = new ResponseResult();
        result.setResultCodeAndMessage(resultCodeEnum);
        result.setData(data);
        return result;
    }

    // =============================================== 追加的方式 ===============================================

    /**
     * 添加响应编号
     *
     * @return
     */
    public static ResponseResult code(Integer code) {
        ResponseResult result = new ResponseResult();
        result.setCode(code);
        return result;
    }

    /**
     * 追加响应消息
     *
     * @return
     */
    public ResponseResult message(String message) {
        this.message = message;
        return this;
    }

    /**
     * 追加响应数据
     *
     * @return
     */
    public ResponseResult data(Object data) {
        this.data = data;
        return this;
    }

}


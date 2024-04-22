package com.moyz.adi.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 会话表
 * </p>
 *
 * @author moyz
 * @since 2023-04-11
 */
@Data
@TableName("adi_conversation")
@Schema(title = "对话实体", description = "对话表")
public class Conversation extends BaseEntity {

    @Schema(title = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(title = "对话uuid")
    @TableField("uuid")
    private String uuid;

    @Schema(title = "会话标题")
    @TableField("title")
    private String title;

    @Schema(title = "消耗的token数量")
    @TableField("tokens")
    private Integer tokens;

    @Schema(title = "ai model name")
    @TableField("ai_model")
    private String aiModel;

    @Schema(name = "是否开启理解上下文的功能")
    @TableField("understand_context_enable")
    private Boolean understandContextEnable;

    @Schema(title = "set the system message to ai, ig: you are a lawyer")
    @TableField("ai_system_message")
    private String aiSystemMessage;
}

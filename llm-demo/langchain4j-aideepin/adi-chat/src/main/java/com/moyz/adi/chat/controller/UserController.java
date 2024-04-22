package com.moyz.adi.chat.controller;

import com.moyz.adi.common.service.UserService;
import com.moyz.adi.common.base.ThreadContext;
import com.moyz.adi.common.dto.ConfigResp;
import com.moyz.adi.common.dto.ModifyPasswordReq;
import com.moyz.adi.common.dto.UserUpdateReq;
import com.moyz.adi.common.entity.User;
import com.talanlabs.avatargenerator.Avatar;
import com.talanlabs.avatargenerator.cat.CatAvatar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Slf4j
@Tag(name = "用户controller")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户信息")
    @GetMapping("/{uuid}")
    public void info(@Validated @PathVariable String uuid) {
        log.info(uuid);
    }

    @Operation(summary = "配置信息")
    @GetMapping("/config")
    public ConfigResp configInfo() {
        return userService.getConfig();
    }

    @Operation(summary = "更新信息")
    @PostMapping("/edit")
    public void update(@Validated UserUpdateReq userUpdateReq) {
        userService.updateConfig(userUpdateReq);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password/modify")
    public String modifyPassword(@RequestBody ModifyPasswordReq modifyPasswordReq) {
        userService.modifyPassword(modifyPasswordReq.getOldPassword(), modifyPasswordReq.getNewPassword());
        return "修改成功";
    }

    @Operation(summary = "退出")
    @PostMapping("/logout")
    public void logout() {
        userService.logout();
    }

    @Operation(summary = "当前用户头像")
    @GetMapping(value = "/myAvatar", produces = MediaType.IMAGE_JPEG_VALUE)
    public void myAvatar(HttpServletResponse response) {
        User user = ThreadContext.getCurrentUser();
        Avatar avatar = CatAvatar.newAvatarBuilder().build();
        BufferedImage bufferedImage = avatar.create(user.getId());
        //把图片写给浏览器
        try {
            ImageIO.write(bufferedImage, "png", response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "用户头像")
    @GetMapping(value = "/avatar/{uuid}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void avatar(@Validated @PathVariable String uuid, HttpServletResponse response){
        User user = userService.getByUuid(uuid);
        long userId = 0;
        if(null != user){
            userId = user.getId();
        }
        Avatar avatar = CatAvatar.newAvatarBuilder().build();
        BufferedImage bufferedImage = avatar.create(userId);
        //把图片写给浏览器
        try {
            ImageIO.write(bufferedImage, "png", response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

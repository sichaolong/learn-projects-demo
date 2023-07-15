package scl.demo.qrcodelogin.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import scl.demo.qrcodelogin.entity.Response;
import scl.demo.qrcodelogin.entity.User;
import scl.demo.qrcodelogin.service.UserService;
import scl.demo.qrcodelogin.utils.HostHolderUtil;

@Controller

public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private HostHolderUtil hostHolder;

    @RequestMapping(path = "/getUser", method = RequestMethod.GET)
    @ResponseBody
    public Response getUser() {
        User user = hostHolder.getUser();
        if (user == null) {
            return Response.createErrorResponse("用户未登录");
        }
        return Response.createResponse(null, user);
    }
}

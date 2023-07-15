package scl.demo.qrcodelogin.controller.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import scl.demo.qrcodelogin.entity.User;
import scl.demo.qrcodelogin.service.CacheStoreService;
import scl.demo.qrcodelogin.service.UserService;
import scl.demo.qrcodelogin.utils.CommonUtil;
import scl.demo.qrcodelogin.utils.HostHolderUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolderUtil hostHolder;

    @Autowired
    private CacheStoreService cacheStore;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String accessToken = request.getHeader("access_token");
        // access_token 存在
        if (StringUtils.isNotEmpty(accessToken)) {
            String userId = (String) cacheStore.get(CommonUtil.buildAccessTokenKey(accessToken));
            User user = userService.getCurrentUser(userId);
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}

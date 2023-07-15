package scl.demo.qrcodelogin.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scl.demo.qrcodelogin.entity.User;
import scl.demo.qrcodelogin.utils.CommonUtil;

@Service
public class UserService {

    @Autowired
    private CacheStoreService cacheStoreService;

    public User getCurrentUser(String userId) {
        String userKey = CommonUtil.buildUserKey(userId);
        return (User) cacheStoreService.get(userKey);
    }
}

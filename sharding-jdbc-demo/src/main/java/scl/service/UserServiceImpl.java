package scl.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import scl.entity.User;
import scl.mapper.UserMapper;

/**
 * @projectName:    learn-projects-demo
 * @package:        scl.service
 * @className:      UserServiceImpl
 * @author:     sichaolong
 * @description:  TODO
 * @date:    2023/6/29 19:33
 * @version:    1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}

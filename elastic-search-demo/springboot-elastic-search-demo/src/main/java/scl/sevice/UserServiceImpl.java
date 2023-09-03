package scl.sevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scl.dao.UserRepository;
import scl.pojo.User;

/**
 * @projectName: learn-projects-demo
 * @package: scl.sevice
 * @className: UserServiceImpl
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/3 23:36
 * @version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

}

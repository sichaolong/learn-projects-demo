package scl.sevice;

import scl.pojo.User;

/**
 * @projectName: learn-projects-demo
 * @package: scl.sevice
 * @className: UserService
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/3 23:35
 * @version: 1.0
 */
public interface UserService {
    User save(User user);
    void delete(User user);
    Iterable<User> getAll();
}

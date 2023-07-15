package scl.demo.qrcodelogin.utils;


import org.springframework.stereotype.Component;
import scl.demo.qrcodelogin.entity.User;

@Component
public class HostHolderUtil {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}

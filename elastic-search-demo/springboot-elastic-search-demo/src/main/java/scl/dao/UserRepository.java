package scl.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import scl.pojo.User;

/**
 * @projectName: learn-projects-demo
 * @package: scl.dao
 * @className: UserRepository
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/9/3 23:34
 * @version: 1.0
 */
@Repository
public interface UserRepository extends ElasticsearchRepository<User, String> {
}
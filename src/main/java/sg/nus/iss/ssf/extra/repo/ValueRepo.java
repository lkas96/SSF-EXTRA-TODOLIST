package sg.nus.iss.ssf.extra.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.nus.iss.ssf.extra.constant.Constant;

@Repository
public class ValueRepo {
    
    // slide 20

    @Autowired
    @Qualifier(Constant.template01)
    RedisTemplate<String, String> template;

    // slide 24 - create/update a value 
    public void createValue(String key, String value) {
        template.opsForValue().set(key, value);

        // setIfPresent
        // setIfAbsent
    }

    // slide 25 -retrieve a value
    public String getValue(String key) {
        return template.opsForValue().get(key);
    }

    // slide 27 - delete
    public Boolean deleteValue(String key) {
        return template.delete(key);
    }

    // slide 26 - only works for key with integer value
    public void incrementValue(String key) {
        template.opsForValue().increment(key);
    }

    public void decrementValue(String key) {
        template.opsForValue().decrement(key);
    }

    public void incrementByValue(String key, Integer value) {
        template.opsForValue().increment(key, value);
    }

    public void decrementByValue(String key, Integer value) {
        template.opsForValue().decrement(key, value);
    }

    // slide 28
    public Boolean checkExists(String key) {
        return template.hasKey(key);
    }
}

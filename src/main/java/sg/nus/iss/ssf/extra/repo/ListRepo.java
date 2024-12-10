package sg.nus.iss.ssf.extra.repo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.nus.iss.ssf.extra.constant.Constant;

@Repository
public class ListRepo {

    @Autowired
    @Qualifier(Constant.template01)
    RedisTemplate<String, String> template;

    // slide 30, slide 34
    public void leftPush(String key, String value) {
        template.opsForList().leftPush(key, value);
    }
    
    public void rightPush(String key, String value) {
        template.opsForList().rightPush(key, value);
    }

    // slide 30
    public void leftPop(String key) {
        template.opsForList().leftPop(key, 1);
    }

    // slide 32
    public String get(String key, Integer index) {
        return template.opsForList().index(key, index).toString();
    }

    // slide 33
    public Long size(String key) {
        return template.opsForList().size(key);
    }

    public List<String> getList(String key) {
        List<String> list = template.opsForList().range(key, 0, -1);

        return list;
    }

    // // public Boolean deleteItem(String key, Person valueToDelete) {
    //     Boolean isDeleted = false;

    //     List<String> retrievedList = template.opsForList().range(key, 0, -1);

    //     String [] splitData = valueToDelete.toString().split(",");

    //     Optional<String> tempString = retrievedList.stream().filter(p -> p.toString().contains(splitData[2])).findFirst();

    //     int iFound = -1;
    //     if (tempString.isPresent()) {
    //         for(int i = 0;  i < retrievedList.size(); i++) {
    //             if (retrievedList.get(i).contains(splitData[2])) {
    //                 iFound = i;
    //                 break;
    //             }
    //         }
    //     }

    //     String data = template.opsForList().index(Util.keyPerson, iFound);

    //     System.out.println("valueToDelete: " + valueToDelete.toString());
    //     System.out.println("tempString: " + tempString);
    //     System.out.println("tempString: " + tempString.toString());


    //     if (iFound >= 0) {
    //         template.opsForList().remove(key, 1, data);
    //         isDeleted = true;
    //     }

    //     return isDeleted;
    // }
}

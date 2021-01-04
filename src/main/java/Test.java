import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.util.*;

public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

    }

    private int n1 = 1, n2 = 2;

//    void toMap() throws NoSuchFieldException, IllegalAccessException {
//        Field[] fields = this.getClass().getDeclaredFields();
//        HashMap<String, Object> map = new HashMap();
//        for (Field field : fields) {
//            Object value = this.getClass().getDeclaredField(field.getName()).get(this);
//            map.put(field.getName(), value);
//        }
//        System.out.println(JSON.toJSONString(map));
//    }

    static class Father {
        int f1 = 1;

        HashMap<String, Object> toMap() throws NoSuchFieldException, IllegalAccessException {
            Class<?> clazz = this.getClass();
            HashMap<String, Object> map = new HashMap<>();
            for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    Object value = clazz.getDeclaredField(field.getName()).get(this);
                    map.put(field.getName(), value);
                }
            }
//            System.out.println(JSON.toJSONString(map));
            return map;
        }

    }
    static class Child extends Father {
        int c1 = 1;
    }
}

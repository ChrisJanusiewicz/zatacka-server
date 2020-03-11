package data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapMediator<T1, T2> {

    private ConcurrentMap<T1, T2> map;

    public MapMediator() {
        map = new ConcurrentHashMap<T1, T2>();
    }

    public T2 query(T1 key) {
        return map.get(key);
    }

    public T2 remove(T1 key) {
        return map.remove(key);
    }

    public T2 put(T1 key, T2 value) {
        return map.put(key, value);
    }


}

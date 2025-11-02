package org.bailiun.multipleversionscoexist.en;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DualMap<K> extends HashMap<K, List<String>> {

    public List<String> put(K key, String value) {
        List<String> list = this.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
        return list;
    }
}

//public class DualMap<K,V extends List<String>> extends HashMap<K,V> {
//    @SuppressWarnings("unchecked")
//    public V put(K key, String value) {
//        if(this.containsKey(key)){
//            this.get(key).add(value);
//        }else {
//                super.put(key, (V) new ArrayList<String>() {{add(value);}});
//        }
//        return this.get(key);
//    }
//}

package chatRoom;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2018/3/12.
 *
 * @author qiyun
 */
public class ChatMap<K,V>{
  public Map<K,V> map = Collections.synchronizedMap(new HashMap<>());
  //根据value来删除指定项
  public synchronized void removeByValue(Object value){
    for (Object k:map.keySet()) {
      if(map.get(k)==value){
        map.remove(k);
        break;
      }
    }
  }

  //获取所有value组成的set集合
  public synchronized Set<V> valueSet() {
    Set<V> result = new HashSet<V>();
  //将所有map中的value添加到set中
    map.forEach((Key,Value) -> result.add(Value));
    return result;
  }

  //根据value查找Key
  public synchronized K getKeyByVaule(V val){
    for (K k:map.keySet()) {
      if(map.get(k).equals(val)||map.get(k)==val){
        return k;
      }
    }
    return  null;
  }

  //实现put方法，该方法不允许value重复
  public synchronized V put(K k,V val){
    for (V v:valueSet()) {
      if(val.equals(v)&&val.hashCode()==v.hashCode()){
        throw new RuntimeException("MyMap实例中不允许有重复Value");
      }
    }
    return map.put(k,val);
  }

}

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
  //����value��ɾ��ָ����
  public synchronized void removeByValue(Object value){
    for (Object k:map.keySet()) {
      if(map.get(k)==value){
        map.remove(k);
        break;
      }
    }
  }

  //��ȡ����value��ɵ�set����
  public synchronized Set<V> valueSet() {
    Set<V> result = new HashSet<V>();
  //������map�е�value��ӵ�set��
    map.forEach((Key,Value) -> result.add(Value));
    return result;
  }

  //����value����Key
  public synchronized K getKeyByVaule(V val){
    for (K k:map.keySet()) {
      if(map.get(k).equals(val)||map.get(k)==val){
        return k;
      }
    }
    return  null;
  }

  //ʵ��put�������÷���������value�ظ�
  public synchronized V put(K k,V val){
    for (V v:valueSet()) {
      if(val.equals(v)&&val.hashCode()==v.hashCode()){
        throw new RuntimeException("MyMapʵ���в��������ظ�Value");
      }
    }
    return map.put(k,val);
  }

}

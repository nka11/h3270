package org.h3270.render;

import java.util.*;

/**
 * A Map that maps primitive integers to Objects.
 *  
 * @author Andre Spiegel spiegel@gnu.org
 * @version $Id$
 */
public class IntMap {

  private int[] keys = new int[100];
  private int maxIndex = -1;
  private List values = new ArrayList();
  
  public void put (int key, Object value) {
    int index = indexOf (key);
    if (index == -1) {
      maxIndex = maxIndex + 1;
      if (maxIndex > keys.length) 
        throw new RuntimeException ("IntMap capacity exceeded");
      keys[maxIndex] = key;
      values.add (value);
    } else {
      values.set (index, value);
    }
  }
  
  public Object get (int key) {
    int index = indexOf(key);
    if (index == -1)
      return null;
    else
      return values.get(index);
  }
  
  public int indexOf (int key) {
    for (int i=0; i<keys.length; i++) {
      if (keys[i] == key) return i;
    }
    return -1;
  }
    
  
}

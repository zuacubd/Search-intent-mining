package utility;

/*************************************************************************
 *  Compilation:  javac SymbolTable.java
 *  Execution:    java SymbolTable
 *  
 *  Symbol table implementation using Java's HashMap library.
 *  If you add a key-value pair and the key is already present,
 *  the new key-value pair replaces the old one.
 *
 *  % java SymbolTable
 *  128.112.136.11
 *  208.216.181.15
 *  null
 *
 *************************************************************************/

import java.util.HashMap;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;

public class SymbolTable {
    private HashMap st = new HashMap();
    
    public void put(String key, Object value) { st.put(key, value);   }
    public Object get(String key)             { return st.get(key);   }
    public String toString()                  { return st.toString();}
    public void remove(String key) {st.remove(key);}
    // return an array contains all of the keys
    public String[] keys() {
        Set keyvalues = st.entrySet();
        String[] keys = new String[st.size()];
        Iterator it = keyvalues.iterator();
        for (int i = 0; i < st.size(); i++) {
            Map.Entry entry = (Map.Entry) it.next();
            keys[i] = (String) entry.getKey();
        }
        
        return keys;
    }


   /***********************************************************************
    * Test routine.
    **********************************************************************/
    
    /*
    public static void main(String[] args) {
        SymbolTable st = new SymbolTable();

        // insert some (key, value pairs)
        st.put("www.cs.princeton.edu", "xxx.xxx.xxx.xx");
        st.put("www.cs.princeton.edu", "128.112.136.11");
        st.put("www.princeton.edu",    "128.112.128.15");
        st.put("www.yale.edu",         "130.132.143.21");
        st.put("www.amazon.com",       "208.216.181.15");
        st.put("www.simpsons.com",     "209.052.165.60");

        System.out.println(st);

        // search for IP addresses given URL
        //System.out.println(st.get("www.cs.princeton.edu"));
        //System.out.println(st.get("www.amazon.com"));
        //System.out.println(st.get("www.amazon.edu"));

    }
	*/
}

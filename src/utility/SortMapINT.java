package utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import utilityStemmer.Pair;

public class SortMapINT {

	private Vector vector = new Vector();
	private Pair pair;

	public SortMapINT() {
	}
	
	public void configure(Map data) {
		
		int n = data.size();
		int i = 0;
		String key;
		Integer value;
		this.vector.clear();
		for(Object id:data.keySet()) {
			key = id.toString();
			value = (Integer) data.get(id);
			this.pair = new Pair(key, value);
			this.vector.add(this.pair);
		}
	}
	
	public Map getTopkeyValue(Map data, int k) {
		
		this.configure(data);
//		this.sortData();
		this.sortVectorizedData();
		
		Map topK = new HashMap();
		Pair pair;
		
		int N = this.vector.size();
		
		if(k>N) k = N;
		
		for(int i = 0; i<k; i++) {
			pair = (Pair) this.vector.get(i);
			topK.put(pair.first(), pair.second());
		}
		
		return topK;
	}
	
	public Vector getTopKKey(Map data, int k) {
		
		this.configure(data);
//		this.sortData();
		this.sortVectorizedData();
		
		Vector topK = new Vector();
		Pair pair;
		int N = this.vector.size();

		if(k>N) k = N;
		
		for(int i = 0; i<k; i++) {
			pair = (Pair) this.vector.get(i);
			topK.add(pair.first());
		}
		
		return topK;
	}
	
	@SuppressWarnings("unchecked")
	public void sortVectorizedData() {
		
		Collections.sort(this.vector, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
	
				Pair pair1 = (Pair) o1;
				Pair pair2 = (Pair) o2;
				
				Integer x1 =  (Integer) pair1.second();
				Integer x2 =  (Integer) pair2.second();
				int d = x1.compareTo(x2);
				
		        if(d>0) return -1;
		        else if(d<0) return 1; 
		        else return 0;
			}
		});
	
	}
	
	public void sortData() {
		
		int n = this.vector.size();
		Pair A;
		Pair B;
		Integer d1;
		Integer d2;
		double d;
		
		for(int i = 0; i< n-1; i++) {
			
			for(int j = 0; j<(n-i-1); j++)
			{
				A = (Pair) this.vector.get(j);
				B = (Pair) this.vector.get(j+1);
				
				d1 = (Integer) A.second();
				d2 = (Integer) B.second();
				
				d = d1.intValue() - d2.intValue();
				if(d < 0) 
				{
					this.vector.remove(j);
					this.vector.insertElementAt(B, j);
					this.vector.remove(j+1);
					this.vector.insertElementAt(A, j+1);
				}
			}
		}
	}
	
	public void showData() {
	
		int N = this.vector.size();
		Pair pair;
		
		for(int i = 0; i<N; i++) {
			pair = (Pair) this.vector.get(i);
			System.out.print(pair.first() + "="+ pair.second()+", ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
	
		Map data = new HashMap();
		
		data.put(123, new Integer(142));
		data.put(124, new Integer(234));
		data.put(135, new Integer(534));
		data.put(234, new Integer(323));
		data.put(1244, new Integer(992));
		data.put(123, new Integer(323));
		
		SortMapINT sorting = new SortMapINT();
	
		System.out.println(data);
		System.out.println(sorting.getTopkeyValue(data, 3));
		System.out.println(sorting.getTopKKey(data, 3));
	}
}

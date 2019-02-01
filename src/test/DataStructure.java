package test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class DataStructure {

	
	private void showList(List data) {
		
		int i;
		int n;
		
		n = data.size();
		
		
		
		i = 0;
		while( i<n) 
		{
			System.out.print((i) +" : "+ data.get(i));
			
			i++;
			if(i<n)
				System.out.print(", ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		DataStructure dataStructure = new DataStructure();
		
		Vector data = new Vector();
		
		data.add(3);
		data.add(4);
		data.add(5);
		data.add(10);
	
		dataStructure.showList(data);
		
		data.remove(1);
		
		dataStructure.showList(data);
		
		data.remove(2);
		dataStructure.showList(data);
		
		data.remove(0);
		dataStructure.showList(data);
		
		
	}
}

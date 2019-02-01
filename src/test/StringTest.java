package test;

public class StringTest {

	
	public static void main(String[] args) {
		
		String s1 = "Md ZIa Ullah";
		
		String s2 = "Md ZiA";
		
		System.out.println(s1.indexOf(s2));

		System.out.println(s1.toLowerCase().indexOf(s2.toLowerCase()));

		
	}
}

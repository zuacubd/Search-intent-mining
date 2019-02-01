package test;

public class KendallTau {
    // return Kendall tau distance between two permutations

	public static long distance(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array dimensions disagree");
        }
        
        int N = a.length;

        System.out.println(N);
        int[] ainv = new int[N];
        for (int i = 0; i < N; i++) 
        	ainv[a[i]] = i;

        for (int i = 0; i < N; i++)
            System.out.print(ainv[i]+" ");
        System.out.println();

        Integer[] bnew = new Integer[N];
        for (int i = 0; i < N; i++) 
        	bnew[i] = ainv[b[i]];

        for (int i = 0; i < N; i++)
            System.out.print(bnew[i]+" ");
        System.out.println();
        
        return getInvCount(bnew, N);
    }

    private static int getInvCount(Integer arr[], int n)
    {
    	int inv_count = 0;
    	int i, j;
    	
    	for(i = 0; i < n - 1; i++)
    		for(j = i+1; j < n; j++)
    			if(arr[i] > arr[j])
    				inv_count++;
    	
    	return inv_count;
    }


    // return a random permutation of size N
    public static int[] permutation(int N) {
        int[] a = new int[N];
        for (int i = 0; i < N; i++) {
            int r = (int) (Math.random() * (i + 1));
            a[i] = a[r];
            a[r] = i;
        }
        return a;
    }




    public static void main(String[] args) {

        // two random permutation of size N
//        int N = Integer.parseInt(args[0]);
    	
    	int N = 4;
//    	int[] a = KendallTau.permutation(N);
//        int[] b = KendallTau.permutation(N);

    	int[] a= {0, 1, 2, 3};
    	int[] b= {0, 2, 3, 1};
    	
        // print initial permutation
        for (int i = 0; i < N; i++)
            System.out.print(a[i]+" ");
        System.out.println();
        
        for (int i = 0; i < N; i++)
            System.out.print(b[i]+" ");
        System.out.println();

        System.out.println("inversions = " + KendallTau.distance(a, b));
    }
}
package interview;
import java.util.*;
public class allFactors {
	public static void main(String[] args) {
		allFactors t = new allFactors();
		System.out.println(t.findAllPrimeFactors(35));
	}
	
	public List<Integer> findAllFactors(int n){
		List<Integer> rlt = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>();
		for(int i = 2; i*i <= n; i++){
			if(n%i == 0){
				set.add(i);
				set.add(n/i);
			}
		}
		for(int i : set){
			rlt.add(i);
		}
		return rlt;
	}
	
	public List<Integer> findAllPrimeFactors(int n){
		List<Integer> rlt = new ArrayList<Integer>();
		Set<Integer> set = new HashSet<Integer>();
	    // Print the number of 2s that divide n
	    while (n%2 == 0)
	    {
	        set.add(2);
	        n = n/2;
	    }
	 
	    // n must be odd at this point.  So we can skip one element (Note i = i +2)
	    for (int i = 3; i*i <= n; i = i+2)
	    {
	        // While i divides n, print i and divide n
	        while (n%i == 0)
	        {
	        	set.add(i);
	            n = n/i;
	        }
	    }
	 
	    // This condition is to handle the case whien n is a prime number
	    // greater than 2
	    if (n > 2)
	    	set.add(n);
		for(int i : set){
			rlt.add(i);
		}
		return rlt;
	}
}

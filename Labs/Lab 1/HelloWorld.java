
public class HelloWorld {

	public static void main(String[] args) 
	{
		int[] nums = new int[args.length];

	      for (int i = 0; i < nums.length; i++) {
	         nums[i] = Integer.parseInt(args[i]);
	      }
    	for(int i = 1; i < nums.length; i++) {
    		int j = i;
    		while(j > 0 && nums[j] < nums[j-1]) {
    			int a = nums[j];
    			nums[j] = nums[j-1];
    			nums[j-1] = a;
    			j--;
    		}
    	}
    	for(int i = 0; i < nums.length; i++) {
    		System.out.println(nums[i]);
    	}
    }

}

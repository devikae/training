package braingame.day1;

public class TwoSum {

	public static void main(String[] args) {
		/*
		 * Input: nums = [2,7,11,15], target = 9
		 * Output: [0,1]
		 * Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
		 *
		 * */
		
		// 배열을 반환 해야 한다.
		// 배열의 인덱스 값이 나와야 한다.
		
		int[] nums = {3,3};
		
		int[] result = twoSum(nums, 6);
		
		for(int i =0; i<result.length;i++) {
			System.out.print(result[i] + " ");
		}
	
	}
	
	public static  int[] twoSum(int[] nums, int target) {
		
		int end = 0;
		int[] arr = new int[2];
			
			for(int i =0; i<nums.length;i++) {
				end = nums[i]; 
				
				for(int j = i+1; j<nums.length; j++) {
					
					end += nums[j];
					
					if(end == target) {
						
						arr[0] = i;
						arr[1] = j;
						System.out.println("i = " + i + "   j = " + j);
						
					}else {
						end -= nums[j];
					}
					
				}
				
			}
			
			return arr;
	}
	

}

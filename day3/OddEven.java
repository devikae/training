package day3;

public class OddEven {

	public static void main(String[] args) {
		/*
		[3, 4, 5, 6, 7]
		= 홀수 3개, 짝수 2개
		[12, 16, 22, 24, 29]
		= 홀수 1개, 짝수 4개 
		[41, 43, 45, 47, 49]
		= 홀수 5개, 짝수 0개
		*/
		
		int[] arr = {3,4,5,6,7};
		OddEven(arr);
		
	}
	
	public static void OddEven(int[] arr) {
		int even = 0;
		int odd = 0;
		
		for(int i = 0; i<arr.length; i++) {
			
			if(arr[i] % 2 == 0) {
				even += 1;
			}else {
				odd += 1;
			}
			
		} // for
		
		System.out.println("Odd = " + odd + " Even = " + even);
		
		
	}
	

}

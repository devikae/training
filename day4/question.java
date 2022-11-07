package day4;

import java.util.HashSet;

public class question {

	public static void main(String[] args) {
		// 로또 번호 생성기
		// 1~45 까지의 랜덤 숫자 6개를 배열에 중복되지 않도록 담고, 이를 정렬한 배열을 리턴하는 makeLotto() 함수를 만들어주세요.
		// 였지만 재귀함수를 사용해보자 
		
		int[] temp = new int[6];
		
		int[] myLotto = makeLotto(0, temp);
		
		for(int i = 0; i< myLotto.length; i ++) {
			System.out.print(myLotto[i] + " ");
		}
		// nums = (int) (Math.random() * 43 + 1);
	}
	public static int[] makeLotto(int i, int[] arr) {
		int nums;
		
 		while(true) {
			
			nums = (int) (Math.random() * 43 + 1);
			
			for(int j = 0; j<arr.length;j++) {
				
				if(arr[j] == nums) {
					continue;
				}else {
					arr[i] = nums;
				}
				
			}
			
			if(i == 5) {
				return arr;
			}
			
			i++;
			
			makeLotto(i, arr);
			
		}
 		
		
	}

}

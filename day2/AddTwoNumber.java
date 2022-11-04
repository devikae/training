package day2;

import java.util.ArrayList;

public class AddTwoNumber {

	public static void main(String[] args) {
		/**
		 * 입력: l1 = [2,4,3], l2 = [5,6,4]
		 * 출력: [7,0,8]
		 * 설명: 342 + 465 = 807.
		 * 
		 * 입력: l1 = [0], l2 = [0]
		 * 출력: [0]
		 * 
		 */
		
		
		int[] nums1 = {2,4,3};
		int[] nums2 = {5,6,4};
		
		ArrayList<Integer> list = AddTwoNum(nums1, nums2);
		
		System.out.println(list);
		
		
		
	}
	
	public static ArrayList<Integer> AddTwoNum(int[] list1, int[] list2) {
		ArrayList<Integer> list = new ArrayList<>();
		
		for(int i= 0; i<list1.length; i++) {
			
			int add = list1[i] + list2[i]; // 각 인덱스에 맞춰 더한다
			
			
			if(add >= 10) { // 만약 더한 값이 10을 넘거나 10과 같다면?
				add -= 10; // 10을 빼준다.
				
				if(list.get(0) == null) { // 리스트 0번째 의 값이 null 이라면?
					list.add(add); // 값을 추가 해준다.
				}else { // 첫번째 값이 널이 아닐때 즉, 인덱스의 합이 10이 넘고 리스트에 첫번째 값이 들어 있을 때 
					
					// 앞의 인덱스를 꺼내 +1을 해준 후 add를 담는다. 
					list.set(i-1, list.get(i-1)+1);
					list.add(add);
				}
			}else {
				list.add(add);
			}
			
			
			
			
		}
		
		
		
		return list;
		
	}

}

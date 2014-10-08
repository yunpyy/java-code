package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 需求:将一个无序数组按照从小到大的顺序排列
 * 方法：选择排序
 * 实现:选定一个元素，依次和后面的元素进行相比较；如果选定的元素大于后面比较的元素，就交换位置
 * 步骤:
 *      ①:第一轮比较完之后，最小值出现在第0角标位置
 *      ②:第二轮比较完之后，第二最小值出现在第1角标位置
 *      依次类推...
 */
public class SelectionSort {
	public static void select(int[] arr){
		for(int i=0; i<arr.length; i++){
			for(int j=i+1; j<arr.length; j++){
				if(arr[i] > arr[j]){
					int temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}
	}
	
	public static void print(int[] arr){
		for(int i : arr){
			System.out.println(i + "");
		}
	}
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("请输入数组的长度(n):");
		try {
			String n = br.readLine();
			int length = Integer.parseInt(n);
			int[] arr = new int[length];
			System.out.println("请输入要排序的整数:");
			for(int i=0; i<arr.length; i++){
				n = br.readLine();
				arr[i] = Integer.parseInt(n);
			}
			System.out.println("排序前的数组:");
			print(arr);
			select(arr);
			System.out.println("排序后的数组:");
			print(arr);
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

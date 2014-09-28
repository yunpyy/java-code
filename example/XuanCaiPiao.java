package example;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
public class XuanCaiPiao{
    public static void main(String[] args) {
        XuanCaiPiao xuan = new XuanCaiPiao();
        xuan.printHao("红球",33, 6);
        xuan.printHao("蓝球",16, 1);
    }
  
    private boolean xuan(int max,Set<Integer> set) {
        int temp = (int) (Math.random() * max + 1);
        return set.add(temp);
    }
    private void printHao(String color,int max,int num){
        Set<Integer> set = new HashSet<Integer>();
        for(int i=0;i<num;){
                if (xuan(max,set)) {
                    i++;
                }
            }    
        System.out.print(color+"：");
         
        Integer[] ia = (Integer[])set.toArray(new Integer[0]);
        Arrays.sort(ia);
         
        for(Integer hao:ia){
            System.out.print(hao+" ");
        }
         
    }
 }
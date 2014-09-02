package example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Puzzle {
    /*
     * 贝克、库伯、弗莱舍、米勒和斯麦尔住在一个五层公寓楼的不同层，
     * 贝克不住在顶层，库伯不住在底层，弗莱舍不住在顶层也不住在底层。
     * 米勒住的比库伯高，斯麦尔不住在弗莱舍相邻的层，弗莱舍不住在库伯相邻的层。
     * 请问他们各住在哪层？
     */
    //生成随机楼层
    public static List<Integer> getRandom() {
        List<Integer> list = new ArrayList<Integer>();
        Random rand = new Random();
        boolean[] bool = new boolean[6];
        int randInt = 0;
        for (int i = 0; i < 5; i++) {
            do {
                randInt = rand.nextInt(6);
            } while (bool[randInt] || randInt == 0);
            bool[randInt] = true;
            list.add(randInt);
        }
        return list;
    }
    
    public void showResult() {
        String[] person = {"贝克 ","库伯 ","弗莱舍","米勒","斯麦尔"};
        Integer[] array = new Integer[5];
        while(true){
            List<Integer> list=getRandom();
            array = (Integer[])list.toArray(new Integer[5]);
            if(     array[0]!=5                 //贝克不住在顶层
                    &&array[1]!=1               //库伯不住在底层
                    &&array[2]!=1&&array[2]!=5  //弗莱舍不住在顶层也不住在底层
                    &&(array[3]>array[1])        //米勒住的比库伯高
                    &&(array[4]-array[2]!=1)&&(array[4]-array[2]!=-1)   //斯麦尔不住在弗莱舍相邻的层
                    &&(array[2]-array[1]!=1)&&(array[2]-array[1]!=-1)){ //弗莱舍不住在库伯相邻的层
                break;//查找结束
            }
        }
        for(int i=0;i<5;i++){
            System.out.println(person[i]+":\t"+array[i]+"层");
        }
    }
    
    public static void main(String[] args) {
        new Puzzle().showResult();
    }
}

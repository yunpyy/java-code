package example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Puzzle {
    /*
     * ���ˡ��Ⲯ�������ᡢ���պ�˹���ס��һ����㹫Ԣ¥�Ĳ�ͬ�㣬
     * ���˲�ס�ڶ��㣬�Ⲯ��ס�ڵײ㣬�����᲻ס�ڶ���Ҳ��ס�ڵײ㡣
     * ����ס�ıȿⲮ�ߣ�˹�����ס�ڸ��������ڵĲ㣬�����᲻ס�ڿⲮ���ڵĲ㡣
     * �������Ǹ�ס���Ĳ㣿
     */
    //�������¥��
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
        String[] person = {"���� ","�Ⲯ ","������","����","˹���"};
        Integer[] array = new Integer[5];
        while(true){
            List<Integer> list=getRandom();
            array = (Integer[])list.toArray(new Integer[5]);
            if(     array[0]!=5                 //���˲�ס�ڶ���
                    &&array[1]!=1               //�Ⲯ��ס�ڵײ�
                    &&array[2]!=1&&array[2]!=5  //�����᲻ס�ڶ���Ҳ��ס�ڵײ�
                    &&(array[3]>array[1])        //����ס�ıȿⲮ��
                    &&(array[4]-array[2]!=1)&&(array[4]-array[2]!=-1)   //˹�����ס�ڸ��������ڵĲ�
                    &&(array[2]-array[1]!=1)&&(array[2]-array[1]!=-1)){ //�����᲻ס�ڿⲮ���ڵĲ�
                break;//���ҽ���
            }
        }
        for(int i=0;i<5;i++){
            System.out.println(person[i]+":\t"+array[i]+"��");
        }
    }
    
    public static void main(String[] args) {
        new Puzzle().showResult();
    }
}

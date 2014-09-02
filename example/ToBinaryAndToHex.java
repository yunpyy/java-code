package example;

public class ToBinaryAndToHex {
	 
    public static void main(String[] args) {
        System.out.println(toBinary(11));
        System.out.println(toHex(559));
        System.out.println(Integer.toBinaryString(11));
        System.out.println(Integer.toHexString(559));
      /*1001
        22F
        1001
        22f*/
    }
     
    public static String toBinary(int n){
        StringBuilder temp = new StringBuilder();
        while(n/2 >=1 || n%2 == 1){//除2取余， (|| n%2 == 1) 在次为了补上最后一个0或者1
            temp.append(n%2);
            n = n/2;
        }
        return temp.reverse().toString();
    }
    /**
     10进制转16进制：
        将给定的十进制整数除以基数16，余数便是等值的16进制的最低位。 
        将上一步的商再除以基数16，余数便是等值的16进制数的次低位。 
        重复上一步骤，直到最后所得的商等于0为止。各次除得的余数，便是16进制各位的数，最后一次的余数是最高位
     */
    public static String toHex(int n){
        /*思路：除16取余*/       
        StringBuilder temp = new StringBuilder();
        while(n/16 >= 1){
            int aa = n/16;
            int bb = n%16;
            //0123456789 10 11 12 13 14 15
            //0123456789 A  B  C  D  E  F
            String str = "";
            if(bb == 10){
                str = "A";
            }else if(bb == 11){
                str = "B";
            }else if(bb == 12){
                str = "C";
            }else if(bb == 13){
                str = "D";
            }else if(bb == 14){
                str = "E";
            }else if(bb == 15){
                str = "F";
            }else{
                str = bb+"";
            }
            temp.append(str);
            n = aa;
            if(n/16 < 1){//补上最后一位
                temp.append(n);
            }
        }
        return temp.reverse().toString();
    }
}

package example.rpg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
 
//三个人物的基类
abstract class Person {
    int val;             //生命值
    double coldTime;     //冷却时间
    int waitTime;        //晕眩时间
    int fight;          //攻击力
    int chanceHit;      //发起主动技能的概率
    int chanceDefense;  //发起防御技能的概率
 
    abstract void hit(Person p);   //攻击技能
 
    abstract int defense(Person p); //防御技能，返回被伤害点数
}
 
class DH extends Person {
    public DH() {
        val = 600;
        coldTime = 1.0;
        fight = 30;
        chanceHit = 3;      //表示30%的概率
        chanceDefense = 3;
        waitTime = 0;
    }
 
    Random rand = new Random();
    boolean hitFlag = false;      //主动技能发动的标识
    boolean defenseFlag = false;  //防御技能发动的标识
 
    public void hit(Person p) {
        if (rand.nextInt(10) < chanceHit) {  //发动主动技能
            int hurt = p.defense(this);
            p.val = p.val - hurt;
            if (p.val <= 0) {
                System.out.println(this.getClass().getSimpleName() + "胜出!");
                System.exit(0);
            }
            val = val + hurt;
            if (val > 600)
                val = 600;
            hitFlag = true;     //标记主动技能已经发动
        } else {  //进行普通攻击
            int hurt = p.defense(this);
            p.val = p.val - hurt;
            if (p.val <= 0) {
                System.out.println(this.getClass().getSimpleName() + "胜出!");
                System.exit(0);
            }
        }
        System.out.println(this.getClass().getSimpleName() + "攻击"
                + p.getClass().getSimpleName() + ","
                + this.getClass().getSimpleName()
                + (this.hitFlag ? "发动攻击技能   " : "未发动攻击技能   ")
                + p.getClass().getSimpleName()
                + (this.defenseFlag ? "发动防御技能   " : "未发动防御技能   ")
                + this.getClass().getSimpleName() + ":" + this.val + ","
                + p.getClass().getSimpleName() + ":" + p.val);
        hitFlag = false;     //
        defenseFlag = false; //重置标记，下次重用
    }
 
    public int defense(Person p) {
        if (rand.nextInt(10) < chanceDefense) {
            defenseFlag = true;   //标记防御技能已经发动
            return 0;
        } else {
            return p.fight;
        }
    }
}

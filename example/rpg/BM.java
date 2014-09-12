package example.rpg;

import java.util.Random;

class BM extends Person {
    public BM() {
        val = 650;
        coldTime = 1.5;
        fight = 40;
        chanceHit = 3;
        chanceDefense = 3;
        waitTime = 0;
    }
 
    int count = 0;   //防御技能发动的次数
    int temp = 40;   //攻击力，值同fight
    boolean hitFlag = false;
    boolean defenseFlag = false;
    Random rand = new Random();
 
    public void hit(Person p) {
        if (rand.nextInt(10) < chanceHit) {
            fight = fight * 2;   //发动双倍攻击
            hitFlag = true;
        }
        int hurt = p.defense(this);
        p.val = p.val - hurt;
        fight = temp;     //还原为单倍攻击
        if (p.val <= 0) {
            System.out.println(this.getClass().getSimpleName() + "胜出!");
            System.exit(0);
        }
        System.out.println(this.getClass().getSimpleName() + "攻击"
                + p.getClass().getSimpleName() + ","
                + this.getClass().getSimpleName()
                + (this.hitFlag ? "发动攻击技能   " : "未发动攻击技能   ")
                + p.getClass().getSimpleName()
                + (this.defenseFlag ? "发动防御技能   " : "未发动防御技能   ")
                + this.getClass().getSimpleName() + ":" + this.val + ","
                + p.getClass().getSimpleName() + ":" + p.val);
        hitFlag = false;
        defenseFlag = false;
    }
 
    public int defense(Person p) {
        if (rand.nextInt(10) < chanceDefense) {
            if (count != 0) {
                p.val = p.val - p.fight;
                count++;
                defenseFlag = true;
                if (p.val <= 0) {
                    System.out.println(this.getClass().getSimpleName() + "胜出!");
                    System.exit(0);
                }
            }
        }
        return p.fight;
    }
}
 
class MK extends Person {
    public MK() {
        val = 700;
        coldTime = 2.5;
        fight = 50;
        chanceDefense = 6;
        chanceHit = 3;
        waitTime = 0;
    }
 
    boolean hitFlag = false;
    boolean defenseFlag = false;
    Random rand = new Random();
 
    public void hit(Person p) {
        if (rand.nextInt(10) < chanceHit) {
            p.waitTime = 3;   //使对方晕眩3s
            hitFlag = true;
        }
        int hurt = p.defense(this);
        p.val = p.val - hurt;
        if (p.val <= 0) {
            System.out.println(this.getClass().getSimpleName() + "胜出!");
            System.exit(0);
        }
        System.out.println(this.getClass().getSimpleName() + "攻击"
                + p.getClass().getSimpleName() + ","
                + this.getClass().getSimpleName()
                + (this.hitFlag ? "发动攻击技能   " : "未发动攻击技能   ")
                + p.getClass().getSimpleName()
                + (this.defenseFlag ? "发动防御技能   " : "未发动防御技能   ")
                + this.getClass().getSimpleName() + ":" + this.val + ","
                + p.getClass().getSimpleName() + ":" + p.val);
        hitFlag = false;
        defenseFlag = false;
    }
 
    public int defense(Person p) {
        if (rand.nextInt(10) < chanceDefense) {
            defenseFlag = true;
            return p.fight / 2;   //防御技能发动，伤害减半
        }
        return p.fight;
    }
}
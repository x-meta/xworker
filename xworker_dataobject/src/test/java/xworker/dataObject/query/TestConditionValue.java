package xworker.dataObject.query;

import org.xmeta.World;

public class TestConditionValue {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker");

            Condition condition = new Condition();
            condition.eq("abc", "");
            condition.eq("abc", "123");

            System.out.println(condition.toSql());
            System.out.println(condition.getConditionValueList());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

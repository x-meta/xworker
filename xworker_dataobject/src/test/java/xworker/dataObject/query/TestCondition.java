package xworker.dataObject.query;

import org.xmeta.World;
import xworker.lang.text.JsonFormator;

import java.util.List;

public class TestCondition {
    public static void print(Condition condition){
        System.out.println("-------------------------------");
        System.out.println(condition.toSql());
        List<ConditionValue> values = condition.getConditionValueList();
        System.out.println("value size: " + values.size());
        int index = 1;
        for(Object value : condition.getConditionValueList()){
            System.out.println("    value" + index + ": " + value);
            index++;
        }

        try {
            System.out.println(JsonFormator.format(condition.getConditionValues()));
        }catch(Exception e){
        }
        System.out.println(condition.getConditionValues());
        System.out.println(condition.getConditionThing().toXML());
        System.out.println();
    }

    public static void main(String args[]){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            Condition condition = new Condition();
            condition.eq("name", "test").and().eq("sex", "male").oreq("sex", "fmale");
            print(condition);

            condition = new Condition();
            condition.eq("name", "test").and().sql("sex", "(sex=? or sex=?)", "male", "fmale");
            print(condition);

            condition = new Condition();
            condition.eq("name", null).and().eq("sex", null).oreq("sex", "fmale");
            print(condition);

            condition = new Condition();
            condition.in("name", "a", "b", "c").and().eq("sex", null).oreq("sex", "fmale");
            print(condition);

            condition = new Condition();
            condition.in("name", "a", "b", "c").and().eq("sex", null).oreq("sex", "fmale");
            print(condition);

            condition = new Condition();
            condition.in("name", "a", "b", "c").and().eq("sex", null).oreq("sex", "fmale");
            condition.sql("test", "(name='test' and sex='male')");
            condition.orsql("test", "(name=? and sex=?)", "test", "male");
            print(condition);

            condition = new Condition();
            condition.in("name", "a", "b", "c").and().eq("sex", null).oreq("sex", "fmale");
            condition.clause("name", Condition.in, "select id from user").eq("name", "test");
            print(condition);

            condition = new Condition();
            condition.in("name", "a", "b", "c").and().eq("sex", null).oreq("sex", "fmale");
            condition.orclauseTemplate("name", Condition.in, "select id from user where %%SQL%% t").eq("name", "test");
            print(condition);

            condition = new Condition();
            condition.in("name", "a", "b", "c").and().eq("sex", null).oreq("sex", "fmale");
            condition.orclauseTemplate("name", Condition.in, "select id from user where %%SQL%% t").eq("name", null);
            print(condition);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}


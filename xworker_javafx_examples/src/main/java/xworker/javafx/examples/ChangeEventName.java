package xworker.javafx.examples;

import org.xmeta.Thing;
import org.xmeta.World;

import java.io.File;
import java.util.Iterator;

/**
 * 临时用，因为修改了EventHandler模型，因此需要修改相关实例。
 */
public class ChangeEventName {
    public static void main(String args[]){
        try{
            World world = World.getInstance();
            world.init("/xworker/");
            world.addFileThingManager("xworker_javafx", new File("./xworker_javafx/src/main/resources/"), false,true);
            world.addFileThingManager("xworker_javafx_examples", new File("./xworker_javafx_examples/src/main/resources/"), false,true);
            world.addFileThingManager("xworker_local", new File("./xworker_explorer\\xworker\\projects\\_local\\things\\"), false,true);

            checkThingManager("xworker_javafx");
            checkThingManager("xworker_javafx_examples");
            checkThingManager("xworker_local");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void checkThingManager(String name){
        Iterator<Thing> iter = World.getInstance().getThingManager(name).iterator("", true);
        while(iter.hasNext()){
            Thing thing = iter.next();
            checkThing(thing);
        }
    }

    public static void checkThing(Thing thing){
        if(thing.getThingName().indexOf("EventHandler") != -1) {
            String type = thing.getStringBlankAsNull("type");
            if(type != null){
                thing.set("name", type);
                thing.save();

                System.out.println(thing.getMetadata().getPath() + ":" + thing.getString("type"));
            }
        }

        for(Thing child : thing.getChilds()){
            checkThing(child);
        }
    }
}

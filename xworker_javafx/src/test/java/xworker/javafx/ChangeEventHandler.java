package xworker.javafx;

import org.xmeta.Thing;
import org.xmeta.World;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class ChangeEventHandler {
    public static void main(String args[]){
        try{
            World world = World.getInstance();
            world.init("/xworker/");
            world.addFileThingManager("xworker_javafx", new File("./xworker_javafx/src/main/resources/"), false,true);

            Iterator<Thing> iter = world.getThingManager("xworker_javafx").iterator("", true);
            Thing eventHandler = world.getThing("xworker.javafx.event.EventHandler");
            while(iter.hasNext()){
                Thing thing = iter.next();
                checkThing(thing);
                //System.out.println("Check: " + thing.getMetadata().getPath());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void checkThing(Thing thing){
        if(thing.getMetadata().getName().indexOf("EventHandler") != -1) {
            System.out.println(thing.getMetadata().getPath());
        }

        for(Thing child : thing.getChilds()){
            checkThing(child);
        }
    }
}

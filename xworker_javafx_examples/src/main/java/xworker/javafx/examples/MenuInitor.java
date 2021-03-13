package xworker.javafx.examples;

import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;

import java.io.File;
import java.util.Iterator;

public class MenuInitor {
    public static void check(Thing menu, Thing example){
        String path = example.getMetadata().getPath();
        path = path.substring("xworker.javafx.examples.".length(), path.length());
        String paths[] = path.split("[.]");

        Thing parent = menu;
        for(int i=0; i<paths.length - 1; i++){
            //父目录
            String category = paths[i];
            boolean ok = false;
            for(Thing child : parent.getChilds()){
                if(child.getMetadata().getName().equals(category) && child.getStringBlankAsNull("extends") == null){
                    parent = child;
                    ok = true;
                    break;
                }
            }

            if(!ok){
                Thing node = new Thing("xworker.lang.MetaDescriptor3/@thing");
                node.set("name", category);
                parent.addChild(node);

                parent = node;
            }
        }

        //查看是否已经存在
        boolean exists = false;
        for(Thing child : parent.getChilds()){
            if(child.getString("extends").equals(example.getMetadata().getPath())){
                exists = true;
                break;
            }
        }
        if(!exists){
            Thing node = new Thing("xworker.lang.MetaDescriptor3/@thing");
            node.set("name", paths[paths.length - 1]);
            node.set("extends", example.getMetadata().getPath());
            parent.addChild(node);
        }
    }

    public static void main(String[] args){
        try {
            World world = World.getInstance();
            world.init("./xworker/");
            world.addFileThingManager("xworker_javafx_examples", new File("./src/main/resources"), false, true);

            Thing menuThing = world.getThing("xworker.javafx.examples.JavaFXExamlesMenu");
            ThingManager thingManager = world.getThingManager("xworker_javafx_examples");
            Iterator<Thing> iter =  thingManager.iterator("xworker.javafx.examples", true);
            while(iter.hasNext()){
                Thing thing = iter.next();
                check(menuThing, thing);
                System.out.println(thing.getMetadata().getPath());
            }

            menuThing.save();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

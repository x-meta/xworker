package xworker.javafx;

import org.xmeta.Thing;
import org.xmeta.World;

import java.io.File;
import java.util.Iterator;

public class InitIcons {
    public static void main(String args[]){
        try{
            World world = World.getInstance();
            world.init("/xworker/");
            world.addFileThingManager("xworker_javafx", new File("./xworker_javafx/src/main/resources/"), false,true);

            String iconPath = "/com/oracle/javafx/scenebuilder/kit/editor/images/nodeicons/";
            String iconFileRoot = "./xworker_javafx/src/main/resources";
            Iterator<Thing> iter = world.getThingManager("xworker_javafx").iterator("", true);
            while(iter.hasNext()){
                Thing thing = iter.next();
                File file = new File(iconFileRoot + iconPath + thing.getMetadata().getName() + ".png");
                if(file.exists()) {
                    System.out.println(file.getAbsolutePath());
                    System.out.println(thing.getMetadata().getPath());
                    thing.set("icon", iconPath + thing.getMetadata().getName() + ".png");
                    thing.save();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

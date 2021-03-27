package xworker.javafx.thingeditor;

import org.xmeta.World;
import xworker.io.SystemIoRedirector;

import java.io.File;

public class TestSimpleThingEditor {

    public static void main(String[] args){
        try {
            SystemIoRedirector.init();

            World world = World.getInstance();
            world.init("./xworker/");

            File dir = new File(".");
            for(File file : dir.listFiles()){
                File resoures = new File(file, "src/main/resources");
                if(resoures.exists()){
                    world.addFileThingManager(file.getName(), resoures, false, false);
                }
            }
            SimpleThingEditor.run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

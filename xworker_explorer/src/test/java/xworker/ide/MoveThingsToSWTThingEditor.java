package xworker.ide;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.io.SystemIoRedirector;
import xworker.util.XWorkerUtils;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveThingsToSWTThingEditor {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("../xworker_explorer/xworker/");

            Thread.currentThread().setContextClassLoader(world.getClassLoader());
            SystemIoRedirector.init();

            File dir = new File(".");
            for(File file : dir.listFiles()){
                File resoures = new File(file, "src/main/resources");
                if(resoures.exists()){
                    world.addFileThingManager(file.getName(), resoures, false, false);
                }
            }

            Thing examples = world.getThing("xworker.example.swt.SwtExamples");

            Map<String, String> context = new HashMap<>();
            for(Thing thing : examples.getChilds()){
                Thing example = world.getThing(thing.getString("path"));
                if(example == null){
                    continue;
                }
                example = example.getRoot();
                String path = example.getMetadata().getPath();
                if(context.get(path) != null){
                    continue;
                }else{
                    context.put(path, path);
                }

                Thing root = example;
                String thingManager = root.getMetadata().getThingManager().getName();
                System.out.println(thingManager + ":" + path);
                if(!"xworker_swt_thingeditor".equals(thingManager)){
                    System.out.println(path + ":" + thingManager);

                    root.remove();
                    root.saveAs("xworker_swt_thingeditor", path);
                    System.out.println("Moved");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

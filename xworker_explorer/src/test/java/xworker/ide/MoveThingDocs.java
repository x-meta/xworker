package xworker.ide;

import org.apache.commons.io.FileUtils;
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

public class MoveThingDocs {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            Thread.currentThread().setContextClassLoader(world.getClassLoader());
            SystemIoRedirector.init();

            File dir = new File("../");
            for(File file : dir.listFiles()){
                File resoures = new File(file, "src/main/resources");
                if(resoures.exists()){
                    world.addFileThingManager(file.getName(), resoures, false, false);
                }
            }

            File target = new File("../xworker_thingeditor_doc/src/main/resources");
            Thing index = world.getThing("xworker.example.ExamplesIndex");
            List<Thing> things = XWorkerUtils.searchRegistThings(index, "child", Collections.emptyList(), new ActionContext());

            Map<String, String> context = new HashMap<>();
            for(Thing thing : things){
                Thing root = thing.getRoot();
                String path = root.getMetadata().getPath();
                String thingManager = root.getMetadata().getThingManager().getName();
                if(context.get(path) != null){
                    continue;
                }else{
                    context.put(path, path);
                }

                String group = thing.getStringBlankAsNull("group");
                if(group == null || group.startsWith("dynamicModel")) {
                    System.out.println(path + ":" + thingManager);

                    if(!"xworker_thingeditor_doc".equals(thingManager)) {
                        root.remove();
                        root.saveAs("xworker_thingeditor_doc", path);
                        System.out.println("Moved");
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

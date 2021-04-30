package xworker.ide;

import org.xmeta.Thing;
import org.xmeta.World;
import xworker.io.SystemIoRedirector;

import java.io.File;

public class FXExplorer {
    public static void main(String[] args){
        try{
            //首次从git上拉下来没有databases目录，会报数据库的错误
            File dbDir = new File("./xworker/databases/");
            if(dbDir.exists() == false) {
                dbDir.mkdirs();
            }

            //初始化引擎，如果代码着色有问题，请设置color的dll的路径
            World world = World.getInstance();
            world.init("./xworker/");

            Thread.currentThread().setContextClassLoader(world.getClassLoader());
            SystemIoRedirector.init();

            File dir = new File("../");
            for(File file : dir.listFiles()){
                File resoures = new File(file, "src/main/resources");
                if(resoures.exists() && world.getThingManager(file.getName()) == null){
                    world.addFileThingManager(file.getName(), resoures, false, false);
                }
            }
            System.out.println(world.getThingManager("_local"));

            xworker.javafx.thingeditor.SimpleThingEditor.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

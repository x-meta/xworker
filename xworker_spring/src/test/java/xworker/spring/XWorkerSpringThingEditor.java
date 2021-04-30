package xworker.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.xmeta.World;
import xworker.ide.SimpleThingEditor;

import java.io.File;

@MapperScan("xworker.mybatis.mapper")
@SpringBootApplication
public class XWorkerSpringThingEditor {
    public static void main(String[] args){
        try {
            World world = World.getInstance();
            world.init("./xworker/");

            world.addFileThingManager("xworker_spring", new File("./src/main/resources"), false, true);
            world.addFileThingManager("xworker_spring_test", new File("./src/test/resources"), false, true);

            ApplicationContext applicationContext = SpringApplication.run(XWorkerSpringThingEditor.class, args);
            SpringConfig.setApplicationContext(applicationContext);

            SimpleThingEditor.run();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

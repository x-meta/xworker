package xworker.web.thingeditor;

import org.xmeta.World;

import java.io.File;

public class TestWebThingEditor {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            //添加模型管理器，即模型存放的目录，编辑模型需要。Test是模型管理器的名字，不能和其它模型管理重复。
            world.addFileThingManager("Test", new File("./src"), false, true);
           //模型可以放到Java源码目录下，模型可以和Java一起编译和打包，不过在jar中的模型一般是只读的

            XWorkerWebThingEditor.run();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

package xworker.ide;

import org.xmeta.Thing;
import org.xmeta.World;
import xworker.swt.SwtThingEditor;

public class SwtSimpleEditor {
    public static void main(String args[]){
        try{
            World world = World.getInstance();
            world.init("./xworker");

            Thing editorConfig = world.getThing("_local.test.swt.thingeditor.TestSwtThingEditorConfig");
            SwtThingEditor.run(editorConfig);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

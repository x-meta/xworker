package xworker.javafx.examples;

import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class JavafxExamples {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            Thing example = world.getThing("xworker.javafx.examples.JavaFXEamples");
            example.doAction("create", new ActionContext());
        }catch(Exception e){
            e.printStackTrace();;
        }
    }
}

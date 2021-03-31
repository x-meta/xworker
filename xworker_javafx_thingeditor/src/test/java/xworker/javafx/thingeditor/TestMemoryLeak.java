package xworker.javafx.thingeditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import xworker.javafx.thing.form.ThingForm;
import xworker.javafx.thing.form.attributeeditors.TextFieldEditor;

import java.io.File;

public class TestMemoryLeak extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);

        primaryStage.setScene(scene);
        primaryStage.show();


        Thing attribute = new Thing();

        ThingForm thingForm = new ThingForm(attribute, new ActionContext());
        //vBox.getChildren().add(thingForm.getFormNode());

        int count = 1000000;
        for(int i=0; i<count; i++){
            Thing thing = new Thing("_local.test.javafx.thing.ThingFormMemoryLeak");
            thing.put("name", "count:" + i);
            thingForm.setThing(thing);

            if(i % 100 == 0){
                System.out.println(i);
                System.gc();
            }
        }
    }

    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            world.addFileThingManager("_local", new File("./xworker/_local/"), false, false);
            Application.launch();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

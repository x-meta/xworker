package test;

import javafx.scene.control.ChoiceBox;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

import java.io.File;

public class ChoiceBoxExample {
    public static void main(String[] args){
        try{
            World world = World.getInstance();
            world.init("./xworker/");

            world.addFileThingManager("xworker_javafx_example", new File("../xworker_javafx_examples/src/main/resources/"), false, true);
            Thing thing = world.getThing("xworker.javafx.examples.scene.control.ChoiceBoxExample");
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "\n" +
                    "<ChoiceBox name=\"choiceBox\" _xmeta_id_=\"ChoiceBox\" descriptors=\"xworker.javafx.scene.Nodes/@ChoiceBox\">\n" +
                    "    <ThingItems>\n" +
                    "        <Item name=\"choice1\" label=\"Choice1\" value=\"1\"></Item>\n" +
                    "        <Item name=\"choice2\" label=\"Choice 2\" value=\"2\"></Item>\n" +
                    "        <Item name=\"choice 3\" label=\"Choice 3\" value=\"3\"></Item>\n" +
                    "    </ThingItems>\n" +
                    "    <ChoiceBoxEventHandler type=\"onAction\">\n" +
                    "        <GroovyAction name=\"GroovyAction\" descriptors=\"xworker.lang.actions.GroovyAction\" code=\"println choiceBox.getSelectionModel().getSelectedItem();\"></GroovyAction>\n" +
                    "    </ChoiceBoxEventHandler>\n" +
                    "</ChoiceBox>";
            Thing hbox = world.getThing("xworker.javafx.examples.scene.control.ChoiceBoxExample/@Nodes/@HBox");
            Thing newChoice = new Thing();
            newChoice.parseXML(xml);
            hbox.addChild(newChoice);

            ActionContext actionContext = new ActionContext();
            thing.doAction("create", actionContext);
            Thread.sleep(4000);
            ChoiceBox<?> choiceBox = actionContext.getObject("choiceBox");
            System.out.println(choiceBox);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

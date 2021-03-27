package xworker.javafx.thing.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import xworker.thingeditor.ThingMenu;

public class ThingEditorMenuEventHandler implements EventHandler<ActionEvent> {
    ThingMenu thingMenu;

    public ThingEditorMenuEventHandler(ThingMenu thingMenu){
        this.thingMenu = thingMenu;
    }

    @Override
    public void handle(ActionEvent event) {
        thingMenu.execute();
    }
}

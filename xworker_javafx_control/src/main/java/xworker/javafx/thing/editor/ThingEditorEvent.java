package xworker.javafx.thing.editor;

import javafx.event.Event;
import javafx.event.EventType;
import org.xmeta.Thing;

public class ThingEditorEvent extends Event {
    ThingEditor thingEditor;
    public ThingEditorEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}

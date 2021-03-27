package xworker.javafx.thing.editor;

import javafx.event.Event;
import javafx.event.EventType;
import org.xmeta.Index;
import org.xmeta.Thing;
import xworker.javafx.thing.model.IndexTableViewModelEvent;

public class ThingEditorEvent extends Event {
    public static final EventType<ThingEditorEvent> ONSELECTTHING = new EventType<ThingEditorEvent>(Event.ANY, "ONSELECTTHING");

    public static final EventType<ThingEditorEvent> ANY = ONSELECTTHING;

    ThingEditor thingEditor;

    Index index;
    public ThingEditorEvent(ThingEditor thingEditor) {
        super(ONSELECTTHING);

        this.thingEditor = thingEditor;
    }

    public ThingEditor getThingEditor() {
        return thingEditor;
    }
}

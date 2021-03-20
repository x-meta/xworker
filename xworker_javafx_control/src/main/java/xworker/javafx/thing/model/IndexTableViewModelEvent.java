package xworker.javafx.thing.model;

import javafx.event.Event;
import javafx.event.EventType;
import org.xmeta.Index;

public class IndexTableViewModelEvent extends Event {
    public static final EventType<IndexTableViewModelEvent> OPENINDEX = new EventType<IndexTableViewModelEvent>(Event.ANY, "OPENINDEX");

    public static final EventType<IndexTableViewModelEvent> ANY = OPENINDEX;

    Index index;
    public IndexTableViewModelEvent(Index index) {
        super(OPENINDEX);

        this.index = index;
    }

    public Index getIndex() {
        return index;
    }
}
